package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtTextDirectory;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.ArrayList;

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

    public void addMetadata(QtTextDirectory directory)
    {
        TextSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QtTextDirectory.TAG_AUTO_SCALE, ((description.displayFlags & 0x0002) == 0x0002) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_MOVIE_BACKGROUND_COLOR, ((description.displayFlags & 0x0008) == 0x0008) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_SCROLL_IN, ((description.displayFlags & 0x0020) == 0x0020) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_SCROLL_OUT, ((description.displayFlags & 0x0040) == 0x0040) ? true : false);

        directory.setString(QtTextDirectory.TAG_HORIZONTAL_SCROLL, ((description.displayFlags & 0x0080) == 0x0080) ? "Horizontal" : "Vertical");

        directory.setString(QtTextDirectory.TAG_REVERSE_SCROLL, ((description.displayFlags & 0x0100) == 0x0100) ? "Reverse" : "Normal");

        directory.setBoolean(QtTextDirectory.TAG_CONTINUOUS_SCROLL, ((description.displayFlags & 0x0200) == 0x0200) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_DROP_SHADOW, ((description.displayFlags & 0x1000) == 0x1000) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_ANTI_ALIAS, ((description.displayFlags & 0x2000) == 0x2000) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_KEY_TEXT, ((description.displayFlags & 0x4000) == 0x4000) ? true : false);

        switch (description.textJustification) {
            case (0):
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Left");
                break;
            case (1):
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Center");
                break;
            case (-1):
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Right");
        }

        directory.setIntArray(QtTextDirectory.TAG_BACKGROUND_COLOR, description.backgroundColor);
        directory.setLong(QtTextDirectory.TAG_DEFAULT_TEXT_BOX, description.defaultTextBox);
        directory.setInt(QtTextDirectory.TAG_FONT_NUMBER, description.fontNumber);

        switch (description.fontFace) {
            case (0x0001):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Bold");
                break;
            case (0x0002):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Italic");
                break;
            case (0x0004):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Underline");
                break;
            case (0x0008):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Outline");
                break;
            case (0x0010):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Shadow");
                break;
            case (0x0020):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Condense");
                break;
            case (0x0040):
                directory.setString(QtTextDirectory.TAG_FONT_FACE, "Extend");
        }

        directory.setIntArray(QtTextDirectory.TAG_FOREGROUND_COLOR, description.foregroundColor);
        directory.setString(QtTextDirectory.TAG_NAME, description.textName);
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
