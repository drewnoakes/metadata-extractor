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
package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QuickTimeHandler;
import com.drew.lang.DateUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;
import java.io.IOException;

/**
 * Classes that extend this class should be from the media dat atom types:
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
 *
 * @author Payton Garland
 */
public abstract class QuickTimeMediaHandler<T extends QuickTimeDirectory> extends QuickTimeHandler<T>
{
    public QuickTimeMediaHandler(Metadata metadata, QuickTimeContext context)
    {
        super(metadata);

        if (context.creationTime != null && context.modificationTime != null) {
            // Get creation/modification times
            directory.setDate(
                QuickTimeMediaDirectory.TAG_CREATION_TIME,
                DateUtil.get1Jan1904EpochDate(context.creationTime)
            );
            directory.setDate(
                QuickTimeMediaDirectory.TAG_MODIFICATION_TIME,
                DateUtil.get1Jan1904EpochDate(context.modificationTime)
            );
        }
    }

    @Override
    public boolean shouldAcceptAtom(@NotNull Atom atom)
    {
        return atom.type.equals(getMediaInformation())
            || atom.type.equals(QuickTimeAtomTypes.ATOM_SAMPLE_DESCRIPTION)
            || atom.type.equals(QuickTimeAtomTypes.ATOM_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(@NotNull Atom atom)
    {
        return atom.type.equals(QuickTimeContainerTypes.ATOM_SAMPLE_TABLE)
            || atom.type.equals(QuickTimeContainerTypes.ATOM_MEDIA_INFORMATION)
            || atom.type.equals(QuickTimeContainerTypes.ATOM_MEDIA_BASE)
            || atom.type.equals("tmcd");
    }

    @Override
    public QuickTimeMediaHandler processAtom(@NotNull Atom atom, @Nullable byte[] payload, QuickTimeContext context) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(getMediaInformation())) {
                processMediaInformation(reader, atom);
            } else if (atom.type.equals(QuickTimeAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
                processSampleDescription(reader, atom);
            } else if (atom.type.equals(QuickTimeAtomTypes.ATOM_TIME_TO_SAMPLE)) {
                processTimeToSample(reader, atom, context);
            }
        }
        return this;
    }

    protected abstract String getMediaInformation();

    protected abstract void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom, QuickTimeContext context) throws IOException;
}
