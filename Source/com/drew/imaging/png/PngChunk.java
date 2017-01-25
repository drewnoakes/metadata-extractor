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
