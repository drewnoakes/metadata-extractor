package com.drew.metadata.exif.makernotes;

import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_FILTER_EFFECT;
import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_PICTURE_CONTROL_ADJUST;
import static com.drew.metadata.exif.makernotes.NikonPictureControl1Directory.TAG_TONING_EFFECT;

public final class NikonPictureControl1Descriptor extends TagDescriptor<NikonPictureControl1Directory> {
    public NikonPictureControl1Descriptor(NikonPictureControl1Directory directory) {
        super(directory);
    }

    @Override
    public String getDescription(int tagType) {
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

    public String getPictureControlAdjustDescription() {
        return getIndexedDescription(
            TAG_PICTURE_CONTROL_ADJUST,
            "Default Settings",
            "Quick Adjust",
            "Full Control"
        );
    }

    public String getFilterEffectDescription() {
        byte[] value = _directory.getByteArray(TAG_FILTER_EFFECT);
        if (value == null) {
            return null;
        }

        switch (value[0]) {
            case (byte) 0x80:
                return "Off";
            case (byte) 0x81:
                return "Yellow";
            case (byte) 0x82:
                return "Orange";
            case (byte) 0x83:
                return "Red";
            case (byte) 0x84:
                return "Green";
            case (byte) 0xFF:
                return "N/A";
            default:
                return super.getDescription(TAG_FILTER_EFFECT);
        }
    }

    public String getToningEffectDescription() {
        byte[] value = _directory.getByteArray(TAG_TONING_EFFECT);
        if (value == null) {
            return null;
        }

        switch (value[0]) {
            case (byte) 0x80:
                return "B&W";
            case (byte) 0x81:
                return "Sepia";
            case (byte) 0x82:
                return "Cyanotype";
            case (byte) 0x83:
                return "Red";
            case (byte) 0x84:
                return "Yellow";
            case (byte) 0x85:
                return "Green";
            case (byte) 0x86:
                return "Blue-green";
            case (byte) 0x87:
                return "Blue";
            case (byte) 0x88:
                return "Purple-blue";
            case (byte) 0x89:
                return "Red-purple";
            case (byte) 0xFF:
                return "N/A";
            default:
                return super.getDescription(TAG_TONING_EFFECT);
        }
    }
}
