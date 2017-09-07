package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QtDirectory;

import java.util.HashMap;

public class QtTextDirectory extends QtDirectory
{
    // Text Media Description Atom
    public static final int TAG_AUTO_SCALE                          = 1;
    public static final int TAG_MOVIE_BACKGROUND_COLOR              = 2;
    public static final int TAG_SCROLL_IN                           = 3;
    public static final int TAG_SCROLL_OUT                          = 4;
    public static final int TAG_HORIZONTAL_SCROLL                   = 5;
    public static final int TAG_REVERSE_SCROLL                      = 6;
    public static final int TAG_CONTINUOUS_SCROLL                   = 7;
    public static final int TAG_DROP_SHADOW                         = 8;
    public static final int TAG_ANTI_ALIAS                          = 9;
    public static final int TAG_KEY_TEXT                            = 10;
    public static final int TAG_JUSTIFICATION                       = 11;
    public static final int TAG_BACKGROUND_COLOR                    = 12;
    public static final int TAG_DEFAULT_TEXT_BOX                    = 13;
    public static final int TAG_FONT_NUMBER                         = 14;
    public static final int TAG_FONT_FACE                           = 15;
    public static final int TAG_FOREGROUND_COLOR                    = 16;
    public static final int TAG_NAME                                = 17;

    public QtTextDirectory()
    {
        this.setDescriptor(new QtTextDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        QtMediaDirectory.addQtMediaTags(_tagNameMap);
        _tagNameMap.put(TAG_AUTO_SCALE, "Auto Scale");
        _tagNameMap.put(TAG_MOVIE_BACKGROUND_COLOR, "Use Background Color");
        _tagNameMap.put(TAG_SCROLL_IN, "Scroll In");
        _tagNameMap.put(TAG_SCROLL_OUT, "Scroll Out");
        _tagNameMap.put(TAG_HORIZONTAL_SCROLL, "Scroll Orientation");
        _tagNameMap.put(TAG_REVERSE_SCROLL, "Scroll Direction");
        _tagNameMap.put(TAG_CONTINUOUS_SCROLL, "Continuous Scroll");
        _tagNameMap.put(TAG_DROP_SHADOW, "Drop Shadow");
        _tagNameMap.put(TAG_ANTI_ALIAS, "Anti-aliasing");
        _tagNameMap.put(TAG_KEY_TEXT, "Display Text Background Color");
        _tagNameMap.put(TAG_JUSTIFICATION, "Alignment");
        _tagNameMap.put(TAG_BACKGROUND_COLOR, "Background Color");
        _tagNameMap.put(TAG_DEFAULT_TEXT_BOX, "Default Text Box");
        _tagNameMap.put(TAG_FONT_NUMBER, "Font Number");
        _tagNameMap.put(TAG_FONT_FACE, "Font Face");
        _tagNameMap.put(TAG_FOREGROUND_COLOR, "Foreground Color");
        _tagNameMap.put(TAG_NAME, "Font Name");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QT Text";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
