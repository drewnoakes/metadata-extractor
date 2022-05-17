package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class SonyTag9050bDescriptor extends TagDescriptor<SonyTag9050bDirectory>
{
    public SonyTag9050bDescriptor(@NotNull SonyTag9050bDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case SonyTag9050bDirectory.TAG_FLASH_STATUS:
                return getFlashStatusDescription();
            case SonyTag9050bDirectory.TAG_SONY_EXPOSURE_TIME:
                return getSonyExposureTimeDescription();
            case SonyTag9050bDirectory.TAG_INTERNAL_SERIAL_NUMBER:
                return getInternalSerialNumberDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getInternalSerialNumberDescription()
    {
        int[] values = _directory.getIntArray(SonyTag9050bDirectory.TAG_INTERNAL_SERIAL_NUMBER);
        if (values == null)
            return null;
        StringBuilder sn = new StringBuilder();
        for (int value : values) {
            sn.append(String.format("%02x", value));
        }
        return sn.toString();
    }

    @Nullable
    public String getSonyExposureTimeDescription()
    {
        Float value = _directory.getFloatObject(SonyTag9050bDirectory.TAG_SONY_EXPOSURE_TIME);
        if (value == null)
            return null;
        if (value == 0)
            return "0";
        return String.format("1/%s", (int)(0.5 + (1 / value)));
    }

    @Nullable
    public String getFlashStatusDescription()
    {

        Integer value = _directory.getInteger(SonyTag9050bDirectory.TAG_FLASH_STATUS);
        if (value == null)
            return null;
        switch (value) {
            case 0x00:
                return "No flash present";
            case 0x02:
                return "Flash inhibited";
            case 0x40:
                return "Built-in flash present";
            case 0x41:
                return "Built-in flash fired";
            case 0x42:
                return "Built-in flash inhibited";
            case 0x80:
                return "External flash present";
            case 0x81:
                return "External flash fired";
            default:
                return "Unknown (" + value + ")";
        }
    }
}
