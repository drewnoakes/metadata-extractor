package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtSoundDirectory;

import java.io.IOException;

public class AtomHeaderMediaSoundInformation extends AtomFull
{
    int balance;

    public AtomHeaderMediaSoundInformation(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        balance = reader.getInt16();
        reader.skip(2); // Reserved
    }

    public void addMetadata(QtSoundDirectory directory)
    {
        double integerPortion = balance & 0xFFFF0000;
        double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtSoundDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
    }
}
