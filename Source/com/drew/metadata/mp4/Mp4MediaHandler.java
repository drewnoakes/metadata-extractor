package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mp4.media.Mp4MediaDirectory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Classes that extend this class should be from the media dat atom types:
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
 */
public abstract class Mp4MediaHandler<T extends Mp4MediaDirectory> extends Mp4Handler<T>
{
    public Mp4MediaHandler(Metadata metadata)
    {
        super(metadata);
        if (Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME != null && Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME != null) {
            // Get creation/modification times
            Calendar calendar = Calendar.getInstance();
            calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
            Date date = calendar.getTime();
            long macToUnixEpochOffset = date.getTime();
            String creationTimeStamp = new Date(Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME * 1000 + macToUnixEpochOffset).toString();
            String modificationTimeStamp = new Date(Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME * 1000 + macToUnixEpochOffset).toString();
            directory.setString(Mp4MediaDirectory.TAG_CREATION_TIME, creationTimeStamp);
            directory.setString(Mp4MediaDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);
        }
    }

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(getMediaInformation())
            || fourCC.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)
            || fourCC.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(Mp4ContainerTypes.BOX_SAMPLE_TABLE)
            || fourCC.equals(Mp4ContainerTypes.BOX_MEDIA_INFORMATION);
    }

    @Override
    public Mp4Handler<T> processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        SequentialReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(getMediaInformation())) {
            processMediaInformation(reader);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)) {
            processSampleDescription(reader);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE)) {
            processTimeToSample(reader);
        }
        return this;
    }

    @Override
    public Mp4Handler processContainer(String fourCC)
    {
        return this;
    }

    protected abstract String getMediaInformation();

    /**
     * All sample description atoms will begin with the structure specified here:
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-33044
     *
     * Unique values will follow depending upon the handler
     */
    protected abstract void processSampleDescription(@NotNull SequentialReader reader) throws IOException;

    /**
     * Media information atoms will be one of three types: 'vmhd', 'smhd', or 'gmhd'
     *
     * 'gmhd' atoms will have a unique child specific to the media type you are dealing with
     *
     * Each structure will be specified in its respective handler
     */
    protected abstract void processMediaInformation(@NotNull SequentialReader reader) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader) throws IOException;
}
