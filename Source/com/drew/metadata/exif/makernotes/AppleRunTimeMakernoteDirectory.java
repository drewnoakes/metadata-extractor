package com.drew.metadata.exif.makernotes;

import java.util.HashMap;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

public class AppleRunTimeMakernoteDirectory extends Directory
{
    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    public static final int CMTimeFlags = 1;
    public static final int CMTimeEpoch = 2;
    public static final int CMTimeScale = 3;
    public static final int CMTimeValue = 4;

    static
    {
        _tagNameMap.put(CMTimeFlags, "Flags");
        _tagNameMap.put(CMTimeEpoch, "Epoch");
        _tagNameMap.put(CMTimeScale, "Scale");
        _tagNameMap.put(CMTimeValue, "Value");
    }

    public AppleRunTimeMakernoteDirectory()
    {
        super.setDescriptor(new AppleRunTimeMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Apple Run Time";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

}
