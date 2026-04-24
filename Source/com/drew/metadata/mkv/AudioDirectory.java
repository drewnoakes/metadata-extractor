package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

import static com.drew.metadata.mkv.ElementIDs.*;

public class AudioDirectory extends Directory
{
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<>();

    static
    {
        _tagNameMap.put(TRACK_NUMBER, "Track number");
        _tagNameMap.put(TRACK_UID, "Track UID");
        _tagNameMap.put(TRACK_TYPE, "Track type");
        _tagNameMap.put(TAG_LACING, "Tag lacing");
        _tagNameMap.put(CODEC_ID, "Codec ID");
        _tagNameMap.put(LANGUAGE, "Language");
        _tagNameMap.put(LANGUAGE_BCP47, "Language BCP47");
        _tagNameMap.put(DEFAULT_DURATION, "Default duration");
        _tagNameMap.put(CHANNELS, "Channels");
        _tagNameMap.put(SAMPLING_FREQUENCY, "Sampling frequency");
        _tagNameMap.put(BIT_DEPTH, "Bit depth");
    }

    public AudioDirectory()
    {
        this.setDescriptor(new TagDescriptor<Directory>(this));
    }

    @Override
    public String getName()
    {
        return "Audio";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
