package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class Mp4Directory extends Directory {

    public static final int TAG_CREATION_TIME                           = 0x0100;
    public static final int TAG_MODIFICATION_TIME                       = 0x0101;
    public static final int TAG_TIME_SCALE                              = 0x0102;
    public static final int TAG_DURATION                                = 0x0103;
    public static final int TAG_PREFERRED_RATE                          = 0x0104;
    public static final int TAG_PREFERRED_VOLUME                        = 0x0105;
    public static final int TAG_PREVIEW_TIME                            = 0x0108;
    public static final int TAG_PREVIEW_DURATION                        = 0x0109;
    public static final int TAG_POSTER_TIME                             = 0x010A;
    public static final int TAG_SELECTION_TIME                          = 0x010B;
    public static final int TAG_SELECTION_DURATION                      = 0x010C;
    public static final int TAG_CURRENT_TIME                            = 0x010D;
    public static final int TAG_NEXT_TRACK_ID                           = 0x010E;
    public static final int TAG_TRANSFORMATION_MATRIX                   = 0x010F;
    public static final int TAG_MEDIA_TIME_SCALE                        = 0x0306;

    public static final int TAG_MAJOR_BRAND                             = 1;
    public static final int TAG_MINOR_VERSION                           = 2;
    public static final int TAG_COMPATIBLE_BRANDS                       = 3;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_MAJOR_BRAND, "Major Brand");
        _tagNameMap.put(TAG_MINOR_VERSION, "Minor Version");
        _tagNameMap.put(TAG_COMPATIBLE_BRANDS, "Compatible Brands");

        _tagNameMap.put(TAG_CREATION_TIME, "Creation Time");
        _tagNameMap.put(TAG_MODIFICATION_TIME, "Modification Time");
        _tagNameMap.put(TAG_TIME_SCALE, "Media Time Scale");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_PREFERRED_RATE, "Preferred Rate");
        _tagNameMap.put(TAG_PREFERRED_VOLUME, "Preferred Volume");
        _tagNameMap.put(TAG_PREVIEW_TIME, "Preview Time");
        _tagNameMap.put(TAG_PREVIEW_DURATION, "Preview Duration");
        _tagNameMap.put(TAG_POSTER_TIME, "Poster Time");
        _tagNameMap.put(TAG_SELECTION_TIME, "Selection Time");
        _tagNameMap.put(TAG_SELECTION_DURATION, "Selection Duration");
        _tagNameMap.put(TAG_CURRENT_TIME, "Current Time");
        _tagNameMap.put(TAG_NEXT_TRACK_ID, "Next Track ID");
        _tagNameMap.put(TAG_TRANSFORMATION_MATRIX, "Transformation Matrix");

        _tagNameMap.put(TAG_MEDIA_TIME_SCALE, "Media Time Scale");
    }

    public Mp4Directory()
    {
        this.setDescriptor(new Mp4Descriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "MP4";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
