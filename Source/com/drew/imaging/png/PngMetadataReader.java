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
package com.drew.imaging.png;

import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.file.FileMetadataReader;
import com.drew.metadata.filter.MetadataFilter;
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
        desiredChunkTypes.add(PngChunkType.iTXt);
        desiredChunkTypes.add(PngChunkType.tIME);
        desiredChunkTypes.add(PngChunkType.pHYs);
        desiredChunkTypes.add(PngChunkType.sBIT);

        _desiredChunkTypes = Collections.unmodifiableSet(desiredChunkTypes);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws PngProcessingException, IOException
    {
        return readMetadata(file, null);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file, @Nullable final MetadataFilter filter) throws PngProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream, filter);
        } finally {
            inputStream.close();
        }
        new FileMetadataReader().read(file, metadata, filter);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws PngProcessingException, IOException
    {
        return readMetadata(inputStream, null);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, @Nullable final MetadataFilter filter) throws PngProcessingException, IOException
    {
        Iterable<PngChunk> chunks = new PngChunkReader().extract(new StreamReader(inputStream), _desiredChunkTypes);

        Metadata metadata = new Metadata();

        for (PngChunk chunk : chunks) {
            try {
                processChunk(metadata, chunk, filter);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return metadata;
    }

    private static void processChunk(@NotNull Metadata metadata, @NotNull PngChunk chunk, @Nullable final MetadataFilter filter) throws PngProcessingException, IOException
    {
        if (filter != null && !filter.directoryFilter(PngDirectory.class))
            return;

        PngChunkType chunkType = chunk.getType();
        byte[] bytes = chunk.getBytes();

        if (chunkType.equals(PngChunkType.IHDR)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                PngHeader header = new PngHeader(bytes);
                PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
                directory.setInt(PngDirectory.TAG_IMAGE_WIDTH, header.getImageWidth(), filter);
                directory.setInt(PngDirectory.TAG_IMAGE_HEIGHT, header.getImageHeight(), filter);
                directory.setInt(PngDirectory.TAG_BITS_PER_SAMPLE, header.getBitsPerSample(), filter);
                directory.setInt(PngDirectory.TAG_COLOR_TYPE, header.getColorType().getNumericValue(), filter);
                directory.setInt(PngDirectory.TAG_COMPRESSION_TYPE, header.getCompressionType(), filter);
                directory.setInt(PngDirectory.TAG_FILTER_METHOD, header.getFilterMethod(), filter);
                directory.setInt(PngDirectory.TAG_INTERLACE_METHOD, header.getInterlaceMethod(), filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.PLTE)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                PngDirectory directory = new PngDirectory(PngChunkType.PLTE);
                directory.setInt(PngDirectory.TAG_PALETTE_SIZE, bytes.length / 3, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.tRNS)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                PngDirectory directory = new PngDirectory(PngChunkType.tRNS);
                directory.setInt(PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY, 1, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.sRGB)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                int srgbRenderingIntent = bytes[0];
                PngDirectory directory = new PngDirectory(PngChunkType.sRGB);
                directory.setInt(PngDirectory.TAG_SRGB_RENDERING_INTENT, srgbRenderingIntent, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.cHRM)) {
            if (filter == null || filter.directoryFilter(PngChromaticitiesDirectory.class)) {
                PngChromaticities chromaticities = new PngChromaticities(bytes);
                PngChromaticitiesDirectory directory = new PngChromaticitiesDirectory();
                directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_Y, chromaticities.getWhitePointY(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_RED_X, chromaticities.getRedX(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_RED_Y, chromaticities.getRedY(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_X, chromaticities.getGreenX(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_Y, chromaticities.getGreenY(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_X, chromaticities.getBlueX(), filter);
                directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_Y, chromaticities.getBlueY(), filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.gAMA)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                int gammaInt = ByteConvert.toInt32BigEndian(bytes);
                new SequentialByteArrayReader(bytes).getInt32();
                PngDirectory directory = new PngDirectory(PngChunkType.gAMA);
                directory.setDouble(PngDirectory.TAG_GAMMA, gammaInt / 100000.0, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.iCCP)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                byte[] profileNameBytes = reader.getNullTerminatedBytes(79);
                PngDirectory directory = new PngDirectory(PngChunkType.iCCP);
                directory.setStringValue(PngDirectory.TAG_ICC_PROFILE_NAME, new StringValue(profileNameBytes, _latin1Encoding), filter);
                byte compressionMethod = reader.getInt8();
                if (compressionMethod == 0) {
                    // Only compression method allowed by the spec is zero: deflate
                    int bytesLeft = bytes.length - profileNameBytes.length - 2;
                    byte[] compressedProfile = reader.getBytes(bytesLeft);
                    InflaterInputStream inflateStream = new InflaterInputStream(new ByteArrayInputStream(compressedProfile));
                    new IccReader().extract(new RandomAccessStreamReader(inflateStream), metadata, directory, filter);
                    inflateStream.close();
                } else {
                    directory.addError("Invalid compression method value");
                }
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.bKGD)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                PngDirectory directory = new PngDirectory(PngChunkType.bKGD);
                directory.setByteArray(PngDirectory.TAG_BACKGROUND_COLOR, bytes, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.tEXt)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                byte[] keywordBytes = reader.getNullTerminatedBytes(79);
                StringValue keyword = new StringValue(keywordBytes, _latin1Encoding);
                int bytesLeft = bytes.length - keywordBytes.length - 1;
                StringValue value = reader.getNullTerminatedStringValue(bytesLeft, _latin1Encoding);
                List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
                textPairs.add(new KeyValuePair(keyword, value));
                PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.iTXt)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                byte[] keywordBytes = reader.getNullTerminatedBytes(79);
                byte compressionFlag = reader.getInt8();
                byte compressionMethod = reader.getInt8();
                // TODO we currently ignore languageTagBytes and translatedKeywordBytes
                byte[] languageTagBytes = reader.getNullTerminatedBytes(bytes.length);
                byte[] translatedKeywordBytes = reader.getNullTerminatedBytes(bytes.length);
                int bytesLeft = bytes.length - keywordBytes.length - 1 - 1 - 1 - languageTagBytes.length - 1 - translatedKeywordBytes.length - 1;
                byte[] textBytes = null;
                if (compressionFlag == 0) {
                    textBytes = reader.getNullTerminatedBytes(bytesLeft);
                } else if (compressionFlag == 1) {
                    if (compressionMethod == 0) {
                        textBytes = StreamUtil.readAllBytes(new InflaterInputStream(new ByteArrayInputStream(bytes, bytes.length - bytesLeft, bytesLeft)));
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
                    StringValue keyword = new StringValue(keywordBytes, _latin1Encoding);
                    if (keyword.toString().equals("XML:com.adobe.xmp")) {
                        // NOTE in testing images, the XMP has parsed successfully, but we are not extracting tags from it as necessary
                        new XmpReader().extract(textBytes, metadata, filter);
                    } else {
                        List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
                        textPairs.add(new KeyValuePair(keyword, new StringValue(textBytes, _latin1Encoding)));
                        PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                        directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs, filter);
                        metadata.addDirectory(directory);
                    }
                }
            }
        } else if (chunkType.equals(PngChunkType.tIME)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
                int year = reader.getUInt16();
                int month = reader.getUInt8();
                int day = reader.getUInt8();
                int hour = reader.getUInt8();
                int minute = reader.getUInt8();
                int second = reader.getUInt8();
                PngDirectory directory = new PngDirectory(PngChunkType.tIME);
                if (DateUtil.isValidDate(year, month - 1, day) && DateUtil.isValidTime(hour, minute, second)) {
                    String dateString = String.format("%04d:%02d:%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
                    directory.setString(PngDirectory.TAG_LAST_MODIFICATION_TIME, dateString, filter);
                } else {
                    directory.addError(String.format(
                        "PNG tIME data describes an invalid date/time: year=%d month=%d day=%d hour=%d minute=%d second=%d",
                        year, month, day, hour, minute, second));
                }
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.pHYs)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
                int pixelsPerUnitX = reader.getInt32();
                int pixelsPerUnitY = reader.getInt32();
                byte unitSpecifier = reader.getInt8();
                PngDirectory directory = new PngDirectory(PngChunkType.pHYs);
                directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_X, pixelsPerUnitX, filter);
                directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_Y, pixelsPerUnitY, filter);
                directory.setInt(PngDirectory.TAG_UNIT_SPECIFIER, unitSpecifier, filter);
                metadata.addDirectory(directory);
            }
        } else if (chunkType.equals(PngChunkType.sBIT)) {
            if (filter == null || filter.directoryFilter(PngDirectory.class)) {
                PngDirectory directory = new PngDirectory(PngChunkType.sBIT);
                directory.setByteArray(PngDirectory.TAG_SIGNIFICANT_BITS, bytes, filter);
                metadata.addDirectory(directory);
            }
        }
    }
}
