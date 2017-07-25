package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class QtSubtitleDirectory extends QtDirectory
{

    public QtSubtitleDirectory()
    {
        this.setDescriptor(new QtSubtitleDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        // Not yet implemented
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QT Subtitle";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
