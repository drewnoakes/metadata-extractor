package com.drew.imaging.quicktime;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;


public abstract class QtHandler<T extends QtDirectory>
{
    protected Metadata metadata;
    protected T directory;

    public QtHandler(Metadata metadata)
    {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    protected abstract T getDirectory();

    protected abstract boolean shouldAcceptAtom(@NotNull Atom atom);

    protected abstract boolean shouldAcceptContainer(@NotNull Atom atom);

    protected abstract QtHandler processAtom(@NotNull Atom atom, @NotNull byte[] payload) throws IOException;

    protected QtHandler processContainer(@NotNull Atom atom) throws IOException
    {
        return processAtom(atom, null);
    }
}
