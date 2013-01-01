package com.drew.imaging.png;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.png.PngDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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

        // TODO what other chunk types would we want?

        Iterable<PngChunk> chunks = new PngChunkReader().extract(new StreamReader(inputStream), desiredChunkTypes);

        Metadata metadata = new Metadata();

        for (PngChunk chunk : chunks) {
            PngChunkType chunkType = chunk.getType();

            if (chunkType.equals(PngChunkType.IHDR)) {
                PngHeader header = new PngHeader(chunk);
                PngDirectory directory = metadata.getOrCreateDirectory(PngDirectory.class);
                directory.setInt(PngDirectory.TAG_IMAGE_WIDTH, header.getImageWidth());
                directory.setInt(PngDirectory.TAG_IMAGE_HEIGHT, header.getImageHeight());
                directory.setInt(PngDirectory.TAG_BITS_PER_SAMPLE, header.getBitsPerSample());
                directory.setInt(PngDirectory.TAG_COLOR_TYPE, header.getColorType().getNumericValue());
                directory.setInt(PngDirectory.TAG_COMPRESSION_TYPE, header.getCompressionType());
                directory.setInt(PngDirectory.TAG_FILTER_METHOD, header.getFilterMethod());
                directory.setInt(PngDirectory.TAG_INTERLACE_METHOD, header.getInterlaceMethod());
            } else if (chunkType.equals(PngChunkType.PLTE)) {
                // TODO handle palette
            }

            // TODO determine other interesting chunks to process
            // TODO determine other formats we can handle, such as XMP and ICC
        }

        return metadata;
    }
}
