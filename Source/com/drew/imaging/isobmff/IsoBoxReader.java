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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.isobmff;

import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * Reads boxes at registered paths, in the way {@code JpegSegmentMetadataReader} reads JPEG
 * segments of registered types.
 * <p>
 * A path names box types from the top level down, separated by {@code /}, such as
 * {@code moov/trak/mdia/hdlr}. A {@code *} segment matches any one box type. The walk descends
 * only into containers on the way to a registered path, so everything else is skipped unread.
 * <p>
 * Readers hold no state of their own: per-file and per-container state lives in the
 * {@link IsoBoxContext}, so one reader instance can serve any number of files.
 */
public interface IsoBoxReader
{
    @NotNull
    Iterable<String> getBoxPaths();

    /**
     * Reads the box of {@code box}. The payload reader starts at the beginning of the payload,
     * independent of any other reader registered for the same box.
     */
    void readBox(@NotNull IsoBoxContext box) throws IOException;
}
