package com.drew.imaging.png;

import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.png.PngChromaticitiesDirectory;
import com.drew.metadata.png.PngDirectory;
import com.drew.metadata.xmp.XmpReader;

import java.io.*;
import java.util.*;
import java.util.zip.InflaterInputStream;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws PngProcessingException, IOException
    {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return readMetadata(inputStream);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws PngProcessingException, IOException
    {
        // TODO keep a single static hash of these
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

        Iterable<PngChunk> chunks = new PngChunkReader().extract(new StreamReader(inputStream), desiredChunkTypes);

        Metadata metadata = new Metadata();
        List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();

        for (PngChunk chunk : chunks) {
            PngChunkType chunkType = chunk.getType();
            byte[] bytes = chunk.getBytes();

            if (chunkType.equals(PngChunkType.IHDR)) {
                PngHeader header = new PngHeader(bytes);
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setInt(PngDirectory.TAG_IMAGE_WIDTH, header.getImageWidth());
                directory.setInt(PngDirectory.TAG_IMAGE_HEIGHT, header.getImageHeight());
                directory.setInt(PngDirectory.TAG_BITS_PER_SAMPLE, header.getBitsPerSample());
                directory.setInt(PngDirectory.TAG_COLOR_TYPE, header.getColorType().getNumericValue());
                directory.setInt(PngDirectory.TAG_COMPRESSION_TYPE, header.getCompressionType());
                directory.setInt(PngDirectory.TAG_FILTER_METHOD, header.getFilterMethod());
                directory.setInt(PngDirectory.TAG_INTERLACE_METHOD, header.getInterlaceMethod());
            } else if (chunkType.equals(PngChunkType.PLTE)) {
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setInt(PngDirectory.TAG_PALETTE_SIZE, bytes.length / 3);
            } else if (chunkType.equals(PngChunkType.tRNS)) {
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setInt(PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY, 1);
            } else if (chunkType.equals(PngChunkType.sRGB)) {
                int srgbRenderingIntent = new SequentialByteArrayReader(bytes).getInt8();
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setInt(PngDirectory.TAG_SRGB_RENDERING_INTENT, srgbRenderingIntent);
            } else if (chunkType.equals(PngChunkType.cHRM)) {
                PngChromaticities chromaticities = new PngChromaticities(bytes);
                PngChromaticitiesDirectory directory = metadata.getOrCreateDirectory(PngChromaticitiesDirectory.class);
                directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX());
                directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX());
                directory.setInt(PngChromaticitiesDirectory.TAG_RED_X, chromaticities.getRedX());
                directory.setInt(PngChromaticitiesDirectory.TAG_RED_Y, chromaticities.getRedY());
                directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_X, chromaticities.getGreenX());
                directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_Y, chromaticities.getGreenY());
                directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_X, chromaticities.getBlueX());
                directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_Y, chromaticities.getBlueY());
            } else if (chunkType.equals(PngChunkType.gAMA)) {
                int gammaInt = new SequentialByteArrayReader(bytes).getInt32();
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setDouble(PngDirectory.TAG_GAMMA, gammaInt / 100000.0);
            } else if (chunkType.equals(PngChunkType.iCCP)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                String profileName = reader.getNullTerminatedString(79);
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setString(PngDirectory.TAG_PROFILE_NAME, profileName);
                byte compressionMethod = reader.getInt8();
                if (compressionMethod == 0) {
                    // Only compression method allowed by the spec is zero: deflate
                    // This assumes 1-byte-per-char, which it is by spec.
                    int bytesLeft = bytes.length - profileName.length() - 2;
                    byte[] compressedProfile = reader.getBytes(bytesLeft);
                    InflaterInputStream inflateStream = new InflaterInputStream(new ByteArrayInputStream(compressedProfile));
                    new IccReader().extract(new RandomAccessStreamReader(inflateStream), metadata);
                    inflateStream.close();
                }
            } else if (chunkType.equals(PngChunkType.bKGD)) {
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setByteArray(PngDirectory.TAG_BACKGROUND_COLOR, bytes);
            } else if (chunkType.equals(PngChunkType.tEXt)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                String keyword = reader.getNullTerminatedString(79);
                int bytesLeft = bytes.length - keyword.length() - 1;
                String value = reader.getNullTerminatedString(bytesLeft);
                textPairs.add(new KeyValuePair(keyword, value));
            } else if (chunkType.equals(PngChunkType.iTXt)) {
                SequentialReader reader = new SequentialByteArrayReader(bytes);
                String keyword = reader.getNullTerminatedString(79);
                byte compressionFlag = reader.getInt8();
                byte compressionMethod = reader.getInt8();
                String languageTag = reader.getNullTerminatedString(bytes.length);
                String translatedKeyword = reader.getNullTerminatedString(bytes.length);
                int bytesLeft = bytes.length - keyword.length() - 1 - 1 - 1 - languageTag.length() - 1 - translatedKeyword.length() - 1;
                String text = null;
                if (compressionFlag == 0) {
                    text = reader.getNullTerminatedString(bytesLeft);
                } else if (compressionFlag == 1) {
                    if (compressionMethod == 0) {
                        text = StringUtil.fromStream(new InflaterInputStream(new ByteArrayInputStream(bytes, bytes.length - bytesLeft, bytesLeft)));
                    } else {
                        metadata.getOrCreateDirectory(PngDirectory.class).addError("Invalid compression method value");
                    }
                } else {
                    metadata.getOrCreateDirectory(PngDirectory.class).addError("Invalid compression flag value");
                }

                if (text != null) {
                    if (keyword.equals("XML:com.adobe.xmp")) {
                        // NOTE in testing images, the XMP has parsed successfully, but we are not extracting tags from it as necessary
                        new XmpReader().extract(text, metadata);
                    } else {
                        textPairs.add(new KeyValuePair(keyword, text));
                    }
                }
            } else if (chunkType.equals(PngChunkType.tIME)) {
                SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
                int year = reader.getUInt16();
                int month = reader.getUInt8() - 1;
                int day = reader.getUInt8();
                int hour = reader.getUInt8();
                int minute = reader.getUInt8();
                int second = reader.getUInt8();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                //noinspection MagicConstant
                calendar.set(year, month, day, hour, minute, second);
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setDate(PngDirectory.TAG_LAST_MODIFICATION_TIME, calendar.getTime());
            }
        }

        if (textPairs.size() != 0) {
            PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
            directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
        }

        return metadata;
    }
}
