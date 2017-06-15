package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class QtMovieHeaderAtom extends QtAtom implements QtLeafAtom {
    private static long MVHD_TIME_OFFSET = 12;

    private int timescale;
    private float duration;
    private float preferredRate;
    private float preferredVolume;
    private float previewTime;
    private float previewDuration;
    private float posterTime;
    private float selectionTime;
    private float selectionDuration;
    private float currentTime;
    private int nextTrack;
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

        source.read(buffer);
        preferredRate = (buffer[0] * (float)Math.pow(16, 1)) + buffer[1] + (buffer[2] * (float)Math.pow(16, -1)) + (buffer[3] * (float)Math.pow(16, -2));

        buffer = new byte[2];
        source.read(buffer);
        preferredVolume = (((float)buffer[0] + (float)(buffer[1] * Math.pow(16.0, -1.0))) / 1) * 100;

        source.skip(46);

        buffer = new byte[4];
        source.read(buffer);
        previewTime = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        previewDuration = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        posterTime = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        selectionTime = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        selectionDuration = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        currentTime = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        nextTrack = ByteUtil.getInt32(buffer, 0, true);

        calculateTimestamps();

    }

    public void populateMetadata(Directory directory) throws MetadataException
    {
        directory.setInt(QtDirectory.TAG_MEDIA_TIME_SCALE, timescale);
        directory.setFloat(QtDirectory.TAG_DURATION, duration);
        directory.setString(QtDirectory.TAG_CREATION_TIMESTAMP, creationTimestamp);
        directory.setString(QtDirectory.TAG_MODIFICATION_TIMESTAMP, modificationTimestamp);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_PREFERRED_RATE, preferredRate);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_PREFERRED_VOLUME, preferredVolume);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_PREVIEW_TIME, previewTime);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_DURATION, previewDuration);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_POSTER_TIME, posterTime);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_SELECTION_TIME, selectionTime);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_SELECTION_DURATION, selectionDuration);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_CURRENT_TIME, currentTime);
        directory.setFloat(QtDirectory.TAG_MOOV_MVHD_NEXT_TRACK_ID, nextTrack);
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
