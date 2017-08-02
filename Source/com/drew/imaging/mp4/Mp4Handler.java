package com.drew.imaging.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.boxes.Box;

import java.io.IOException;

public abstract class Mp4Handler<T extends Mp4Directory>
{
    protected Metadata metadata;
    protected T directory;

    public Mp4Handler(Metadata metadata)
    {
        this.metadata = metadata;
        this.directory = getDirectory();
        metadata.addDirectory(directory);
    }

    protected abstract T getDirectory();

    protected abstract boolean shouldAcceptBox(@NotNull Box box);

    protected abstract boolean shouldAcceptContainer(@NotNull Box box);

    protected abstract Mp4Handler processBox(@NotNull Box box, @NotNull byte[] payload) throws IOException;

    protected Mp4Handler processContainer(@NotNull Box box) throws IOException
    {
        return processBox(box, null);
    }
}
