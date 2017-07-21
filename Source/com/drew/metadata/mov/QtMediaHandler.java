package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtMediaHandler implements QtHandler
{
    private static final String VIDEO_MEDIA_INFORMATION = "vide";
    private static final String SOUND_MEDIA_INFORMATION = "soun";
    private String handler;

    @Override
    public boolean shouldAcceptAtom(String fourCC) {
        return fourCC.equals(QtAtomTypes.ATOM_HANDLER);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC) {
        return fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION);
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            processHandler(reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC) {
        if (fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION)) {
            if (handler.equals(VIDEO_MEDIA_INFORMATION)) {
                return new QtVideoMediaHandler();
            } else if (handler.equals(SOUND_MEDIA_INFORMATION)) {
                return new QtSoundMediaHandler();
            }
        }
        return this;
    }

    private void processHandler(SequentialByteArrayReader reader) throws IOException {
        reader.skip(8);
        handler = new String(reader.getBytes(4));
    }
}
