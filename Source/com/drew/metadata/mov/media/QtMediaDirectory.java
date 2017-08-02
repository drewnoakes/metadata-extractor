package com.drew.metadata.mov.media;

import com.drew.metadata.Directory;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public abstract class QtMediaDirectory extends QtDirectory
{
    public static final int TAG_CREATION_TIME = 0x5001;
    public static final int TAG_MODIFICATION_TIME = 0x5002;
    public static final int TAG_DURATION = 0x5003;

    protected static void addQtMediaTags(HashMap<Integer, String> map)
    {
        map.put(TAG_CREATION_TIME, "Creation Time");
        map.put(TAG_MODIFICATION_TIME, "Modification Time");
        map.put(TAG_DURATION, "Duration");
    }
}
