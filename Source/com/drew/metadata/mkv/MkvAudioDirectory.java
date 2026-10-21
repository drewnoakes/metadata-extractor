package com.drew.metadata.mkv;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

public class MkvAudioDirectory extends MkvTrackDirectory
{
    public static final int TAG_CHANNELS = ElementIDs.CHANNELS;
    public static final int TAG_SAMPLING_FREQUENCY = ElementIDs.SAMPLING_FREQUENCY;
    public static final int TAG_BIT_DEPTH = ElementIDs.BIT_DEPTH;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        addTrackTags(_tagNameMap);
        _tagNameMap.put(TAG_CHANNELS, "Channels");
        _tagNameMap.put(TAG_SAMPLING_FREQUENCY, "Sampling Frequency");
        _tagNameMap.put(TAG_BIT_DEPTH, "Bit Depth");
    }

    public MkvAudioDirectory()
    {
        setDescriptor(new MkvAudioDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Audio";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
