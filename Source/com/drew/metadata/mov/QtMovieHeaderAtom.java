package com.drew.metadata.mov;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class QtMovieHeaderAtom extends QtAtom implements QtLeafAtom {
    private static long MVHD_TIME_OFFSET = 12;

    private int timescale;
    private float duration;
    private long creationTime;
    private String creationTimestamp;
    private long modificationTime;
    private String modificationTimestamp;

    public QtMovieHeaderAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];

        source.reset();
        source.skip(offset + MVHD_TIME_OFFSET);

        source.read(buffer);
        creationTime = ByteUtil.getUnsignedInt32(buffer, 0, true);

        source.read(buffer);
        modificationTime = ByteUtil.getUnsignedInt32(buffer, 0, true);

        source.read(buffer);
        timescale = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        duration = ByteUtil.getInt32(buffer, 0, true);
        duration = duration/timescale;

        calculateTimestamps();

    }

    public void populateMetadata(FileInfo fileId)
    {
        fileId.addMetadata(StandardMetadata.MEDIA_TIMESCALE, timescale);
        fileId.addMetadata(StandardMetadata.DURATION, duration);
        fileId.addMetadata(StandardMetadata.CREATION_TIMESTAMP, creationTimestamp);
        fileId.addMetadata(StandardMetadata.MODIFICATION_TIMESTAMP, modificationTimestamp);
    }

    private void calculateTimestamps()
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(1904, 0, 1, 0, 0, 0);      // january 1, 1904  -  macintosh time epoch
        Date date = calendar.getTime();
        long macToUnixEpochOffset = date.getTime();

        creationTimestamp = new Date(creationTime*1000 + macToUnixEpochOffset).toString();
        modificationTimestamp = new Date(modificationTime*1000 + macToUnixEpochOffset).toString();
    }

    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ")\n Timescale: " + timescale +
            "\n Duration: " + duration) +
            "\n Creation Time: " + creationTime +
            "\n Modification Time: " + modificationTime;
    }
}
