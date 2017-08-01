package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class Mp4MetaDirectory extends Mp4MediaDirectory
{
    public Mp4MetaDirectory()
    {
        this.setDescriptor(new Mp4MetaDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
    }

    @Override
    public String getName()
    {
        return "MP4 Metadata";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return null;
    }
}
