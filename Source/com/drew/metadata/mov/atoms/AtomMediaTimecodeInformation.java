package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtTimecodeDirectory;

import java.io.IOException;

public class AtomMediaTimecodeInformation extends AtomFull
{
    int textFont;
    int textFace;
    int textSize;
    int[] textColor;
    int[] backgroundColor;
    String fontName;

    public AtomMediaTimecodeInformation(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        textFont = reader.getInt16();
        textFace = reader.getInt16();
        textSize = reader.getInt16();
        reader.skip(2); // Reserved
        textColor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
        backgroundColor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
        fontName = reader.getString(reader.getUInt8());
    }

    public void addMetadata(QtTimecodeDirectory directory)
    {
        directory.setInt(QtTimecodeDirectory.TAG_TEXT_FONT, textFont);
        switch (textFace) {
            case (0x0001):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Bold");
                break;
            case (0x0002):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Italic");
                break;
            case (0x0004):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Underline");
                break;
            case (0x0008):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Outline");
                break;
            case (0x0010):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Shadow");
                break;
            case (0x0020):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Condense");
                break;
            case (0x0040):
                directory.setString(QtTimecodeDirectory.TAG_TEXT_FACE, "Extend");
        }

        directory.setInt(QtTimecodeDirectory.TAG_TEXT_SIZE, textSize);
        directory.setIntArray(QtTimecodeDirectory.TAG_TEXT_COLOR, textColor);
        directory.setIntArray(QtTimecodeDirectory.TAG_BACKGROUND_COLOR, backgroundColor);
        directory.setString(QtTimecodeDirectory.TAG_FONT_NAME, fontName);
    }
}
