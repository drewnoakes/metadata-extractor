package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class QtDirectory extends Directory {

    public static final int TAG_MEDIA_TIME_SCALE                  = 1;
    public static final int TAG_FRAME_RATE                        = 2;
    public static final int TAG_SCREEN_WIDTH_PX                   = 3;
    public static final int TAG_SCREEN_HEIGHT_PX                  = 4;
    public static final int TAG_VIDEO_CODEC                       = 5;
    public static final int TAG_DURATION                          = 6;
    public static final int TAG_CREATION_TIMESTAMP                = 7;
    public static final int TAG_MODIFICATION_TIMESTAMP            = 8;
    public static final int TAG_CHANNELS                          = 9;
    public static final int TAG_SAMPLE_SIZE                       = 10;
    public static final int TAG_SAMPLE_RATE                       = 11;
    public static final int TAG_AUDIO_CODEC                       = 12;



    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_MEDIA_TIME_SCALE, "Media Time Scale");
        _tagNameMap.put(TAG_FRAME_RATE, "Frame Rate");
        _tagNameMap.put(TAG_SCREEN_WIDTH_PX, "Frame Width (px)");
        _tagNameMap.put(TAG_SCREEN_HEIGHT_PX, "Frame Height (px)");
        _tagNameMap.put(TAG_VIDEO_CODEC, "Video Codec");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_CREATION_TIMESTAMP, "Creation Timestamp");
        _tagNameMap.put(TAG_MODIFICATION_TIMESTAMP, "Modification Timestamp");
        _tagNameMap.put(TAG_CHANNELS, "Channels");
        _tagNameMap.put(TAG_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_SAMPLE_RATE, "Sample Rate");
        _tagNameMap.put(TAG_AUDIO_CODEC, "Audio Codec");
    }

    public QtDirectory()
    {
        this.setDescriptor(new QtDescriptor(this));
    }

    @Override
    @NotNull
    public String getName() { return "Quicktime"; }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
