package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html#//apple_ref/doc/uid/TP40000939-CH203-CJBCBIFF
 */
public class FileTypeCompatibilityAtom extends Atom
{
    String majorBrand;
    long minorVersion;
    ArrayList<String> compatibleBrands;

    public FileTypeCompatibilityAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(atom);

        majorBrand = reader.getString(4);
        minorVersion = reader.getUInt32();
        compatibleBrands = new ArrayList<String>();
        for (int i = 16; i < size; i += 4) {
            compatibleBrands.add(reader.getString(4));
        }
    }

    public void addMetadata(QtDirectory directory)
    {
        directory.setString(QtDirectory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(QtDirectory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(QtDirectory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[compatibleBrands.size()]));
    }
}
