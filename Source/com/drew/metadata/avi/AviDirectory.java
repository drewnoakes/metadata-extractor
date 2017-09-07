package com.drew.metadata.avi;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds basic metadata from Avi files
 *
 * @author Payton Garland
 */
public class AviDirectory extends Directory
{

    public static final int TAG_FRAMES_PER_SECOND = 1;
    public static final int TAG_SAMPLES_PER_SECOND = 2;
    public static final int TAG_DURATION = 3;
    public static final int TAG_VIDEO_CODEC = 4;
    public static final int TAG_AUDIO_CODEC = 5;
    public static final int TAG_WIDTH = 6;
    public static final int TAG_HEIGHT = 7;
    public static final int TAG_STREAMS = 8;

    public static final String CHUNK_STREAM_HEADER = "strh";
    public static final String CHUNK_MAIN_HEADER = "avih";

    public static final String LIST_HEADER = "hdrl";
    public static final String LIST_STREAM_HEADER = "strl";

    public static final String FORMAT = "AVI ";

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FRAMES_PER_SECOND, "Frames Per Second");
        _tagNameMap.put(TAG_SAMPLES_PER_SECOND, "Samples Per Second");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_VIDEO_CODEC, "Video Codec");
        _tagNameMap.put(TAG_AUDIO_CODEC, "Audio Codec");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_STREAMS, "Stream Count");
    }

    public AviDirectory()
    {
        this.setDescriptor(new AviDescriptor(this));
    }

    @Override
    public String getName() {
        return "AVI";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
