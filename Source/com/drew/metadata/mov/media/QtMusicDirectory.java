package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class QtMusicDirectory extends Directory
{
    public QtMusicDirectory()
    {
        this.setDescriptor(new QtMusicDescriptor(this));
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
        return "QT Music";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
