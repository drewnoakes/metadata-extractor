package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.AtomFileTypeCompatibility;
import com.drew.metadata.mov.atoms.AtomHandlerReference;
import com.drew.metadata.mov.atoms.AtomHeaderMedia;
import com.drew.metadata.mov.atoms.AtomHeaderMovie;

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
        SequentialReader reader = new SequentialByteArrayReader(payload);

        if (fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
            AtomHeaderMovie atom = new AtomHeaderMovie(reader, baseAtom);
            atom.addMetadata(directory);
        } else if (fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
            AtomFileTypeCompatibility atom = new AtomFileTypeCompatibility(reader, baseAtom);
            atom.addMetadata(directory);
        } else if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            AtomHandlerReference atom = new AtomHandlerReference(reader, baseAtom);
            return handlerFactory.getHandler(atom.getComponentType(), metadata);
        } else if (fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER)) {
            AtomHeaderMedia atom = new AtomHeaderMedia(reader, baseAtom);
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
}
