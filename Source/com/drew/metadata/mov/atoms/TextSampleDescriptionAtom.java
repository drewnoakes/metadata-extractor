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
import com.drew.metadata.mov.media.QuickTimeTextDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-57428
 *
 * @author Payton Garland
 */
public class TextSampleDescriptionAtom extends SampleDescriptionAtom<TextSampleDescriptionAtom.TextSampleDescription>
{
    public TextSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    TextSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return new TextSampleDescription(reader);
    }

    public void addMetadata(QuickTimeTextDirectory directory)
    {
        TextSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QuickTimeTextDirectory.TAG_AUTO_SCALE, (description.displayFlags & 0x0002) == 0x0002);

        directory.setBoolean(QuickTimeTextDirectory.TAG_MOVIE_BACKGROUND_COLOR, (description.displayFlags & 0x0008) == 0x0008);

        directory.setBoolean(QuickTimeTextDirectory.TAG_SCROLL_IN, (description.displayFlags & 0x0020) == 0x0020);

        directory.setBoolean(QuickTimeTextDirectory.TAG_SCROLL_OUT, (description.displayFlags & 0x0040) == 0x0040);

        directory.setString(QuickTimeTextDirectory.TAG_HORIZONTAL_SCROLL, ((description.displayFlags & 0x0080) == 0x0080) ? "Horizontal" : "Vertical");

        directory.setString(QuickTimeTextDirectory.TAG_REVERSE_SCROLL, ((description.displayFlags & 0x0100) == 0x0100) ? "Reverse" : "Normal");

        directory.setBoolean(QuickTimeTextDirectory.TAG_CONTINUOUS_SCROLL, (description.displayFlags & 0x0200) == 0x0200);

        directory.setBoolean(QuickTimeTextDirectory.TAG_DROP_SHADOW, (description.displayFlags & 0x1000) == 0x1000);

        directory.setBoolean(QuickTimeTextDirectory.TAG_ANTI_ALIAS, (description.displayFlags & 0x2000) == 0x2000);

        directory.setBoolean(QuickTimeTextDirectory.TAG_KEY_TEXT, (description.displayFlags & 0x4000) == 0x4000);

        switch (description.textJustification) {
            case (0):
                directory.setString(QuickTimeTextDirectory.TAG_JUSTIFICATION, "Left");
                break;
            case (1):
                directory.setString(QuickTimeTextDirectory.TAG_JUSTIFICATION, "Center");
                break;
            case (-1):
                directory.setString(QuickTimeTextDirectory.TAG_JUSTIFICATION, "Right");
        }

        directory.setIntArray(QuickTimeTextDirectory.TAG_BACKGROUND_COLOR, description.backgroundColor);
        directory.setLong(QuickTimeTextDirectory.TAG_DEFAULT_TEXT_BOX, description.defaultTextBox);
        directory.setInt(QuickTimeTextDirectory.TAG_FONT_NUMBER, description.fontNumber);

        switch (description.fontFace) {
            case (0x0001):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Bold");
                break;
            case (0x0002):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Italic");
                break;
            case (0x0004):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Underline");
                break;
            case (0x0008):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Outline");
                break;
            case (0x0010):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Shadow");
                break;
            case (0x0020):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Condense");
                break;
            case (0x0040):
                directory.setString(QuickTimeTextDirectory.TAG_FONT_FACE, "Extend");
        }

        directory.setIntArray(QuickTimeTextDirectory.TAG_FOREGROUND_COLOR, description.foregroundColor);
        directory.setString(QuickTimeTextDirectory.TAG_NAME, description.textName);
    }

    class TextSampleDescription extends SampleDescription
    {
        int displayFlags;
        int textJustification;
        int[] backgroundColor;
        long defaultTextBox;
        int fontNumber;
        int fontFace;
        int[] foregroundColor;
        String textName;

        public TextSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            displayFlags = reader.getInt32();
            textJustification = reader.getInt32();
            backgroundColor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
            defaultTextBox = reader.getInt64();
            reader.skip(8); // 64-bits of reserved space set to 0
            fontNumber = reader.getUInt16();
            fontFace = reader.getUInt16();
            reader.skip(1); // 8-bits of reserved space set to 0
            reader.skip(2); // 16-bits of reserved space set to 0
            foregroundColor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
            textName = reader.getString(reader.getUInt8());
        }
    }
}
