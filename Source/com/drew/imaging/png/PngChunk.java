package com.drew.imaging.png;

import com.drew.lang.annotations.NotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChunk
{
    @NotNull
    private final PngChunkType _chunkType;
    @NotNull
    private final byte[] _bytes;

    public PngChunk(@NotNull PngChunkType chunkType, @NotNull byte[] bytes)
    {
        _chunkType = chunkType;
        _bytes = bytes;
    }

    @NotNull
    public PngChunkType getType()
    {
        return _chunkType;
    }

    @NotNull
    public byte[] getBytes()
    {
        return _bytes;
    }
}
