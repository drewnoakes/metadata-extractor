package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.*;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QtAtomHandler extends QtHandler<QtDirectory>
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
    public boolean shouldAcceptAtom(@NotNull Atom atom)
    {
        return atom.type.equals(QtAtomTypes.ATOM_FILE_TYPE)
            || atom.type.equals(QtAtomTypes.ATOM_MOVIE_HEADER)
            || atom.type.equals(QtAtomTypes.ATOM_HANDLER)
            || atom.type.equals(QtAtomTypes.ATOM_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(Atom atom)
    {
        return atom.type.equals(QtContainerTypes.ATOM_TRACK)
            || atom.type.equals(QtContainerTypes.ATOM_USER_DATA)
            || atom.type.equals(QtContainerTypes.ATOM_METADATA)
            || atom.type.equals(QtContainerTypes.ATOM_MOVIE)
            || atom.type.equals(QtContainerTypes.ATOM_MEDIA);
    }

    @Override
    public QtHandler processAtom(@NotNull Atom atom, @NotNull byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);

            if (atom.type.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
                MovieHeaderAtom movieHeaderAtom = new MovieHeaderAtom(reader, atom);
                movieHeaderAtom.addMetadata(directory);
            } else if (atom.type.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
                FileTypeCompatibilityAtom fileTypeCompatibilityAtom = new FileTypeCompatibilityAtom(reader, atom);
                fileTypeCompatibilityAtom.addMetadata(directory);
            } else if (atom.type.equals(QtAtomTypes.ATOM_HANDLER)) {
                HandlerReferenceAtom handlerReferenceAtom = new HandlerReferenceAtom(reader, atom);
                return handlerFactory.getHandler(handlerReferenceAtom.getComponentType(), metadata);
            } else if (atom.type.equals(QtAtomTypes.ATOM_MEDIA_HEADER)) {
                MediaHeaderAtom mediaHeaderAtom = new MediaHeaderAtom(reader, atom);
            }
        } else {
            if (atom.type.equals(QtContainerTypes.ATOM_COMPRESSED_MOVIE)) {
                directory.addError("Compressed QuickTime movies not supported");
            }
        }
        return this;
    }
}
