package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class QtDirectory extends Directory {

    // Movie Header Atom (https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-56313)
    public static final int TAG_CREATION_TIME = 0x0100;
    public static final int TAG_MODIFICATION_TIME = 0x0101;
    public static final int TAG_TIME_SCALE = 0x0102;
    public static final int TAG_DURATION = 0x0103;
    public static final int TAG_PREFERRED_RATE = 0x0104;
    public static final int TAG_PREFERRED_VOLUME = 0x0105;
    public static final int TAG_PREVIEW_TIME = 0x0108;
    public static final int TAG_PREVIEW_DURATION = 0x0109;
    public static final int TAG_POSTER_TIME = 0x010A;
    public static final int TAG_SELECTION_TIME = 0x010B;
    public static final int TAG_SELECTION_DURATION = 0x010C;
    public static final int TAG_CURRENT_TIME = 0x010D;
    public static final int TAG_NEXT_TRACK_ID = 0x010E;

    // Video Sample Description Atom
    public static final int TAG_VENDOR = 0X0201;
    public static final int TAG_TEMPORAL_QUALITY = 0x0202;
    public static final int TAG_SPATIAL_QUALITY = 0x0203;
    public static final int TAG_WIDTH = 0x0204;
    public static final int TAG_HEIGHT = 0x0205;
    public static final int TAG_HORIZONTAL_RESOLUTION = 0x0206;
    public static final int TAG_VERTICAL_RESOLUTION = 0x0207;
    public static final int TAG_COMPRESSOR_NAME = 0x0208;
    public static final int TAG_DEPTH = 0x0209;
    public static final int TAG_COMPRESSION_TYPE = 0x020A;

    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT = 0x0301;
    public static final int TAG_NUMBER_OF_CHANNELS = 0x0302;
    public static final int TAG_SAMPLE_SIZE = 0x0303;
    public static final int TAG_SAMPLE_RATE = 0x0304;


    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_CREATION_TIME, "Creation Time");
        _tagNameMap.put(TAG_MODIFICATION_TIME, "Modification Time");
        _tagNameMap.put(TAG_TIME_SCALE, "Time Scale");
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

        _tagNameMap.put(TAG_VENDOR, "Vendor");
        _tagNameMap.put(TAG_TEMPORAL_QUALITY, "Temporal Quality");
        _tagNameMap.put(TAG_SPATIAL_QUALITY, "Spatial Quality");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_HORIZONTAL_RESOLUTION, "Horizontal Resolution");
        _tagNameMap.put(TAG_VERTICAL_RESOLUTION, "Vertical Resolution");
        _tagNameMap.put(TAG_COMPRESSOR_NAME, "Compressor Name");
        _tagNameMap.put(TAG_DEPTH, "Depth");
        _tagNameMap.put(TAG_COMPRESSION_TYPE, "Compression Type");

        _tagNameMap.put(TAG_AUDIO_FORMAT, "Audio Format");
        _tagNameMap.put(TAG_NUMBER_OF_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_SAMPLE_RATE, "Sample Rate");
    }

    public QtDirectory()
    {
        this.setDescriptor(new QtDescriptor(this));
    }

    @Override
    @NotNull
    public String getName() { return "Quicktime"; }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
