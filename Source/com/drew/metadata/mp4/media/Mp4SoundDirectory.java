package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class Mp4SoundDirectory extends Mp4MediaDirectory
{
    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT                            = 101;
    public static final int TAG_NUMBER_OF_CHANNELS                      = 102;
    public static final int TAG_AUDIO_SAMPLE_SIZE                       = 103;
    public static final int TAG_AUDIO_SAMPLE_RATE                       = 104;

    public static final int TAG_SOUND_BALANCE                           = 105;

    public Mp4SoundDirectory()
    {
        this.setDescriptor(new Mp4SoundDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
        _tagNameMap.put(TAG_AUDIO_FORMAT, "Format");
        _tagNameMap.put(TAG_NUMBER_OF_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_RATE, "Sample Rate");
        _tagNameMap.put(TAG_SOUND_BALANCE, "Balance");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "MP4 Sound";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
