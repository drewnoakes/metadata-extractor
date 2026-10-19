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
 * Visitor interface for {@link IsoBoxWalker}.
 * <p>
 * For each box encountered the walker first asks {@link #shouldRecurse} — if {@code true} it
 * calls {@link #processContainer} to obtain the visitor for the nested level, then recurses.
 * Otherwise it asks {@link #shouldVisit} — if {@code true} it reads the payload bytes and calls
 * {@link #visit}. If neither, the box is skipped.
 */
public abstract class IsoBoxVisitor
{
    /**
     * Returns {@code true} if the walker should recurse into this box's payload as a nested box sequence.
     * When {@code true}, {@link #processContainer} is called next.
     */
    protected abstract boolean shouldRecurse(@NotNull IsoBox box);

    /**
     * Called before recursing into a container box. Returns the visitor to use for the nested level.
     * Returning {@code this} continues with the same visitor; returning a new instance delegates to
     * a format-specific sub-handler.
     */
    @NotNull
    protected abstract IsoBoxVisitor processContainer(@NotNull IsoBox box);

    /**
     * Returns {@code true} if the walker should read the payload and call {@link #visit}.
     * Only consulted when {@link #shouldRecurse} returned {@code false}.
     */
    protected abstract boolean shouldVisit(@NotNull IsoBox box);

    /**
     * Called with the complete payload bytes of a visited box.
     *
     * @param box     the box header (type, size, usertype)
     * @param payload bytes of the box payload (everything after the full box header)
     */
    protected abstract void visit(@NotNull IsoBox box, @NotNull byte[] payload) throws IOException;

    /** Records an error encountered during parsing. */
    public abstract void addError(@NotNull String message);
}
