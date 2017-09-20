package com.drew.imaging.heif;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifDirectory;
import com.drew.metadata.heif.boxes.Box;

import java.io.IOException;

public abstract class HeifHandler<T extends HeifDirectory>
{
    protected Metadata metadata;
    protected T directory;

    public HeifHandler(Metadata metadata)
    {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    protected abstract T getDirectory();

    protected abstract boolean shouldAcceptBox(@NotNull Box box);

    protected abstract boolean shouldAcceptContainer(@NotNull Box box);

    protected abstract HeifHandler processBox(@NotNull Box box, @NotNull byte[] payload) throws IOException;

    /**
     * There is potential for a box to both contain other boxes and contain information, so this method will
     * handle those occurences.
     */
    protected abstract void processContainer(@NotNull Box box, @NotNull SequentialReader reader) throws IOException;
}
