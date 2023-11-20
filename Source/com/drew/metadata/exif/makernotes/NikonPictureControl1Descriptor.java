package com.drew.metadata.exif.makernotes;

import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_FILTER_EFFECT;
import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_PICTURE_CONTROL_ADJUST;
import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_TONING_EFFECT;

public final class NikonPictureControl1Descriptor extends TagDescriptor<NikonPictureControl1Directory>
{
    public NikonPictureControl1Descriptor(NikonPictureControl1Directory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_PICTURE_CONTROL_ADJUST:
                return getPictureControlAdjustDescription();
            case TAG_FILTER_EFFECT:
                return getFilterEffectDescription();
            case TAG_TONING_EFFECT:
                return getToningEffectDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    public String getPictureControlAdjustDescription()
    {
        return getIndexedDescription(
            TAG_PICTURE_CONTROL_ADJUST,
            "Default Settings",
            "Quick Adjust",
            "Full Control"
        );
    }

    public String getFilterEffectDescription()
    {
        Integer value = _directory.getInteger(TAG_FILTER_EFFECT);
        if (value == null) {
            return null;
        }

        switch (value) {
            case 0x80:
                return "Off";
            case 0x81:
                return "Yellow";
            case 0x82:
                return "Orange";
            case 0x83:
                return "Red";
            case 0x84:
                return "Green";
            case 0xFF:
                return "N/A";
            default:
                return super.getDescription(TAG_FILTER_EFFECT);
        }
    }

    public String getToningEffectDescription()
    {
        Integer value = _directory.getInteger(TAG_TONING_EFFECT);
        if (value == null) {
            return null;
        }

        switch (value) {
            case 0x80:
                return "B&W";
            case 0x81:
                return "Sepia";
            case 0x82:
                return "Cyanotype";
            case 0x83:
                return "Red";
            case 0x84:
                return "Yellow";
            case 0x85:
                return "Green";
            case 0x86:
                return "Blue-green";
            case 0x87:
                return "Blue";
            case 0x88:
                return "Purple-blue";
            case 0x89:
                return "Red-purple";
            case 0xFF:
                return "N/A";
            default:
                return super.getDescription(TAG_TONING_EFFECT);
        }
    }
}
