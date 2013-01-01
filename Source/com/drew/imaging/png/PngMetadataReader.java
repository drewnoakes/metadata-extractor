package com.drew.imaging.png;

import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.png.PngChromaticitiesDirectory;
import com.drew.metadata.png.PngDirectory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.InflaterInputStream;

/**
 * @author Drew Noakes http://drewnoakes.com
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
        Set<PngChunkType> desiredChunkTypes = new HashSet<PngChunkType>();
        desiredChunkTypes.add(PngChunkType.IHDR);
        desiredChunkTypes.add(PngChunkType.PLTE);
        desiredChunkTypes.add(PngChunkType.tRNS);
        desiredChunkTypes.add(PngChunkType.cHRM);
        desiredChunkTypes.add(PngChunkType.sRGB);
        desiredChunkTypes.add(PngChunkType.gAMA);
        desiredChunkTypes.add(PngChunkType.iCCP);

        Iterable<PngChunk> chunks = new PngChunkReader().extract(new StreamReader(inputStream), desiredChunkTypes);

        Metadata metadata = new Metadata();

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
            }
        }

        return metadata;
    }
}
