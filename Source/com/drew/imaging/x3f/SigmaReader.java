/*
 * Copyright 2002-2016 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.x3f;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessFileReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.x3f.SigmaPropertyDirectory;
import com.drew.metadata.x3f.SigmaPropertyKeys;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.drew.lang.RandomAccessStreamReader.DEFAULT_CHUNK_LENGTH;

/**
 * @author Anthony Mandra http://anthonymandra.com
 */
public class SigmaReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException, TiffProcessingException
    {
        Metadata metadata = new Metadata();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        try {
            process(new RandomAccessFileReader(randomAccessFile), metadata, 0);
        } finally {
            randomAccessFile.close();
        }

        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, TiffProcessingException
    {
        // TIFF processing requires random access, as directories can be scattered throughout the byte sequence.
        // InputStream does not support seeking backwards, so we wrap it with RandomAccessStreamReader, which
        // buffers data from the stream as we seek forward.

        return readMetadata(new RandomAccessStreamReader(inputStream));
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, long streamLength) throws IOException, TiffProcessingException
    {
        // TIFF processing requires random access, as directories can be scattered throughout the byte sequence.
        // InputStream does not support seeking backwards, so we wrap it with RandomAccessStreamReader, which
        // buffers data from the stream as we seek forward.

        return readMetadata(new RandomAccessStreamReader(inputStream, DEFAULT_CHUNK_LENGTH, streamLength));
    }

    @NotNull
    public static Metadata readMetadata(@NotNull RandomAccessReader reader) throws IOException, TiffProcessingException
    {
        Metadata metadata = new Metadata();
        process(reader, metadata, 0);
        return metadata;
    }

    /**
     * Processes a X3F data sequence.
     *
     * @param reader the {@link RandomAccessReader} from which the data should be read
     * @param x3fHeaderOffset the offset within <code>reader</code> at which the TIFF header starts
     * @throws TiffProcessingException if an error occurred during the processing of TIFF data that could not be
     *                                 ignored or recovered from
     * @throws IOException an error occurred while accessing the required data
     */
    public static void process(@NotNull final RandomAccessReader reader, Metadata metadata,
                           final int x3fHeaderOffset) throws IOException
    {
        reader.setMotorolaByteOrder(false); // little endian

        // For, posterity, we already identified this file "FOVb"
//        String identifier =             reader.getString(0, 4, Charsets.ASCII);

        final int versionMinor =        reader.getInt16(4 + x3fHeaderOffset);
        final int versionMajor =        reader.getInt16(6 + x3fHeaderOffset);
        final long uid =                reader.getInt64(8 + x3fHeaderOffset);
        final int mark =                reader.getInt32(24 + x3fHeaderOffset);
        final int width =               reader.getInt32(28 + x3fHeaderOffset);
        final int height =              reader.getInt32(32 + x3fHeaderOffset);
        final int rotation =            reader.getInt32(36 + x3fHeaderOffset);

        // For self-documentation
//        0 None Extended data slot is unused.
//        1 FLOAT32 Exposure adjust.
//        2 FLOAT32 Contrast adjust.
//        3 FLOAT32 Shadow adjust.
//        4 FLOAT32 Highlight adjust.
//        5 FLOAT32 Saturation adjust.
//        6 FLOAT32 Sharpness adjust.
//        7 FLOAT32 Color adjust red.
//        8 FLOAT32 Color adjust green.
//        9 FLOAT32 Color adjust blue.
//        10 FLOAT32 X3 Fill Light adjust
        if (versionMajor == 2 && versionMinor == 2 || versionMinor == 1)    //extended was only 2.1, 2.2?
        {
            final String whiteBalance = reader.getNullTerminatedString(40 + x3fHeaderOffset, 32, Charsets.ASCII);

            final byte[] extendedTypes = new byte[32];
            final float[] extendedDatums = new float[32];
            final int extendedTypeOffset = 72 + x3fHeaderOffset;
            final int extendedDatumOffset = 104 + x3fHeaderOffset;

            for (int i = 0; i < 32; i++)
            {
                extendedTypes[i] = reader.getByte(i + extendedTypeOffset);
                extendedDatums[i] = reader.getFloat32(i * 4 + extendedDatumOffset);
            }
        }
        //TODO: header directory if needed

        // Max 2G file (reader limit)
        final int directoryPosition = reader.getInt32((int)reader.getLength() - 4);
        final String directoryId = reader.getString(directoryPosition, 4, Charsets.ASCII);  // "Should be "SECd"
        final int version = reader.getInt32(4 + directoryPosition);
        final int directoryCount = reader.getInt32(8 + directoryPosition);
        DirectoryIndex[] directoryIndices = new DirectoryIndex[directoryCount];

        int indexPosition = 12 + directoryPosition;
        for (int i = 0; i < directoryCount; i++)
        {
            int directoryIndexOffset = i * 12;
            DirectoryIndex d = new DirectoryIndex();
            d.Offset = reader.getInt32(directoryIndexOffset + indexPosition);
            d.Length = reader.getInt32(4 + directoryIndexOffset + indexPosition);
            d.Type = reader.getString(8 + directoryIndexOffset + indexPosition, 4, Charsets.ASCII);
            directoryIndices[i] = d;
        }

        for (DirectoryIndex dir : directoryIndices)
        {
            if ("PROP".equals(dir.Type)) //PROP, IMAG, IMA2, CAMF, (One was garbage)
            {
                SigmaPropertyDirectory directory = new SigmaPropertyDirectory();
                String propId = reader.getString(dir.Offset, 4, Charsets.ASCII);  // "Should be "SECp"
                int propVersion = reader.getInt32(4 + dir.Offset);
                int propCount = reader.getInt32(8 + dir.Offset);
                int charFormat = reader.getInt32(12 + dir.Offset); // 0=char16 uni
//                int reserved = reader.getInt32(16 + dir.Offset); // for posterity
                int propLength = reader.getInt32(20 + dir.Offset);  // complete property chunk size in char (16bit)
                int propIndexOffset = 24 + dir.Offset;

                final List<Property> properties = new ArrayList<Property>();
                int propPairOffset = 0;
                for (int i = 0; i < propCount; i++)
                {
                    propPairOffset = i * 8 + propIndexOffset;

                    Property p = new Property();
                    p.NameOffset = reader.getInt32(propPairOffset);
                    p.ValueOffset = reader.getInt32(4 + propPairOffset);

                    properties.add(p);
                }

                int endOfPropHeader = propPairOffset + 8;
                byte[] propertyChunk = reader.getBytes(endOfPropHeader, propLength * 2);
                for (Property p : properties)
                {
                    // 16-bit null-terminated Unicode character strings
                    String name = getUtf16String(propertyChunk, endOfPropHeader + 2 * p.NameOffset);
                    String value = getUtf16String(propertyChunk, endOfPropHeader + 2 * p.ValueOffset);

                    try
                    {
                        SigmaPropertyKeys key = SigmaPropertyKeys.fromValue(name);
                        if (key == null)
                            directory.addError(key + " is unknown.");
                        else
                            directory.setString(key.getInt(), value);
                    }
                    catch(Exception e)
                    {
                        directory.addError(name + " was not recognized.");
                    }
                }
                metadata.addDirectory(directory);
            }
            else if ("IMAG".equals(dir.Type) || "IMA2".equals(dir.Type))
            {
                String imageId = reader.getString(dir.Offset, 4, Charsets.ASCII);  // "Should be "SECi"
                int imageVersion = reader.getInt32(4 + dir.Offset);
                int imageType = reader.getInt32(8 + dir.Offset);    //2 = processed for preview

                if (imageType != 2)     //TODO: Anything but 2 is raw?
                    continue;

                // We will just lazily throw this to the jpeg reader, which so far has worked perfectly.  It's possible we'd need to switch processing based on format.
                int imageFormat = reader.getInt32(12 + dir.Offset); //3 = uncompressed 24-bit 8/8/8 RGB, 11 = Huffman-encoded DPCM 8/8/8 RGB, 18 = JPEG-compressed 8/8/8 RGB
                int imageWidth = reader.getInt32(16 + dir.Offset);
                int imageHeight = reader.getInt32(20 + dir.Offset);
                int rowSize = reader.getInt32(24 + dir.Offset);  // Will always be a multiple of 4 (32-bit aligned). A value of zero here means that rows are variable-length (as in Huffman data).
                int imageStart = 28 + dir.Offset;

                byte[] image = reader.getBytes(imageStart, dir.Length - 28 /*header*/);
                ByteArrayInputStream jpegmem = new ByteArrayInputStream(image);
                try {
                    Metadata jpegDirectory = JpegMetadataReader.readMetadata(jpegmem);
                    for (Directory directory : jpegDirectory.getDirectories()) {
                        metadata.addDirectory(directory);
                    }
                } catch (JpegProcessingException e) {
//                    _currentDirectory.addError("Error processing JpgFromRaw: " + e.getMessage());
                } catch (IOException e) {
//                    _currentDirectory.addError("Error reading JpgFromRaw: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Returns the sequence of byte pairs punctuated by a <code>\0</code> value that represent a 16 bit string.
     *
     * @param index The index within the buffer at which to start reading the string.
     * the returned array will be <code>maxLengthChar</code> long (2 * maxLengthChar bytes).
     * @return UTF16 string.
     * @throws IOException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    private static String getUtf16String(byte[] buffer, int index) throws IOException
    {
        final StringBuilder str = new StringBuilder();
        final ByteArrayInputStream is = new ByteArrayInputStream(buffer, index, buffer.length - index);
        final InputStreamReader reader = new InputStreamReader(is, "UTF-16LE");
        int c;
        while ((c = reader.read()) > 0)
        {
            str.append((char) c);
        }
        return str.toString();
    }

    private static class DirectoryIndex
    {
        int Offset;
        int Length;
        String Type;
    }

    private static class Property
    {
        int NameOffset;
        int ValueOffset;
    }
}
