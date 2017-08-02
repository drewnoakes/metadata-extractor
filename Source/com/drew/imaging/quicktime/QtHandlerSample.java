package com.drew.imaging.quicktime;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;

public abstract class QtHandlerSample<T extends Directory, U extends AtomSample>
{
    protected Metadata metadata;
    protected T directory;
    protected U baseAtom;

    public QtHandlerSample(Metadata metadata)
    {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    protected abstract U getAtom(SequentialReader reader) throws IOException;

    protected abstract T getDirectory();

    protected abstract boolean shouldAcceptAtom(@NotNull String fourCC);

    protected abstract boolean shouldAcceptContainer(@NotNull String fourCC);

    protected abstract QtHandlerSample processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException;

    protected QtHandlerSample processContainer(@NotNull String fourCC) throws IOException
    {
        return processAtom(fourCC, null);
    }
}
