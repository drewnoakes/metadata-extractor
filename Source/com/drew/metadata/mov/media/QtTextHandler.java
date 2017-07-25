package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtMediaHandler;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCJAJEA
 */
public class QtTextHandler extends QtMediaHandler
{
    public QtTextHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtTextDirectory getDirectory()
    {
        return new QtTextDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_BASE_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException
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

        directory.setBoolean(QtTextDirectory.TAG_AUTO_SCALE, ((displayFlags & 0x0002) == 0x0002) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_MOVIE_BACKGROUND_COLOR, ((displayFlags & 0x0008) == 0x0008) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_SCROLL_IN, ((displayFlags & 0x0020) == 0x0020) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_SCROLL_OUT, ((displayFlags & 0x0040) == 0x0040) ? true : false);

        directory.setString(QtTextDirectory.TAG_HORIZONTAL_SCROLL, ((displayFlags & 0x0080) == 0x0080) ? "Horizontal" : "Vertical");

        directory.setString(QtTextDirectory.TAG_REVERSE_SCROLL, ((displayFlags & 0x0100) == 0x0100) ? "Reverse" : "Normal");

        directory.setBoolean(QtTextDirectory.TAG_CONTINUOUS_SCROLL, ((displayFlags & 0x0200) == 0x0200) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_DROP_SHADOW, ((displayFlags & 0x1000) == 0x1000) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_ANTI_ALIAS, ((displayFlags & 0x2000) == 0x2000) ? true : false);

        directory.setBoolean(QtTextDirectory.TAG_KEY_TEXT, ((displayFlags & 0x4000) == 0x4000) ? true : false);

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
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Left");
                break;
            case (1):
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Center");
                break;
            case (-1):
                directory.setString(QtTextDirectory.TAG_JUSTIFICATION, "Right");
        }

        directory.setInt(QtTextDirectory.TAG_BACKGROUND_COLOR, backgroundColor);
        directory.setLong(QtTextDirectory.TAG_DEFAULT_TEXT_BOX, defaultTextBlock);
        directory.setInt(QtTextDirectory.TAG_FONT_NUMBER, fontNumber);

        switch (fontFace) {
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

        directory.setInt(QtTextDirectory.TAG_FOREGROUND_COLOR, foregroundColor);
        directory.setString(QtTextDirectory.TAG_NAME, textName);
    }

    @Override
    protected void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }
}
