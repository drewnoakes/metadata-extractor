package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public abstract class QtMediaHandler implements QtHandler
{
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
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException
    {
        ByteArrayReader reader = new ByteArrayReader(payload);
        if (fourCC.equals(getMediaInformation())) {
            processMediaInformation(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE)) {
            processTimeToSample(directory, reader);
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
     * All sample description atoms will start with the following:
     * <ul>
     *     <li>Version: 1 byte</li>
     *     <li>Flags: 3 bytes</li>
     *     <li>Number of entries: 4 bytes</li>
     *     <li>Sample Description Size: 4 bytes</li>
     *     <li>Data Format: 4 bytes</li>
     *     <li>Reserved: 6 bytes</li>
     *     <li>Data reference index: 2 bytes</li>
     * </ul>
     * As per the documentation
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-33044
     *
     * Unique values will follow depending upon the handler
     */
    abstract void processSampleDescription(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException;

    /**
     * Media information atoms will be one of three types: 'vmhd', 'smhd', or 'gmhd'
     *
     * Each structure will be specified in its respective handler
     */
    abstract void processMediaInformation(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException;

    abstract void processTimeToSample(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException;
}
