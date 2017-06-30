package com.drew.metadata.mp3;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class Mp3Directory extends Directory
{

    public static final int TAG_ID = 1;
    public static final int TAG_LAYER = 2;
    public static final int TAG_BITRATE = 3;
    public static final int TAG_FREQUENCY = 4;
    public static final int TAG_MODE = 5;
    public static final int TAG_EMPHASIS = 6;
    public static final int TAG_COPYRIGHT = 7;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_ID, "ID");
        _tagNameMap.put(TAG_LAYER, "Layer");
        _tagNameMap.put(TAG_BITRATE, "Bitrate");
        _tagNameMap.put(TAG_FREQUENCY, "Frequency");
        _tagNameMap.put(TAG_MODE, "Mode");
        _tagNameMap.put(TAG_EMPHASIS, "Emphasis Method");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
    }

    public Mp3Directory()
    {
        this.setDescriptor(new Mp3Descriptor(this));
    }

    @Override
    public String getName()
    {
        return "MP3";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
