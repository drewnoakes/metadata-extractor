package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class QtMediaSoundDirectory extends QtDirectory
{
    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT                            = 0x0301;
    public static final int TAG_NUMBER_OF_CHANNELS                      = 0x0302;
    public static final int TAG_AUDIO_SAMPLE_SIZE                       = 0x0303;
    public static final int TAG_AUDIO_SAMPLE_RATE                       = 0x0304;

    public static final int TAG_SOUND_BALANCE                           = 0x0305;

    public QtMediaSoundDirectory()
    {
        this.setDescriptor(new QtDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_AUDIO_FORMAT, "Audio Format");
        _tagNameMap.put(TAG_NUMBER_OF_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_SIZE, "Audio Sample Size");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_RATE, "Audio Sample Rate");

        _tagNameMap.put(TAG_SOUND_BALANCE, "Sound Balance");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QuickTime Sound";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
