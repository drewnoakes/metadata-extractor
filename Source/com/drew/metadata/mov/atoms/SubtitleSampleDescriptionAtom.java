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
package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QuickTimeSubtitleDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW80
 *
 * @author Payton Garland
 */
public class SubtitleSampleDescriptionAtom extends SampleDescriptionAtom<SubtitleSampleDescriptionAtom.SubtitleSampleDescription>
{
    public SubtitleSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    SubtitleSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return null;
    }

    class SubtitleSampleDescription extends SampleDescription
    {
        int displayFlags;
        long defaultTextBox;
        int fontIdentifier;
        int fontFace;
        int fontSize;
        int[] foregroundColor;

        public SubtitleSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            displayFlags = reader.getInt32();
            reader.skip(1); // Reserved
            reader.skip(1); // Reserved
            reader.skip(4); // Reserved
            defaultTextBox = reader.getInt64();
            reader.skip(4); // Reserved
            fontIdentifier = reader.getInt16();
            fontFace = reader.getInt8();
            fontSize = reader.getInt8();
            foregroundColor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
            // font table atom 'ftab' not currently parsed
        }
    }

    public void addMetadata(QuickTimeSubtitleDirectory directory)
    {
        SubtitleSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QuickTimeSubtitleDirectory.TAG_VERTICAL_PLACEMENT, (description.displayFlags & 0x20000000) == 0x20000000);
        directory.setBoolean(QuickTimeSubtitleDirectory.TAG_SOME_SAMPLES_FORCED, (description.displayFlags & 0x40000000) == 0x40000000);
        directory.setBoolean(QuickTimeSubtitleDirectory.TAG_ALL_SAMPLES_FORCED, (description.displayFlags & 0xC0000000) == 0xC0000000);

        directory.setLong(QuickTimeSubtitleDirectory.TAG_DEFAULT_TEXT_BOX, description.defaultTextBox);
        directory.setInt(QuickTimeSubtitleDirectory.TAG_FONT_IDENTIFIER, description.fontIdentifier);
        switch (description.fontFace) {
            case (1):
                directory.setString(QuickTimeSubtitleDirectory.TAG_FONT_FACE, "Bold");
                break;
            case (2):
                directory.setString(QuickTimeSubtitleDirectory.TAG_FONT_FACE, "Italic");
                break;
            case (4):
                directory.setString(QuickTimeSubtitleDirectory.TAG_FONT_FACE, "Underline");
                break;
        }
        directory.setInt(QuickTimeSubtitleDirectory.TAG_FONT_SIZE, description.fontSize);
        directory.setIntArray(QuickTimeSubtitleDirectory.TAG_FOREGROUND_COLOR, description.foregroundColor);
    }
}
