package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Payton Garland
 */
public class QtAtomHandler extends QtHandler
{
    private QtHandlerFactory handlerFactory = new QtHandlerFactory(this);

    public QtAtomHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtDirectory getDirectory()
    {
        return new QtDirectory();
    }

    @Override
    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)
            || fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)
            || fourCC.equals(QtAtomTypes.ATOM_HANDLER)
            || fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_TRACK)
            || fourCC.equals(QtContainerTypes.ATOM_USER_DATA)
            || fourCC.equals(QtContainerTypes.ATOM_METADATA)
            || fourCC.equals(QtContainerTypes.ATOM_MOVIE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA);
    }

    @Override
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        ByteArrayReader reader = new ByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
            processMovieHeader(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
            processFileType(directory, payload, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            String handler = new String(reader.getBytes(8, 4));
            return handlerFactory.getHandler(handler, metadata);
        } else if (fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER)) {
            QtHandlerFactory.HANDLER_PARAM_TIME_SCALE = reader.getInt32(12);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        if (fourCC.equals(QtContainerTypes.ATOM_COMPRESSED_MOVIE)) {
            directory.addError("Compressed QuickTime movies not supported");
        }
        return this;
    }

    /**
     * Extracts data from the 'ftyp' atom
     * Index 0 is after size and type
     *
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html#//apple_ref/doc/uid/TP40000939-CH203-CJBCBIFF
     *
     */
    private void processFileType(@NotNull QtDirectory directory, @NotNull byte[] payload, @NotNull ByteArrayReader reader) throws IOException
    {
        directory.setByteArray(QtDirectory.TAG_MAJOR_BRAND, reader.getBytes(0, 4));

        ArrayList<String> compatibleBrands = new ArrayList<String>();
        int brandsCount = (payload.length - 8) / 4;
        for (int i = 8; i < (brandsCount * 4) + 8; i += 4) {
            compatibleBrands.add(new String(reader.getBytes(i, 4)));
        }
        String[] compatibleBrandsReturn = new String[compatibleBrands.size()];
        directory.setStringArray(QtDirectory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(compatibleBrandsReturn));
    }

    /**
     * Extracts data from the 'moov' atom's movie header marked by the fourCC 'mvhd'
     * Index 0 is after size and type
     *
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-32947
     *
     */
    private void processMovieHeader(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        // Get creation/modification times
        long creationTime = ByteUtil.getUnsignedInt32(reader.getBytes(4,4), 0, true);
        long modificationTime = ByteUtil.getUnsignedInt32(reader.getBytes(8, 4), 0, true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
        Date date = calendar.getTime();
        long macToUnixEpochOffset = date.getTime();
        String creationTimeStamp = new Date(creationTime*1000 + macToUnixEpochOffset).toString();
        String modificationTimeStamp = new Date(modificationTime*1000 + macToUnixEpochOffset).toString();
        directory.setString(QtDirectory.TAG_CREATION_TIME, creationTimeStamp);
        directory.setString(QtDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);

        // Get duration and time scale
        int timeScale = reader.getInt32(12);
        double duration = reader.getInt32(16);
        duration = duration / timeScale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(QtDirectory.TAG_DURATION, time);
        directory.setInt(QtDirectory.TAG_TIME_SCALE, timeScale);

        // Calculate preferred rate fixed point
        int preferredRate = reader.getInt32(20);
        double preferredRateInteger = (preferredRate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (preferredRate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        int preferredVolume = reader.getInt16(24);
        double preferredVolumeInteger = (preferredVolume & 0xFF00) >> 8;
        double preferredVolumeFraction = (preferredVolume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(QtDirectory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        directory.setInt(QtDirectory.TAG_PREVIEW_TIME, reader.getInt32(72));
        directory.setInt(QtDirectory.TAG_PREVIEW_DURATION, reader.getInt32(76));
        directory.setInt(QtDirectory.TAG_POSTER_TIME, reader.getInt32(80));
        directory.setInt(QtDirectory.TAG_SELECTION_TIME, reader.getInt32(84));
        directory.setInt(QtDirectory.TAG_SELECTION_DURATION, reader.getInt32(88));
        directory.setInt(QtDirectory.TAG_CURRENT_TIME, reader.getInt32(92));
        directory.setInt(QtDirectory.TAG_NEXT_TRACK_ID, reader.getInt32(96));
    }
}
