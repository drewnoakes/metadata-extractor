package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class QtSoundDirectory extends QtDirectory
{
    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT                            = 0x0301;
    public static final int TAG_NUMBER_OF_CHANNELS                      = 0x0302;
    public static final int TAG_AUDIO_SAMPLE_SIZE                       = 0x0303;
    public static final int TAG_AUDIO_SAMPLE_RATE                       = 0x0304;

    public static final int TAG_SOUND_BALANCE                           = 0x0305;

    public QtSoundDirectory()
    {
        this.setDescriptor(new QtSoundDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        QtMediaDirectory.addQtMediaTags(_tagNameMap);
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
        return "QT Sound";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
