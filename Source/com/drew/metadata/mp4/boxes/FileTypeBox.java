package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4Dictionary;
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.util.ArrayList;

public class FileTypeBox
{
    String majorBrand;
    long minorVersion;
    ArrayList<String> compatibleBrands;

    public FileTypeBox(SequentialReader reader, long size) throws IOException
    {
        majorBrand = reader.getString(4);
        minorVersion = reader.getUInt32();
        compatibleBrands = new ArrayList<String>();
        for (int i = 8; i < size; i += 4) {
            compatibleBrands.add(reader.getString(4));
        }
    }

    public void addMetadata(Mp4Directory directory)
    {
        directory.setString(Mp4Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Mp4Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[compatibleBrands.size()]));
    }
}
