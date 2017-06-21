package com.drew.metadata.avi;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class AviDirectory extends Directory
{

    public static final int TAG_FRAMES_PER_SECOND = 1;
    public static final int TAG_SAMPLES_PER_SECOND = 2;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FRAMES_PER_SECOND, "Frames Per Second");
        _tagNameMap.put(TAG_SAMPLES_PER_SECOND, "Samples Per Second");
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
