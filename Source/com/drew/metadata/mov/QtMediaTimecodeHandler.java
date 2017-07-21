package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtMediaTimecodeHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation() {
        return QtAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int numberOfEntries = reader.getInt32();
        int sampleDescriptionSize = reader.getInt32();
        reader.skip(6); // Reserved
        int dataReferenceIndex = reader.getInt16();
        reader.skip(4); // Reserved
        int flags = reader.getInt32();
        int timeScale = reader.getInt32();
        int frameDuration = reader.getInt32();
        int numberOfFrames = reader.getInt8();
        reader.skip(1); // Reserved
        // Source reference...
    }

    @Override
    public void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        int typeAndVersion = reader.getInt32();
        int textFont = reader.getInt16();
        int textFace = reader.getInt16();
        int textSize = reader.getInt16();
        reader.skip(2); // Reserved
        byte[] textColor = reader.getBytes(6);
        byte[] backgroundColor = reader.getBytes(6);
        String fontName = new String(reader.getBytes(reader.available() - 24));
        System.out.println(fontName);
    }
}
