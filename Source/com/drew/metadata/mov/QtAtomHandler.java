package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Payton Garland
 */
public class QtAtomHandler implements QtHandler
{
    private QtHandlerFactory handlerFactory = new QtHandlerFactory(this);

    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)
            || fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)
            || fourCC.equals(QtAtomTypes.ATOM_HANDLER)
            || fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC) {
        return fourCC.equals(QtContainerTypes.ATOM_TRACK)
            || fourCC.equals(QtContainerTypes.ATOM_USER_DATA)
            || fourCC.equals(QtContainerTypes.ATOM_METADATA)
            || fourCC.equals(QtContainerTypes.ATOM_MOVIE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA);
    }

    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
            processMovieHeader(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
            processFileType(directory, payload, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            int versionAndFlags = reader.getInt32();
            int predefined = reader.getInt32();
            String handler = new String(reader.getBytes(4));
            return handlerFactory.getHandler(handler);
        } else if (fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER)) {
            reader.skip(12);
            QtHandlerFactory.HANDLER_PARAM_TIME_SCALE = reader.getInt32();
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
    }

    private void processFileType(@NotNull QtDirectory directory, @NotNull byte[] payload, SequentialByteArrayReader reader) throws IOException
    {
        directory.setByteArray(QtDirectory.TAG_MAJOR_BRAND, reader.getBytes(4));

        directory.setByteArray(QtDirectory.TAG_MINOR_VERSION, reader.getBytes(4));

        ArrayList<String> compatibleBrands = new ArrayList<String>();
        int brandsCount = (payload.length - 8) / 4;
        for (int i = 8; i < (brandsCount * 4) + 8; i += 4) {
            compatibleBrands.add(new String(reader.getBytes(4)));
        }
        String[] compatibleBrandsReturn = new String[compatibleBrands.size()];
        directory.setStringArray(QtDirectory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(compatibleBrandsReturn));
//        if (!compatibleBrands.contains("qt  ")) {
//            throw new ImageProcessingException("Not a QuickTime movie file");
//        }
    }

    private void processMovieHeader(@NotNull QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);

        // Get creation/modification times
        long creationTime = ByteUtil.getUnsignedInt32(reader.getBytes(4), 0, true);
        long modificationTime = ByteUtil.getUnsignedInt32(reader.getBytes(4), 0, true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
        Date date = calendar.getTime();
        long macToUnixEpochOffset = date.getTime();
        String creationTimeStamp = new Date(creationTime*1000 + macToUnixEpochOffset).toString();
        String modificationTimeStamp = new Date(modificationTime*1000 + macToUnixEpochOffset).toString();
        directory.setString(QtDirectory.TAG_CREATION_TIME, creationTimeStamp);
        directory.setString(QtDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);

        // Get duration and time scale
        int timeScale = reader.getInt32();
        double duration = reader.getInt32();
        duration = duration / timeScale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(QtDirectory.TAG_DURATION, time);
        directory.setInt(QtDirectory.TAG_TIME_SCALE, timeScale);

        // Calculate preferred rate fixed point
        int preferredRate = reader.getInt32();
        double preferredRateInteger = (preferredRate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (preferredRate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        int preferredVolume = reader.getInt16();
        double preferredVolumeInteger = (preferredVolume & 0xFF00) >> 8;
        double preferredVolumeFraction = (preferredVolume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(QtDirectory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        // 10-byte reserved space at index 26
        reader.skip(10);
        // 36-byte matrix structure at index 36
        reader.skip(36);

        directory.setInt(QtDirectory.TAG_PREVIEW_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_PREVIEW_DURATION, reader.getInt32());
        directory.setInt(QtDirectory.TAG_POSTER_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_SELECTION_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_SELECTION_DURATION, reader.getInt32());
        directory.setInt(QtDirectory.TAG_CURRENT_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_NEXT_TRACK_ID, reader.getInt32());
    }
}
