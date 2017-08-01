package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class Mp4HintDirectory extends Mp4MediaDirectory
{
    public static final int TAG_MAX_PDU_SIZE = 1;
    public static final int TAG_AVERAGE_PDU_SIZE = 2;
    public static final int TAG_MAX_BITRATE = 3;
    public static final int TAG_AVERAGE_BITRATE = 4;

    public Mp4HintDirectory()
    {
        this.setDescriptor(new Mp4HintDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
        _tagNameMap.put(TAG_MAX_PDU_SIZE, "Max PDU Size");
        _tagNameMap.put(TAG_AVERAGE_PDU_SIZE, "Average PDU Size");
        _tagNameMap.put(TAG_MAX_BITRATE, "Max Bitrate");
        _tagNameMap.put(TAG_AVERAGE_BITRATE, "Average Bitrate");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "MP4 Hint";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
