package com.drew.imaging.png;

import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileMetadataReader;
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
    private static Set<PngChunkType> _desiredChunkTypes;

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
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream);
        } finally {
            inputStream.close();
        }
        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws PngProcessingException, IOException
    {
        Iterable<PngChunk> chunks = new PngChunkReader().extract(new StreamReader(inputStream), _desiredChunkTypes);

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
        byte[] bytes = chunk.getBytes();

        if (chunkType.equals(PngChunkType.IHDR)) {
            PngHeader header = new PngHeader(bytes);
            PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
            directory.setInt(PngDirectory.TAG_IMAGE_WIDTH, header.getImageWidth());
            directory.setInt(PngDirectory.TAG_IMAGE_HEIGHT, header.getImageHeight());
            directory.setInt(PngDirectory.TAG_BITS_PER_SAMPLE, header.getBitsPerSample());
            directory.setInt(PngDirectory.TAG_COLOR_TYPE, header.getColorType().getNumericValue());
            directory.setInt(PngDirectory.TAG_COMPRESSION_TYPE, header.getCompressionType());
            directory.setInt(PngDirectory.TAG_FILTER_METHOD, header.getFilterMethod());
            directory.setInt(PngDirectory.TAG_INTERLACE_METHOD, header.getInterlaceMethod());
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.PLTE)) {
            PngDirectory directory = new PngDirectory(PngChunkType.PLTE);
            directory.setInt(PngDirectory.TAG_PALETTE_SIZE, bytes.length / 3);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.tRNS)) {
            PngDirectory directory = new PngDirectory(PngChunkType.tRNS);
            directory.setInt(PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY, 1);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.sRGB)) {
            int srgbRenderingIntent = new SequentialByteArrayReader(bytes).getInt8();
            PngDirectory directory = new PngDirectory(PngChunkType.sRGB);
            directory.setInt(PngDirectory.TAG_SRGB_RENDERING_INTENT, srgbRenderingIntent);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.cHRM)) {
            PngChromaticities chromaticities = new PngChromaticities(bytes);
            PngChromaticitiesDirectory directory = new PngChromaticitiesDirectory();
            directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX());
            directory.setInt(PngChromaticitiesDirectory.TAG_WHITE_POINT_X, chromaticities.getWhitePointX());
            directory.setInt(PngChromaticitiesDirectory.TAG_RED_X, chromaticities.getRedX());
            directory.setInt(PngChromaticitiesDirectory.TAG_RED_Y, chromaticities.getRedY());
            directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_X, chromaticities.getGreenX());
            directory.setInt(PngChromaticitiesDirectory.TAG_GREEN_Y, chromaticities.getGreenY());
            directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_X, chromaticities.getBlueX());
            directory.setInt(PngChromaticitiesDirectory.TAG_BLUE_Y, chromaticities.getBlueY());
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.gAMA)) {
            int gammaInt = new SequentialByteArrayReader(bytes).getInt32();
            PngDirectory directory = new PngDirectory(PngChunkType.gAMA);
            directory.setDouble(PngDirectory.TAG_GAMMA, gammaInt / 100000.0);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.iCCP)) {
            SequentialReader reader = new SequentialByteArrayReader(bytes);
            String profileName = reader.getNullTerminatedString(79);
            PngDirectory directory = new PngDirectory(PngChunkType.iCCP);
            directory.setString(PngDirectory.TAG_ICC_PROFILE_NAME, profileName);
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
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.bKGD)) {
            PngDirectory directory = new PngDirectory(PngChunkType.bKGD);
            directory.setByteArray(PngDirectory.TAG_BACKGROUND_COLOR, bytes);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.tEXt)) {
            SequentialReader reader = new SequentialByteArrayReader(bytes);
            String keyword = reader.getNullTerminatedString(79);
            int bytesLeft = bytes.length - keyword.length() - 1;
            String value = reader.getNullTerminatedString(bytesLeft);
            List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
            textPairs.add(new KeyValuePair(keyword, value));
            PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
            directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
            metadata.addDirectory(directory);
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
                    PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                    directory.addError("Invalid compression method value");
                    metadata.addDirectory(directory);
                }
            } else {
                PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                directory.addError("Invalid compression flag value");
                metadata.addDirectory(directory);
            }

            if (text != null) {
                if (keyword.equals("XML:com.adobe.xmp")) {
                    // NOTE in testing images, the XMP has parsed successfully, but we are not extracting tags from it as necessary
                    new XmpReader().extract(text, metadata);
                } else {
                    List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
                    textPairs.add(new KeyValuePair(keyword, text));
                    PngDirectory directory = new PngDirectory(PngChunkType.iTXt);
                    directory.setObject(PngDirectory.TAG_TEXTUAL_DATA, textPairs);
                    metadata.addDirectory(directory);
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
            PngDirectory directory = new PngDirectory(PngChunkType.tIME);
            directory.setDate(PngDirectory.TAG_LAST_MODIFICATION_TIME, calendar.getTime());
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.pHYs)) {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
            int pixelsPerUnitX = reader.getInt32();
            int pixelsPerUnitY = reader.getInt32();
            byte unitSpecifier = reader.getInt8();
            PngDirectory directory = new PngDirectory(PngChunkType.pHYs);
            directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_X, pixelsPerUnitX);
            directory.setInt(PngDirectory.TAG_PIXELS_PER_UNIT_Y, pixelsPerUnitY);
            directory.setInt(PngDirectory.TAG_UNIT_SPECIFIER, unitSpecifier);
            metadata.addDirectory(directory);
        } else if (chunkType.equals(PngChunkType.sBIT)) {
            PngDirectory directory = new PngDirectory(PngChunkType.sBIT);
            directory.setByteArray(PngDirectory.TAG_SIGNIFICANT_BITS, bytes);
            metadata.addDirectory(directory);
        }
    }
}
