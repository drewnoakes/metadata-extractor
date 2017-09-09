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
package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeAtomTypes;
import com.drew.metadata.mov.QuickTimeMediaHandler;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.atoms.TimeToSampleAtom;
import com.drew.metadata.mov.atoms.VideoInformationMediaHeaderAtom;
import com.drew.metadata.mov.atoms.VideoSampleDescriptionAtom;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QuickTimeVideoHandler extends QuickTimeMediaHandler<QuickTimeVideoDirectory>
{
    public QuickTimeVideoHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected String getMediaInformation()
    {
        return QuickTimeAtomTypes.ATOM_VIDEO_MEDIA_INFO;
    }

    @NotNull
    @Override
    protected QuickTimeVideoDirectory getDirectory()
    {
        return new QuickTimeVideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        VideoSampleDescriptionAtom videoSampleDescriptionAtom = new VideoSampleDescriptionAtom(reader, atom);
        videoSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        VideoInformationMediaHeaderAtom videoInformationMediaHeaderAtom = new VideoInformationMediaHeaderAtom(reader, atom);
        videoInformationMediaHeaderAtom.addMetadata(directory);
    }

    @Override
    public void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimeToSampleAtom timeToSampleAtom = new TimeToSampleAtom(reader, atom);
        timeToSampleAtom.addMetadata(directory);
    }
}
