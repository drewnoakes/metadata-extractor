package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;

public abstract class QtHandler<T extends QtDirectory>
{
    protected Metadata metadata;
    protected T directory;

    public QtHandler(Metadata metadata) {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    abstract T getDirectory();

    abstract boolean shouldAcceptAtom(@NotNull String fourCC);

    abstract boolean shouldAcceptContainer(@NotNull String fourCC);

    abstract QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException;

    abstract QtHandler processContainer(@NotNull String fourCC);
}
