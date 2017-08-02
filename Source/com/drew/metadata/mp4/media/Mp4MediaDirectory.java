package com.drew.metadata.mp4.media;

import com.drew.metadata.Directory;
import com.drew.metadata.mp4.Mp4Directory;

import java.util.HashMap;

public abstract class Mp4MediaDirectory extends Mp4Directory
{
    public static final int TAG_CREATION_TIME = 101;
    public static final int TAG_MODIFICATION_TIME = 102;
    public static final int TAG_DURATION = 103;
    public static final int TAG_LANGUAGE_CODE = 104;

    protected static void addMp4MediaTags(HashMap<Integer, String> map)
    {
        map.put(TAG_CREATION_TIME, "Creation Time");
        map.put(TAG_MODIFICATION_TIME, "Modification Time");
        map.put(TAG_DURATION, "Duration");
        map.put(TAG_LANGUAGE_CODE, "ISO 639-2 Language Code");
    }
}
