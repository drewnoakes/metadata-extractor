package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;

import java.io.IOException;

public class QtMediaHeaderAtom extends QtAtom implements QtLeafAtom {
    private int mediaTimescale;

    public QtMediaHeaderAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }
    public void getMetadata(QtDataSource source) throws IOException
    {

        byte[] buffer = new byte[4];

        source.reset();
        source.skip(offset + 20);

        source.read(buffer);
        mediaTimescale = ByteUtil.getInt32(buffer, 0, true);
    }

    public void populateMetadata(Directory directory)
    {
        directory.setInt(QtDirectory.TAG_MEDIA_TIME_SCALE, mediaTimescale);
    }

    public int getMediaTimescale()
    {
        return mediaTimescale;
    }

    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ")\n Media Timescale: " + mediaTimescale);
    }
}
