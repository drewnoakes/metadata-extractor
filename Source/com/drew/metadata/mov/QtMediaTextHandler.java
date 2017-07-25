package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;

import java.io.IOException;

public class QtMediaTextHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        return QtAtomTypes.ATOM_BASE_MEDIA_INFO;
    }

    @Override
    void processSampleDescription(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Begin general structure
        int versionAndFlags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        String dataFormat = reader.getString(12, 4, "UTF-8");
        // 6-bytes of reserved space set to 0
        int dataReferenceIndex = reader.getInt16(22);
        // End general structure

        if (!dataFormat.equals(QtContainerTypes.ATOM_MEDIA_TEXT)) {
            directory.addError("Text sample description atom has incorrect data format, no data extracted");
            return;
        }

        int displayFlags = reader.getInt32(24);
        if ((displayFlags & 0x0002) == 0x0002) {
            directory.setBoolean(QtDirectory.TAG_TEXT_AUTO_SCALE, false);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_AUTO_SCALE, true);
        }

        if ((displayFlags & 0x0008) == 0x0008) {
            directory.setBoolean(QtDirectory.TAG_TEXT_MOVIE_BACKGROUND_COLOR, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_MOVIE_BACKGROUND_COLOR, false);
        }

        if ((displayFlags & 0x0020) == 0x0020) {
            directory.setBoolean(QtDirectory.TAG_TEXT_SCROLL_IN, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_SCROLL_IN, false);
        }

        if ((displayFlags & 0x0040) == 0x0040) {
            directory.setBoolean(QtDirectory.TAG_TEXT_SCROLL_OUT, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_SCROLL_OUT, false);
        }

        if ((displayFlags & 0x0080) == 0x0080) {
            directory.setString(QtDirectory.TAG_TEXT_HORIZONTAL_SCROLL, "Horizontal");
        } else {
            directory.setString(QtDirectory.TAG_TEXT_HORIZONTAL_SCROLL, "Vertical");
        }

        if ((displayFlags & 0x0100) == 0x0100) {
            directory.setString(QtDirectory.TAG_TEXT_REVERSE_SCROLL, "Reverse");
        } else {
            directory.setString(QtDirectory.TAG_TEXT_REVERSE_SCROLL, "Normal");
        }

        if ((displayFlags & 0x0200) == 0x0200) {
            directory.setBoolean(QtDirectory.TAG_TEXT_CONTINUOUS_SCROLL, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_CONTINUOUS_SCROLL, false);
        }

        if ((displayFlags & 0x1000) == 0x1000) {
            directory.setBoolean(QtDirectory.TAG_TEXT_DROP_SHADOW, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_DROP_SHADOW, false);
        }

        if ((displayFlags & 0x2000) == 0x2000) {
            directory.setBoolean(QtDirectory.TAG_TEXT_ANTI_ALIAS, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_ANTI_ALIAS, false);
        }

        if ((displayFlags & 0x4000) == 0x4000) {
            directory.setBoolean(QtDirectory.TAG_TEXT_KEY_TEXT, true);
        } else {
            directory.setBoolean(QtDirectory.TAG_TEXT_KEY_TEXT, false);
        }

        int textJustification = reader.getInt32(28);
        int backgroundColor = reader.getInt24(32);
        long defaultTextBlock = reader.getInt64(35);
        // 64-bits of reserved space set to 0
        int fontNumber = reader.getInt16(43);
        int fontFace = reader.getInt16(45);
        // 8-bits of reserved space set to 0
        // 16-bits of reserved space set to 0
        int foregroundColor = reader.getInt24(48);
        String textName = reader.getString(51, (int)reader.getLength() - 51, "UTF-8");

        switch (textJustification) {
            case (0):
                directory.setString(QtDirectory.TAG_TEXT_JUSTIFICATION, "Left");
                break;
            case (1):
                directory.setString(QtDirectory.TAG_TEXT_JUSTIFICATION, "Center");
                break;
            case (-1):
                directory.setString(QtDirectory.TAG_TEXT_JUSTIFICATION, "Right");
        }

        directory.setInt(QtDirectory.TAG_TEXT_BACKGROUND_COLOR, backgroundColor);
        directory.setLong(QtDirectory.TAG_TEXT_DEFAULT_TEXT_BOX, defaultTextBlock);
        directory.setInt(QtDirectory.TAG_TEXT_FONT_NUMBER, fontNumber);

        switch (fontFace) {
            case (0x0001):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Bold");
                break;
            case (0x0002):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Italic");
                break;
            case (0x0004):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Underline");
                break;
            case (0x0008):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Outline");
                break;
            case (0x0010):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Shadow");
                break;
            case (0x0020):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Condense");
                break;
            case (0x0040):
                directory.setString(QtDirectory.TAG_TEXT_FONT_FACE, "Extend");
        }

        directory.setInt(QtDirectory.TAG_TEXT_FOREGROUND_COLOR, foregroundColor);
        directory.setString(QtDirectory.TAG_TEXT_NAME, textName);
    }

    @Override
    void processMediaInformation(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    void processTimeToSample(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }
}
