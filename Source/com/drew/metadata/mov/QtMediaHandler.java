package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Classes that extend this class should be from the media dat atom types:
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
 */
public abstract class QtMediaHandler extends QtHandler
{
    public QtMediaHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(getMediaInformation())
            || fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)
            || fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_SAMPLE_TABLE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA_BASE)
            || fourCC.equals("tmcd");
    }

    @Override
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        ByteArrayReader reader = new ByteArrayReader(payload);
        if (fourCC.equals(getMediaInformation())) {
            processMediaInformation(reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE)) {
            processTimeToSample(reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
    }

    abstract String getMediaInformation();

    /**
     * All sample description atoms will begin with the structure specified here:
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-33044
     *
     * Unique values will follow depending upon the handler
     */
    abstract void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException;

    /**
     * Media information atoms will be one of three types: 'vmhd', 'smhd', or 'gmhd'
     *
     * 'gmhd' atoms will have a unique child specific to the media type you are dealing with
     *
     * Each structure will be specified in its respective handler
     */
    abstract void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException;

    abstract void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException;
}
