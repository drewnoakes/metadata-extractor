package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtSoundDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25647
 */
public class SoundInformationMediaHeaderAtom extends FullAtom
{
    int balance;

    public SoundInformationMediaHeaderAtom(SequentialReader reader, Atom atom) throws IOException
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
