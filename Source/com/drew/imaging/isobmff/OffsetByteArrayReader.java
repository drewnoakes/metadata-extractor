/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

/** Reads an in-memory payload while reporting the positions it has in the input. */
final class OffsetByteArrayReader extends SequentialByteArrayReader
{
    private final long _base;

    OffsetByteArrayReader(@NotNull byte[] bytes, int start, long base)
    {
        super(bytes, start);
        _base = base;
    }

    @Override
    public long getPosition()
    {
        return _base + super.getPosition();
    }
}
