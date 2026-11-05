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

import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * The structure of one file format: which boxes contain child boxes, and what comes before
 * them. Many containers start with fields of their own, such as a FullBox version and flags
 * ({@code meta}), an entry count ({@code stsd}) or the fixed fields of a sample entry, whose
 * size can depend on a version field or on the track's handler type.
 * <p>
 * A layout that also implements {@link IsoBoxReader} is always run, ahead of the readers
 * passed in, so it can record what later structure depends on (such as a track's handler
 * type) even if no other reader asks for that box.
 */
public interface IsoBoxLayout
{
    /**
     * Called for a box the walk would descend into. Returns {@code false} if the box has no
     * child boxes; otherwise reads or skips whatever comes before them from
     * {@link IsoBoxContext#payload()} and returns {@code true}.
     */
    boolean enterContainer(@NotNull IsoBoxContext box) throws IOException;
}
