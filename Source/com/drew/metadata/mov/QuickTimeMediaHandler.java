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
package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QuickTimeHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Classes that extend this class should be from the media dat atom types:
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
 *
 * @author Payton Garland
 */
public abstract class QuickTimeMediaHandler<T extends QuickTimeDirectory> extends QuickTimeHandler<T>
{
    public QuickTimeMediaHandler(Metadata metadata)
    {
        super(metadata);
        if (QuickTimeHandlerFactory.HANDLER_PARAM_CREATION_TIME != null && QuickTimeHandlerFactory.HANDLER_PARAM_MODIFICATION_TIME != null) {
            // Get creation/modification times
            Calendar calendar = Calendar.getInstance();
            calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
            Date date = calendar.getTime();
            long macToUnixEpochOffset = date.getTime();
            String creationTimeStamp = new Date(QuickTimeHandlerFactory.HANDLER_PARAM_CREATION_TIME * 1000 + macToUnixEpochOffset).toString();
            String modificationTimeStamp = new Date(QuickTimeHandlerFactory.HANDLER_PARAM_MODIFICATION_TIME * 1000 + macToUnixEpochOffset).toString();
            directory.setString(QuickTimeMediaDirectory.TAG_CREATION_TIME, creationTimeStamp);
            directory.setString(QuickTimeMediaDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);
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
    public QuickTimeMediaHandler processAtom(@NotNull Atom atom, @Nullable byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(getMediaInformation())) {
                processMediaInformation(reader, atom);
            } else if (atom.type.equals(QuickTimeAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
                processSampleDescription(reader, atom);
            } else if (atom.type.equals(QuickTimeAtomTypes.ATOM_TIME_TO_SAMPLE)) {
                processTimeToSample(reader, atom);
            }
        }
        return this;
    }

    protected abstract String getMediaInformation();

    protected abstract void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;
}
