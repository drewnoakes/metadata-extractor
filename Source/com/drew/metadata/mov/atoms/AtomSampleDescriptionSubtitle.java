package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtSubtitleDirectory;

import java.io.IOException;

public class AtomSampleDescriptionSubtitle extends AtomSampleDescription<AtomSampleDescriptionSubtitle.SubtitleSampleDescription>
{

    public AtomSampleDescriptionSubtitle(SequentialReader reader, Atom atom) throws IOException
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

    public void addMetadata(QtSubtitleDirectory directory)
    {
        SubtitleSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QtSubtitleDirectory.TAG_VERTICAL_PLACEMENT, ((description.displayFlags & 0x20000000) == 0x20000000) ? true : false);
        directory.setBoolean(QtSubtitleDirectory.TAG_SOME_SAMPLES_FORCED, ((description.displayFlags & 0x40000000) == 0x40000000) ? true : false);
        directory.setBoolean(QtSubtitleDirectory.TAG_ALL_SAMPLES_FORCED, ((description.displayFlags & 0xC0000000) == 0xC0000000) ? true : false);

        directory.setLong(QtSubtitleDirectory.TAG_DEFAULT_TEXT_BOX, description.defaultTextBox);
        directory.setInt(QtSubtitleDirectory.TAG_FONT_IDENTIFIER, description.fontIdentifier);
        switch (description.fontFace) {
            case (1):
                directory.setString(QtSubtitleDirectory.TAG_FONT_FACE, "Bold");
                break;
            case (2):
                directory.setString(QtSubtitleDirectory.TAG_FONT_FACE, "Italic");
                break;
            case (4):
                directory.setString(QtSubtitleDirectory.TAG_FONT_FACE, "Underline");
                break;
        }
        directory.setInt(QtSubtitleDirectory.TAG_FONT_SIZE, description.fontSize);
        directory.setIntArray(QtSubtitleDirectory.TAG_FOREGROUND_COLOR, description.foregroundColor);
    }
}
