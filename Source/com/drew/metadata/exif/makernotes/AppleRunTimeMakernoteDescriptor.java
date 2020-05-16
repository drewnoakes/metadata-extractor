package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.Nullable;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * 
 * @author Bob Johnson
 */
public class AppleRunTimeMakernoteDescriptor extends TagDescriptor<AppleRunTimeMakernoteDirectory>
{

    public AppleRunTimeMakernoteDescriptor(AppleRunTimeMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType)
        {
        case AppleRunTimeMakernoteDirectory.CMTimeFlags:
            return flagsDescription();
        case AppleRunTimeMakernoteDirectory.CMTimeValue:
            return calculateTimeInSeconds();
        default:
            return super.getDescription(tagType);
        }
    }
    
    // flags bitmask details
    // 0000 0001 = Valid
    // 0000 0010 = Rounded
    // 0000 0100 = Positive Infinity
    // 0000 1000 = Negative Infinity
    // 0001 0000 = Indefinite
    private String flagsDescription()
    {
        try {
            final int value = _directory.getInt(AppleRunTimeMakernoteDirectory.CMTimeFlags);
            
            StringBuilder sb = new StringBuilder();

            if ((value & 0x1) == 1)
                sb.append("Valid");
            else
                sb.append("Invalid");
            
            if((value & 0x2) != 0)
                sb.append(", rounded");

            if((value & 0x4) != 0)
                sb.append(", positive infinity");
            
            if((value & 0x8) != 0)
                sb.append(", negative infinity");
            
            if((value & 0x10) != 0)
                sb.append(", indefinite");
            
            return sb.toString();
        }
        catch (MetadataException ignored)
        {
            return null;
        }
    }

    private String calculateTimeInSeconds()
    {
        try
        {
            long value = _directory.getLong(AppleRunTimeMakernoteDirectory.CMTimeValue);
            long scale = _directory.getLong(AppleRunTimeMakernoteDirectory.CMTimeScale);

            return String.format("%d seconds", (value / scale));
        }
        catch (MetadataException ignored)
        {
            return null;

        }
    }
}
