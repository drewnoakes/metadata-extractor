package com.drew.metadata.exif.makernotes;

import com.drew.lang.Charsets;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.io.IOException;
import java.util.HashMap;

public final class NikonPictureControl2Directory extends Directory
{
    // Tag values are offsets into the underlying data.
    // Data from https://exiftool.org/TagNames/Nikon.html#PictureControl

    public static final int TAG_PICTURE_CONTROL_VERSION = 0;
    public static final int TAG_PICTURE_CONTROL_NAME = 4;
    public static final int TAG_PICTURE_CONTROL_BASE = 24;
    public static final int TAG_PICTURE_CONTROL_ADJUST = 48;
    public static final int TAG_PICTURE_CONTROL_QUICK_ADJUST = 49;
    // skip 1
    public static final int TAG_SHARPNESS = 51;
    // skip 1
    public static final int TAG_CLARITY = 53;
    // skip 1
    public static final int TAG_CONTRAST = 55;
    // skip 1
    public static final int TAG_BRIGHTNESS = 57;
    // skip 1
    public static final int TAG_SATURATION = 59;
    // skip 1
    public static final int TAG_HUE = 61;
    // skip 1
    public static final int TAG_FILTER_EFFECT = 63;
    public static final int TAG_TONING_EFFECT = 64;
    public static final int TAG_TONING_SATURATION = 65;

    private static final HashMap<Integer, String> TAG_NAME_MAP = new HashMap<>();

    static
    {
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_VERSION, "Picture Control Version");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_NAME, "Picture Control Name");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_BASE, "Picture Control Base");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_ADJUST, "Picture Control Adjust");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_QUICK_ADJUST, "Picture Control Quick Adjust");
        TAG_NAME_MAP.put(TAG_SHARPNESS, "Sharpness");
        TAG_NAME_MAP.put(TAG_CLARITY, "Clarity");
        TAG_NAME_MAP.put(TAG_CONTRAST, "Contrast");
        TAG_NAME_MAP.put(TAG_BRIGHTNESS, "Brightness");
        TAG_NAME_MAP.put(TAG_SATURATION, "Saturation");
        TAG_NAME_MAP.put(TAG_HUE, "Hue");
        TAG_NAME_MAP.put(TAG_FILTER_EFFECT, "Filter Effect");
        TAG_NAME_MAP.put(TAG_TONING_EFFECT, "Toning Effect");
        TAG_NAME_MAP.put(TAG_TONING_SATURATION, "Toning Saturation");
    }

    public NikonPictureControl2Directory()
    {
        setDescriptor(new NikonPictureControl2Descriptor(this));
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Nikon PictureControl 2";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return TAG_NAME_MAP;
    }

    public static NikonPictureControl2Directory read(byte[] bytes) throws IOException
    {
        int EXPECTED_LENGTH = 68;

        if (bytes.length != EXPECTED_LENGTH) {
            throw new IllegalArgumentException("Must have " + EXPECTED_LENGTH + " bytes.");
        }

        SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);

        NikonPictureControl2Directory directory = new NikonPictureControl2Directory();

        directory.setString(TAG_PICTURE_CONTROL_VERSION, reader.getStringValue(4, Charsets.UTF_8).toString());
        directory.setString(TAG_PICTURE_CONTROL_NAME, reader.getStringValue(20, Charsets.UTF_8).toString());
        directory.setString(TAG_PICTURE_CONTROL_BASE, reader.getStringValue(20, Charsets.UTF_8).toString());

        reader.skip(4);
        directory.setObject(TAG_PICTURE_CONTROL_ADJUST, reader.getByte());
        directory.setObject(TAG_PICTURE_CONTROL_QUICK_ADJUST, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_SHARPNESS, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_CLARITY, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_CONTRAST, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_BRIGHTNESS, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_SATURATION, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_HUE, reader.getByte());
        reader.skip(1);
        directory.setObject(TAG_FILTER_EFFECT, reader.getByte());
        directory.setObject(TAG_TONING_EFFECT, reader.getByte());
        directory.setObject(TAG_TONING_SATURATION, reader.getByte());
        reader.skip(2);

        assert (reader.getPosition() == EXPECTED_LENGTH);

        return directory;
    }
}
