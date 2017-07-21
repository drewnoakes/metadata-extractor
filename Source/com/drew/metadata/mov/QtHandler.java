package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public interface QtHandler
{
    public boolean shouldAcceptAtom(@NotNull String fourCC);

    public boolean shouldAcceptContainer(@NotNull String fourCC);

    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException;

    public QtHandler processContainer(@NotNull String fourCC);
}
