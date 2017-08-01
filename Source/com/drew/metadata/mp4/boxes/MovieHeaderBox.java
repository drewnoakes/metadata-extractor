package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MovieHeaderBox extends FullBox
{
    protected long creationTime;
    protected long modificationTime;
    protected long timescale;
    protected long duration;
    protected int rate;
    protected int volume;
    protected int[] matrix;
    protected long nextTrackID;

    public MovieHeaderBox(SequentialReader reader) throws IOException
    {
        super(reader);
        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            timescale = reader.getUInt32();
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            timescale = reader.getUInt32();
            duration = reader.getUInt32();
        }
        rate = reader.getInt32();
        volume = reader.getInt16();
        reader.skip(2); // Reserved
        reader.skip(8); // Reserved
        matrix = new int[]{reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        reader.skip(24); // Pre-defined
        nextTrackID = reader.getUInt32();
    }

    public void addMetadata(Mp4Directory directory)
    {
        // Get creation/modification times
        Calendar calendar = Calendar.getInstance();
        calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
        Date date = calendar.getTime();
        long macToUnixEpochOffset = date.getTime();
        String creationTimeStamp = new Date(creationTime*1000 + macToUnixEpochOffset).toString();
        String modificationTimeStamp = new Date(modificationTime*1000 + macToUnixEpochOffset).toString();
        directory.setString(Mp4Directory.TAG_CREATION_TIME, creationTimeStamp);
        directory.setString(Mp4Directory.TAG_MODIFICATION_TIME, modificationTimeStamp);

        // Get duration and time scale
        duration = duration / timescale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(Mp4Directory.TAG_DURATION, time);
        directory.setLong(Mp4Directory.TAG_TIME_SCALE, timescale);

        directory.setIntArray(Mp4Directory.TAG_TRANSFORMATION_MATRIX, matrix);

        // Calculate preferred rate fixed point
        double preferredRateInteger = (rate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (rate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4Directory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        double preferredVolumeInteger = (volume & 0xFF00) >> 8;
        double preferredVolumeFraction = (volume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(Mp4Directory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        directory.setLong(Mp4Directory.TAG_NEXT_TRACK_ID, nextTrackID);
    }
}
