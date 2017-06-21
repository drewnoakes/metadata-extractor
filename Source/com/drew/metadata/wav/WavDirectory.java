package com.drew.metadata.wav;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class WavDirectory extends Directory
{
    public static final int TAG_FORMAT = 1;
    public static final int TAG_CHANNELS = 2;
    public static final int TAG_SAMPLES_PER_SEC = 3;
    public static final int TAG_BYTES_PER_SEC = 4;
    public static final int TAG_BLOCK_ALIGNMENT = 5;
    public static final int TAG_BITS_PER_SAMPLE = 6;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FORMAT, "Format");
        _tagNameMap.put(TAG_CHANNELS, "Channels");
        _tagNameMap.put(TAG_SAMPLES_PER_SEC, "Samples Per Second");
        _tagNameMap.put(TAG_BYTES_PER_SEC, "Bytes Per Second");
        _tagNameMap.put(TAG_BLOCK_ALIGNMENT, "Block Alignment");
        _tagNameMap.put(TAG_BITS_PER_SAMPLE, "Bits Per Sample");
    }

    public WavDirectory()
    {
        this.setDescriptor(new WavDescriptor(this));
    }

    @Override
    public String getName() {
        return "WAV";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
