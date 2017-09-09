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
import com.drew.metadata.mov.atoms.TextSampleDescriptionAtom;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QuickTimeTextHandler extends QuickTimeMediaHandler<QuickTimeTextDirectory>
{
    public QuickTimeTextHandler(Metadata metadata)
    {
        super(metadata);
    }

    @NotNull
    @Override
    protected QuickTimeTextDirectory getDirectory()
    {
        return new QuickTimeTextDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QuickTimeAtomTypes.ATOM_BASE_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TextSampleDescriptionAtom textSampleDescriptionAtom = new TextSampleDescriptionAtom(reader, atom);
        textSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Not yet implemented
    }
}
