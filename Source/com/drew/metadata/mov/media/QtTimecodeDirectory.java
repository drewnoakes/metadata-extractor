package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class QtTimecodeDirectory extends QtDirectory
{
    // Timecode Media Description Atom
    public static final int TAG_DROP_FRAME                      = 1;
    public static final int TAG_24_HOUR_MAX                     = 2;
    public static final int TAG_NEGATIVE_TIMES_OK               = 3;
    public static final int TAG_COUNTER                         = 4;
    public static final int TAG_TEXT_FONT                       = 5;
    public static final int TAG_TEXT_FACE                       = 6;
    public static final int TAG_TEXT_SIZE                       = 7;
    public static final int TAG_TEXT_COLOR                      = 8;
    public static final int TAG_BACKGROUND_COLOR                = 9;
    public static final int TAG_FONT_NAME                       = 10;

    public QtTimecodeDirectory()
    {
        this.setDescriptor(new QtTimecodeDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_DROP_FRAME, "Drop Frame");
        _tagNameMap.put(TAG_24_HOUR_MAX, "24 Hour Max");
        _tagNameMap.put(TAG_NEGATIVE_TIMES_OK, "Negative Times OK");
        _tagNameMap.put(TAG_COUNTER, "Counter");
        _tagNameMap.put(TAG_TEXT_FONT, "Text Font");
        _tagNameMap.put(TAG_TEXT_FACE, "Text Face");
        _tagNameMap.put(TAG_TEXT_SIZE, "Text Size");
        _tagNameMap.put(TAG_TEXT_COLOR, "Text Color");
        _tagNameMap.put(TAG_BACKGROUND_COLOR, "Background Color");
        _tagNameMap.put(TAG_FONT_NAME, "Font Name");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QT Timecode";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
