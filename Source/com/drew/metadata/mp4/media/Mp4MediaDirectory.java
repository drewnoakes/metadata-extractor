package com.drew.metadata.mp4.media;

import com.drew.metadata.Directory;

import java.util.HashMap;

public abstract class Mp4MediaDirectory extends Directory
{
    public static final int TAG_CREATION_TIME = 0x1000;
    public static final int TAG_MODIFICATION_TIME = 0x1001;
    public static final int TAG_DURATION = 0x1002;
    public static final int TAG_LANGUAGE_CODE = 0x5004;

    protected static void addMp4MediaTags(HashMap<Integer, String> map)
    {
        map.put(TAG_CREATION_TIME, "Creation Time");
        map.put(TAG_MODIFICATION_TIME, "Modification Time");
        map.put(TAG_DURATION, "Duration");
        map.put(TAG_LANGUAGE_CODE, "ISO 639-2 Language Code");
    }
}
