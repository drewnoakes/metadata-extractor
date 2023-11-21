package com.drew.metadata.exif.makernotes;

import com.drew.lang.Charsets;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.io.IOException;
import java.util.HashMap;

public final class NikonPictureControl1Directory extends Directory
{
    // Tag values are offsets into the underlying data.
    // Data from https://exiftool.org/TagNames/Nikon.html#PictureControl

    public static final int TAG_PICTURE_CONTROL_VERSION = 0;
    public static final int TAG_PICTURE_CONTROL_NAME = 4;
    public static final int TAG_PICTURE_CONTROL_BASE = 24;
    // skip 4
    public static final int TAG_PICTURE_CONTROL_ADJUST = 48;
    public static final int TAG_PICTURE_CONTROL_QUICK_ADJUST = 49;
    public static final int TAG_SHARPNESS = 50;
    public static final int TAG_CONTRAST = 51;
    public static final int TAG_BRIGHTNESS = 52;
    public static final int TAG_SATURATION = 53;
    public static final int TAG_HUE_ADJUSTMENT = 54;
    public static final int TAG_FILTER_EFFECT = 55;
    public static final int TAG_TONING_EFFECT = 56;
    public static final int TAG_TONING_SATURATION = 57;

    private static final HashMap<Integer, String> TAG_NAME_MAP = new HashMap<>();

    static
    {
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_VERSION, "Picture Control Version");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_NAME, "Picture Control Name");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_BASE, "Picture Control Base");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_ADJUST, "Picture Control Adjust");
        TAG_NAME_MAP.put(TAG_PICTURE_CONTROL_QUICK_ADJUST, "Picture Control Quick Adjust");
        TAG_NAME_MAP.put(TAG_SHARPNESS, "Sharpness");
        TAG_NAME_MAP.put(TAG_CONTRAST, "Contrast");
        TAG_NAME_MAP.put(TAG_BRIGHTNESS, "Brightness");
        TAG_NAME_MAP.put(TAG_SATURATION, "Saturation");
        TAG_NAME_MAP.put(TAG_HUE_ADJUSTMENT, "Hue Adjustment");
        TAG_NAME_MAP.put(TAG_FILTER_EFFECT, "Filter Effect");
        TAG_NAME_MAP.put(TAG_TONING_EFFECT, "Toning Effect");
        TAG_NAME_MAP.put(TAG_TONING_SATURATION, "Toning Saturation");
    }

    public NikonPictureControl1Directory()
    {
        setDescriptor(new NikonPictureControl1Descriptor(this));
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Nikon PictureControl 1";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return TAG_NAME_MAP;
    }

    public static NikonPictureControl1Directory read(byte[] bytes) throws IOException
    {
        final int EXPECTED_LENGTH = 58;

        if (bytes.length != EXPECTED_LENGTH) {
            throw new IllegalArgumentException("Must have " + EXPECTED_LENGTH + " bytes.");
        }

        SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);

        NikonPictureControl1Directory directory = new NikonPictureControl1Directory();

        directory.setString(TAG_PICTURE_CONTROL_VERSION, reader.getNullTerminatedStringAndSkipToNextPosition(4, Charsets.UTF_8).toString());
        directory.setString(TAG_PICTURE_CONTROL_NAME, reader.getNullTerminatedStringAndSkipToNextPosition(20, Charsets.UTF_8).toString());
        directory.setString(TAG_PICTURE_CONTROL_BASE, reader.getNullTerminatedStringAndSkipToNextPosition(20, Charsets.UTF_8).toString());
        reader.skip(4);
        directory.setObject(TAG_PICTURE_CONTROL_ADJUST, reader.getUInt8());
        directory.setObject(TAG_PICTURE_CONTROL_QUICK_ADJUST, reader.getUInt8());
        directory.setObject(TAG_SHARPNESS, reader.getUInt8());
        directory.setObject(TAG_CONTRAST, reader.getUInt8());
        directory.setObject(TAG_BRIGHTNESS, reader.getUInt8());
        directory.setObject(TAG_SATURATION, reader.getUInt8());
        directory.setObject(TAG_HUE_ADJUSTMENT, reader.getUInt8());
        directory.setObject(TAG_FILTER_EFFECT, reader.getUInt8());
        directory.setObject(TAG_TONING_EFFECT, reader.getUInt8());
        directory.setObject(TAG_TONING_SATURATION, reader.getUInt8());

        assert (reader.getPosition() == EXPECTED_LENGTH);

        return directory;
    }
}
