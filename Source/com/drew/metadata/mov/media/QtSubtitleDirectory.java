package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class QtSubtitleDirectory extends Directory
{
    public static final int TAG_VERTICAL_PLACEMENT          = 1;
    public static final int TAG_SOME_SAMPLES_FORCED         = 2;
    public static final int TAG_ALL_SAMPLES_FORCED          = 3;
    public static final int TAG_DEFAULT_TEXT_BOX            = 4;
    public static final int TAG_FONT_IDENTIFIER             = 5;
    public static final int TAG_FONT_FACE                   = 6;
    public static final int TAG_FONT_SIZE                   = 7;
    public static final int TAG_FOREGROUND_COLOR            = 8;

    public QtSubtitleDirectory()
    {
        this.setDescriptor(new QtSubtitleDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_VERTICAL_PLACEMENT, "Vertical Placement");
        _tagNameMap.put(TAG_SOME_SAMPLES_FORCED, "Some Samples Forced");
        _tagNameMap.put(TAG_ALL_SAMPLES_FORCED, "All Samples Forced");
        _tagNameMap.put(TAG_DEFAULT_TEXT_BOX, "Default Text Box");
        _tagNameMap.put(TAG_FONT_IDENTIFIER, "Font Identifier");
        _tagNameMap.put(TAG_FONT_FACE, "Font Face");
        _tagNameMap.put(TAG_FONT_SIZE, "Font Size");
        _tagNameMap.put(TAG_FOREGROUND_COLOR, "Foreground Color");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QT Subtitle";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
