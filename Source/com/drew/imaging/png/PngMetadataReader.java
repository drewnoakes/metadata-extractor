/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.imaging.png;

import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.png.PngChromaticitiesDirectory;
import com.drew.metadata.png.PngDirectory;
import com.drew.metadata.xmp.XmpReader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.InflaterInputStream;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngMetadataReader
{
    private static Set<PngChunkType> _desiredChunkTypes;

    /**
     * The PNG spec states that ISO_8859_1 (Latin-1) encoding should be used for:
     * <ul>
     *   <li>"tEXt" and "zTXt" chunks, both for keys and values (https://www.w3.org/TR/PNG/#11tEXt)</li>
     *   <li>"iCCP" chunks, for the profile name (https://www.w3.org/TR/PNG/#11iCCP)</li>
     *   <li>"sPLT" chunks, for the palette name (https://www.w3.org/TR/PNG/#11sPLT)</li>
     * </ul>
     * Note that "iTXt" chunks use UTF-8 encoding (https://www.w3.org/TR/PNG/#11iTXt).
     * <p/>
     * For more guidance: http://www.w3.org/TR/PNG-Decoders.html#D.Text-chunk-processing
     */
    private static Charset _latin1Encoding = Charsets.ISO_8859_1;

    static
    {
        Set<PngChunkType> desiredChunkTypes = new HashSet<PngChunkType>();

        desiredChunkTypes.add(PngChunkType.IHDR);
        desiredChunkTypes.add(PngChunkType.PLTE);
        desiredChunkTypes.add(PngChunkType.tRNS);
        desiredChunkTypes.add(PngChunkType.cHRM);
        desiredChunkTypes.add(PngChunkType.sRGB);
        desiredChunkTypes.add(PngChunkType.gAMA);
        desiredChunkTypes.add(PngChunkType.iCCP);
        desiredChunkTypes.add(PngChunkType.bKGD);
        desiredChunkTypes.add(PngChunkType.tEXt);
        desiredChunkTypes.add(PngChunkType.zTXt);
        desiredChunkTypes.add(PngChunkType.iTXt);
        desiredChunkTypes.add(PngChunkType.tIME);
        desiredChunkTypes.add(PngChunkType.pHYs);
        desiredChunkTypes.add(PngChunkType.sBIT);

        _desiredChunkTypes = Collections.unmodifiableSet(desiredChunkTypes);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws PngProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(new RandomAccessStream(inputStream, file.length()).createReader());
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull ReaderInfo reader) throws PngProcessingException, IOException
    {
        Iterable<PngChunk> chunks = new PngChunkReader().extract(reader, _desiredChunkTypes);

        Metadata metadata = new Metadata();

        for (PngChunk chunk : chunks) {
            try {
                processChunk(metadata, chunk);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return metadata;
    }

    private static void processChunk(@NotNull Metadata metadata, @NotNull PngChunk chunk) throws PngProcessingException, IOException
    {
        PngChunkType chunkType = chunk.getType();

        if (chunkType.equals(PngChunkType.IHDR)) {
            PngHeader header = new PngHeader(chunk.getReader());
            PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
            directory.setInt(PngDirectory.TAG_IMAGE_WIDTH, header.getImageWidth());
            directory.setInt(PngDirectory.TAG_IMAGE_HEIGHT, header.getImageHeight());
            directory.setInt(PngDirectory.TAG_BITS_PER_SAMPLE, header.getBitsPerSample());
            directory.setInt(PngDirectory.TAG_COLOR_TYPE, header.getColorType().getNumericValue());
            directory.setInt(PngDirectory.TAG_COMPRESSION_TYPE, header.getCompressionType() & 0xFF); // make sure it's unsigned
            directory.setInt(PngDirectory.TAG_FILTER_METHOD, header.getFilterMethod());
            directory.setInt(PngDirectory.TAG_INTERLACE_METHOD, header.getInterlaceMethod());
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.PLTE)) {
            PngDirectory directory = new PngDirectory(PngChunkType.PLTE);
            directory.setInt(PngDirectory.TAG_PALETTE_SIZE, (int)chunk.getReader().getLength() / 3);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.tRNS)) {
            PngDirectory directory = new PngDirectory(PngChunkType.tRNS);
            directory.setInt(PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY, 1);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.sRGB)) {
            int srgbRenderingIntent = chunk.getReader().getInt8(); // bytes[0];
            PngDirectory directory = new PngDirectory(PngChunkType.sRGB);
            directory.setInt(PngDirectory.TAG_SRGB_RENDERING_INTENT, srgbRenderingIntent);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.cHRM)) {
            PngChromaticities chromaticities = new PngChromaticities(chunk.getReader());
            PngChromaticitiesDirectory directory = new PngChromaticitiesDirectory();
            directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX());
            directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_Y, chromaticities.getWhitePointY());
            directory.setInt(PngChromaticitiesDirectory.TAG_RED_X, chromaticities.getRedX());
            directory.setInt(PngChromaticitiesDirectory.TAG_RED_Y, chromaticities.getRedY());
            directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_X, chromaticities.getGreenX());
            directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_Y, chromaticities.getGreenY());
            directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_X, chromaticities.getBlueX());
            directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_Y, chromaticities.getBlueY());
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.gAMA)) {
            int gammaInt = ByteConvert.toInt32BigEndian(chunk.getReader().toArray());
            PngDirectory directory = new PngDirectory(PngChunkType.gAMA);
            directory.setDouble(PngDirectory.TAG_GAMMA, gammaInt / 100000.0);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.iCCP)) {
            ReaderInfo reader = chunk.getReader();

            // Profile Name is 1-79 bytes, followed by the 1 byte null character
            StringValue profileName = reader.getNullTerminatedStringValue(79 + 1, _latin1Encoding);
            PngDirectory directory = new PngDirectory(PngChunkType.iCCP);
            directory.setStringValue(PngDirectory.TAG_ICC_PROFILE_NAME, profileName);
            byte compressionMethod = reader.getInt8();
            // Only compression method allowed by the spec is zero: deflate
            if (compressionMethod == 0) {
                // bytes left for compressed text is:
                // total bytes length - (profilenamebytes length + null byte + compression method byte)
                int bytesLeft = (int)reader.getLength() - (profileName.getBytes().length + 1 + 1);
                
                byte[] compressedProfile = reader.getBytes(bytesLeft);

                try {
                    InflaterInputStream inflateStream = new InflaterInputStream(new ByteArrayInputStream(compressedProfile));
                    // the inflate stream is compressed so the length is unknown. Set to Integer max...
                    new IccReader().extract(new RandomAccessStream(inflateStream, Integer.MAX_VALUE).createReader(), metadata, directory);
                    inflateStream.close();
                } catch(java.util.zip.ZipException zex) {
                    directory.addError(String.format("Exception decompressing PNG iCCP chunk : %s", zex.getMessage()));
                    metadata.addDirectory(directory);
                }
            } else {
                directory.addError("Invalid compression method value");
            }
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.bKGD)) {
            byte[] bytes = chunk.getReader().toArray();
            PngDirectory directory = new PngDirectory(PngChunkType.bKGD);
            directory.setByteArray(PngDirectory.TAG_BACKGROUND_COLOR, bytes);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.tEXt)) {
            ReaderInfo reader = chunk.getReader();
            
            // Keyword is 1-79 bytes, followed by the 1 byte null character
            StringValue keywordsv = reader.getNullTerminatedStringValue(79 + 1, _latin1Encoding);
            String keyword = keywordsv.toString();

            // bytes left for text is:
            // total bytes length - (Keyword length + null byte)
            int bytesLeft = (int)reader.getLength() - (keywordsv.getBytes().length + 1);
            StringValue value = reader.getNullTerminatedStringValue(bytesLeft, _latin1Encoding);
            
            List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
            textPairs.add(new KeyValuePair(keyword, value));
            PngDirectory directory = new PngDirectory(PngChunkType.tEXt);
            directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.zTXt)) {
            ReaderInfo reader = chunk.getReader();

            // Keyword is 1-79 bytes, followed by the 1 byte null character
            StringValue keywordsv = reader.getNullTerminatedStringValue(79 + 1, _latin1Encoding);
            String keyword = keywordsv.toString();
            byte compressionMethod = reader.getInt8();

            // bytes left for compressed text is:
            // total bytes length - (Keyword length + null byte + compression method byte)
            int bytesLeft = (int)reader.getLength() - (keywordsv.getBytes().length + 1 + 1);
            byte[] textBytes = null;
            if (compressionMethod == 0) {
                try {
                    byte[] bytes = chunk.getReader().getBytes((int)chunk.getReader().getLength() - bytesLeft, bytesLeft);
                    InflaterInputStream inflateStream = new InflaterInputStream(new ByteArrayInputStream(bytes));
                    textBytes = StreamUtil.readAllBytes(inflateStream);
                    inflateStream.close();
                } catch(java.util.zip.ZipException zex) {
                    textBytes = null;
                    PngDirectory directory = new PngDirectory(PngChunkType.zTXt);
                    directory.addError(String.format("Exception decompressing PNG zTXt chunk with keyword \"%s\": %s", keyword, zex.getMessage()));
                    metadata.addDirectory(directory);
                }
            } else {
                PngDirectory directory = new PngDirectory(PngChunkType.zTXt);
                directory.addError("Invalid compression method value");
                metadata.addDirectory(directory);
            }
            if (textBytes != null) {
                if (keyword.equals("XML:com.adobe.xmp")) {
                    // NOTE in testing images, the XMP has parsed successfully, but we are not extracting tags from it as necessary
                    new XmpReader().extract(textBytes, metadata);
                } else {
                    List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
                    textPairs.add(new KeyValuePair(keyword, new StringValue(textBytes, _latin1Encoding)));
                    PngDirectory directory = new PngDirectory(PngChunkType.zTXt);
                    directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
                    metadata.addDirectory(directory);
                }
            }
        } else if (chunkType.equals(PngChunkType.iTXt)) {
            ReaderInfo reader = chunk.getReader();

            // Keyword is 1-79 bytes, followed by the 1 byte null character
            StringValue keywordsv = reader.getNullTerminatedStringValue(79, _latin1Encoding);
            String keyword = keywordsv.toString();
            byte compressionFlag = reader.getInt8();
            byte compressionMethod = reader.getInt8();
            // TODO we currently ignore languageTagBytes and translatedKeywordBytes
            byte[] languageTagBytes = reader.getNullTerminatedBytes((int)reader.getLength());
            byte[] translatedKeywordBytes = reader.getNullTerminatedBytes((int)reader.getLength());

            // bytes left for compressed text is:
            // total bytes length - (Keyword length + null byte + comp flag byte + comp method byte + lang length + null byte + translated length + null byte)
            int bytesLeft = (int)reader.getLength() - (keywordsv.getBytes().length + 1 + 1 + 1 + languageTagBytes.length + 1 + translatedKeywordBytes.length + 1);
            byte[] textBytes = null;
            if (compressionFlag == 0) {
                textBytes = reader.getNullTerminatedBytes(bytesLeft);
            } else if (compressionFlag == 1) {
                if (compressionMethod == 0) {
                    try {
                        byte[] bytes = chunk.getReader().getBytes((int)chunk.getReader().getLength() - bytesLeft, bytesLeft);
                        InflaterInputStream inflateStream = new InflaterInputStream(new ByteArrayInputStream(bytes));
                        textBytes = StreamUtil.readAllBytes(inflateStream);
                        inflateStream.close();
                    } catch(java.util.zip.ZipException zex) {
                        textBytes = null;
                        PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                        directory.addError(String.format("Exception decompressing PNG iTXt chunk with keyword \"%s\": %s", keyword, zex.getMessage()));
                        metadata.addDirectory(directory);
                    }
                } else {
                    PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                    directory.addError("Invalid compression method value");
                    metadata.addDirectory(directory);
                }
            } else {
                PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                directory.addError("Invalid compression flag value");
                metadata.addDirectory(directory);
            }

            if (textBytes != null) {
                if (keyword.equals("XML:com.adobe.xmp")) {
                    // NOTE in testing images, the XMP has parsed successfully, but we are not extracting tags from it as necessary
                    new XmpReader().extract(textBytes, metadata);
                } else {
                    List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
                    textPairs.add(new KeyValuePair(keyword, new StringValue(textBytes, _latin1Encoding)));
                    PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                    directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
                    metadata.addDirectory(directory);
                }
            }
        } else if (chunkType.equals(PngChunkType.tIME)) {
            ReaderInfo reader = chunk.getReader();
            int year = reader.getUInt16();
            int month = reader.getUInt8();
            int day = reader.getUInt8();
            int hour = reader.getUInt8();
            int minute = reader.getUInt8();
            int second = reader.getUInt8();
            PngDirectory directory = new PngDirectory(PngChunkType.tIME);
            if (DateUtil.isValidDate(year, month - 1, day) && DateUtil.isValidTime(hour, minute, second)) {
                String dateString = String.format("%04d:%02d:%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
                directory.setString(PngDirectory.TAG_LAST_MODIFICATION_TIME, dateString);
            } else {
                directory.addError(String.format(
                    "PNG tIME data describes an invalid date/time: year=%d month=%d day=%d hour=%d minute=%d second=%d",
                    year, month, day, hour, minute, second));
            }
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.pHYs)) {
            ReaderInfo reader = chunk.getReader();
            int pixelsPerUnitX = reader.getInt32();
            int pixelsPerUnitY = reader.getInt32();
            byte unitSpecifier = reader.getInt8();
            PngDirectory directory = new PngDirectory(PngChunkType.pHYs);
            directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_X, pixelsPerUnitX);
            directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_Y, pixelsPerUnitY);
            directory.setInt(PngDirectory.TAG_UNIT_SPECIFIER, unitSpecifier);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.sBIT)) {
            byte[] bytes = chunk.getReader().toArray();
            PngDirectory directory = new PngDirectory(PngChunkType.sBIT);
            directory.setByteArray(PngDirectory.TAG_SIGNIFICANT_BITS, bytes);
            metadata.addDirectory(directory);
        }
    }
}
