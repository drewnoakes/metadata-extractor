package com.drew.imaging.quicktime;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;

public abstract class QtHandler<T extends Directory>
{
    protected Metadata metadata;
    protected T directory;

    public QtHandler(Metadata metadata) {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    protected abstract T getDirectory();

    protected abstract boolean shouldAcceptAtom(@NotNull String fourCC);

    protected abstract boolean shouldAcceptContainer(@NotNull String fourCC);

    protected abstract QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException;

    protected abstract QtHandler processContainer(@NotNull String fourCC);
}
