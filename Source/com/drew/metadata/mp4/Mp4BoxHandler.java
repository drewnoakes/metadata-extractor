package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Source: http://l.web.umkc.edu/lizhu/teaching/2016sp.video-communication/ref/mp4.pdf
 *
 * @author Payton Garland
 */
public class Mp4BoxHandler extends QtHandler
{
    private Mp4HandlerFactory handlerFactory = new Mp4HandlerFactory(this);

    public Mp4BoxHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Mp4Directory getDirectory()
    {
        return new Mp4Directory();
    }

    @Override
    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return fourCC.equals(Mp4BoxTypes.BOX_FILE_TYPE)
            || fourCC.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)
            || fourCC.equals(Mp4BoxTypes.BOX_HANDLER)
            || fourCC.equals(Mp4BoxTypes.BOX_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(Mp4ContainerTypes.BOX_TRACK)
            || fourCC.equals(Mp4ContainerTypes.BOX_USER_DATA)
            || fourCC.equals(Mp4ContainerTypes.BOX_METADATA)
            || fourCC.equals(Mp4ContainerTypes.BOX_MOVIE)
            || fourCC.equals(Mp4ContainerTypes.BOX_MEDIA);
    }

    @Override
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        ByteArrayReader reader = new ByteArrayReader(payload);
        if (fourCC.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)) {
            processMovieHeader(directory, new SequentialByteArrayReader(payload));
        } else if (fourCC.equals(Mp4BoxTypes.BOX_FILE_TYPE)) {
            processFileType(directory, payload, reader);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_HANDLER)) {
            String handler = new String(reader.getBytes(8, 4));
            return handlerFactory.getHandler(handler, metadata);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_MEDIA_HEADER)) {
            processMediaHeader(directory, payload, new ByteArrayReader(payload));
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        if (fourCC.equals(Mp4ContainerTypes.BOX_COMPRESSED_MOVIE)) {
            directory.addError("Compressed QuickTime movies not supported");
        }
        return this;
    }

    /**
     * Extracts data from the 'ftyp' atom
     * Index 0 is after size and type
     */
    private void processFileType(@NotNull Directory directory, @NotNull byte[] payload, @NotNull ByteArrayReader reader) throws IOException
    {
        directory.setByteArray(Mp4Directory.TAG_MAJOR_BRAND, reader.getBytes(0, 4));

        ArrayList<String> compatibleBrands = new ArrayList<String>();
        int brandsCount = (payload.length - 8) / 4;
        for (int i = 8; i < (brandsCount * 4) + 8; i += 4) {
            compatibleBrands.add(new String(reader.getBytes(i, 4)));
        }
        String[] compatibleBrandsReturn = new String[compatibleBrands.size()];
        directory.setStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(compatibleBrandsReturn));
    }

    /**
     * Extracts data from the 'moov' atom's movie header marked by the fourCC 'mvhd'
     */
    private void processMovieHeader(@NotNull Directory directory, @NotNull SequentialByteArrayReader reader) throws IOException
    {
        long creationTime;
        long modificationTime;
        long timescale;
        long duration;

        int version = reader.getInt8();
        // Skip flags
        reader.skip(3);
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

        int rate = reader.getInt32();
        int volume = reader.getInt16();
        // Reserved, set to 0
        reader.skip(2);
        // Pre defined, set to 0
        reader.skip(8);
        int[] matrix = new int[]{reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        reader.skip(24);
        int nextTrackId = reader.getInt32();


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

        directory.setInt(Mp4Directory.TAG_NEXT_TRACK_ID, nextTrackId);
    }

    private void processMediaHeader(@NotNull Directory directory, @NotNull byte[] payload, @NotNull ByteArrayReader reader) throws IOException
    {
        int version = reader.getInt8(0);
        long creationTime;
        long modificationTime;
        long timescale;
        long duration;
        // Skip 3-bytes from flags
        switch (version) {
            case (1):
                creationTime = reader.getInt64(4);
                modificationTime = reader.getInt64(12);
                timescale = reader.getUInt32(20);
                duration = reader.getInt64(24);
                Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME = creationTime;
                Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME = modificationTime;
                Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE = timescale;
                Mp4HandlerFactory.HANDLER_PARAM_DURATION = duration;
                break;
            case (0):
                creationTime = reader.getUInt32(4);
                modificationTime = reader.getUInt32(8);
                timescale = reader.getUInt32(12);
                duration = reader.getUInt32(16);
                Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME = creationTime;
                Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME = modificationTime;
                Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE = timescale;
                Mp4HandlerFactory.HANDLER_PARAM_DURATION = duration;
                break;
        }
        // Skip 1-bit pad set to 0
        // Skip 15-bit language
        // Skip 16-bit pre-defined set to 0
    }
}
