package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.media.QtMediaDirectory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Classes that extend this class should be from the media dat atom types:
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW1
 */
public abstract class QtMediaHandler<T extends QtDirectory> extends QtHandler<T>
{
    public QtMediaHandler(Metadata metadata)
    {
        super(metadata);
        if (QtHandlerFactory.HANDLER_PARAM_CREATION_TIME != null && QtHandlerFactory.HANDLER_PARAM_MODIFICATION_TIME != null) {
            // Get creation/modification times
            Calendar calendar = Calendar.getInstance();
            calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
            Date date = calendar.getTime();
            long macToUnixEpochOffset = date.getTime();
            String creationTimeStamp = new Date(QtHandlerFactory.HANDLER_PARAM_CREATION_TIME * 1000 + macToUnixEpochOffset).toString();
            String modificationTimeStamp = new Date(QtHandlerFactory.HANDLER_PARAM_MODIFICATION_TIME * 1000 + macToUnixEpochOffset).toString();
            directory.setString(QtMediaDirectory.TAG_CREATION_TIME, creationTimeStamp);
            directory.setString(QtMediaDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);
        }
    }

    @Override
    public boolean shouldAcceptAtom(Atom atom)
    {
        return atom.type.equals(getMediaInformation())
            || atom.type.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)
            || atom.type.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(Atom atom)
    {
        return atom.type.equals(QtContainerTypes.ATOM_SAMPLE_TABLE)
            || atom.type.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION)
            || atom.type.equals(QtContainerTypes.ATOM_MEDIA_BASE)
            || atom.type.equals("tmcd");
    }

    @Override
    public QtMediaHandler processAtom(@NotNull Atom atom, @NotNull byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(getMediaInformation())) {
                processMediaInformation(reader, atom);
            } else if (atom.type.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
                processSampleDescription(reader, atom);
            } else if (atom.type.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE)) {
                processTimeToSample(reader, atom);
            }
        }
        return this;
    }

    protected abstract String getMediaInformation();

    protected abstract void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException;
}
