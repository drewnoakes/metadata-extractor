package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtDirectory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class AtomHeaderMovie extends AtomFull
{
    long creationTime;
    long modificationTime;
    long timescale;
    long duration;
    int preferredRate;
    int preferredVolume;
    int[] matrixStructure;
    long previewTime;
    long previewDuration;
    long posterTime;
    long selectionTime;
    long selectionDuration;
    long currentTime;
    long nextTrackID;

    public AtomHeaderMovie(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        creationTime = reader.getUInt32();
        modificationTime = reader.getUInt32();
        timescale = reader.getUInt32();
        duration = reader.getUInt32();
        preferredRate = reader.getInt32();
        preferredVolume = reader.getInt16();
        reader.skip(10); // Reserved
        matrixStructure = new int[]{reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        previewTime = reader.getUInt32();
        previewDuration = reader.getUInt32();
        posterTime = reader.getUInt32();
        selectionTime = reader.getUInt32();
        selectionDuration = reader.getUInt32();
        currentTime = reader.getUInt32();
        nextTrackID = reader.getUInt32();
    }

    public void addMetadata(QtDirectory directory)
    {
        // Get creation/modification times
        Calendar calendar = Calendar.getInstance();
        calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
        Date date = calendar.getTime();
        long macToUnixEpochOffset = date.getTime();
        String creationTimeStamp = new Date(creationTime * 1000 + macToUnixEpochOffset).toString();
        String modificationTimeStamp = new Date(modificationTime * 1000 + macToUnixEpochOffset).toString();
        directory.setString(QtDirectory.TAG_CREATION_TIME, creationTimeStamp);
        directory.setString(QtDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);

        // Get duration and time scale
        duration = duration / timescale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(QtDirectory.TAG_DURATION, time);
        directory.setLong(QtDirectory.TAG_TIME_SCALE, timescale);

        // Calculate preferred rate fixed point
        double preferredRateInteger = (preferredRate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (preferredRate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        double preferredVolumeInteger = (preferredVolume & 0xFF00) >> 8;
        double preferredVolumeFraction = (preferredVolume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(QtDirectory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        directory.setLong(QtDirectory.TAG_PREVIEW_TIME, previewTime);
        directory.setLong(QtDirectory.TAG_PREVIEW_DURATION, previewDuration);
        directory.setLong(QtDirectory.TAG_POSTER_TIME, posterTime);
        directory.setLong(QtDirectory.TAG_SELECTION_TIME, selectionTime);
        directory.setLong(QtDirectory.TAG_SELECTION_DURATION, selectionDuration);
        directory.setLong(QtDirectory.TAG_CURRENT_TIME, currentTime);
        directory.setLong(QtDirectory.TAG_NEXT_TRACK_ID, nextTrackID);
    }
}
