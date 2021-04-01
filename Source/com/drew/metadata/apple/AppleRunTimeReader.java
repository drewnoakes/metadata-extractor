package com.drew.metadata.apple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.AppleMakernoteDirectory;
import com.drew.metadata.exif.makernotes.AppleRunTimeMakernoteDirectory;
import com.drew.metadata.plist.BplistReader;

/**
 * Reads the AppleRunTime data and adds <tt>AppleRunTimeMakernoteDirectory</tt> to the 
 * parent <tt>AppleMakernoteDirectory</tt> if it can be parsed with no errors.
 * 
 */
public class AppleRunTimeReader 
{

    public void extract(@NotNull byte[] bytes, @NotNull final Metadata metadata, @NotNull final Directory parentDirectory)
    {
        parentDirectory.setByteArray(AppleMakernoteDirectory.TAG_RUN_TIME, bytes);
        if(!BplistReader.isValid(bytes))
        {
        	parentDirectory.addError("Input array is not a bplist");
        	return;
        }

        AppleRunTimeMakernoteDirectory directory = new AppleRunTimeMakernoteDirectory();
        directory.setParent(parentDirectory);

        try {
            processAppleRunTime(directory, bytes);

            if (directory.getTagCount() > 0) 
            {
                metadata.addDirectory(directory);
            }
        } 
        catch (IOException e) 
        {
            parentDirectory.addError("Error processing TAG_RUN_TIME: " + e.getMessage());
        }
    }

    /**
     * Process the BPLIST containing the RUN_TIME tag. The directory will only be populated with values
     * if the <tt>flag</tt> indicates that the CMTime structure is &quot;valid&quot;.
     *
     * @param directory The <tt>AppleRunTimeMakernoteDirectory</tt> to set values onto.
     * @param bplist The BPLIST
     * @throws IOException Thrown if an error occurs parsing the BPLIST as a CMTime structure.
     */
    private static void processAppleRunTime(@NotNull final AppleRunTimeMakernoteDirectory directory, @NotNull final byte[] bplist) throws IOException
    {
        final BplistReader.PropertyListResults results = BplistReader.parse(bplist);

        final Set<Map.Entry<Byte, Byte>> entrySet = results.getEntrySet();

        if (entrySet != null) {
            HashMap<String, Object> values = new HashMap<String, Object>(entrySet.size());

            for (Map.Entry<Byte, Byte> entry : entrySet) {
                String key = (String)results.getObjects().get(entry.getKey());
                Object value = results.getObjects().get(entry.getValue());

                values.put(key, value);
            }

            // https://developer.apple.com/documentation/coremedia/cmtime-u58

            byte flags = (Byte)values.get("flags");

            if ((flags & 0x1) == 0x1) {
                directory.setInt(AppleRunTimeMakernoteDirectory.CMTimeFlags, flags);
                directory.setInt(AppleRunTimeMakernoteDirectory.CMTimeEpoch, (Byte)values.get("epoch"));
                directory.setLong(AppleRunTimeMakernoteDirectory.CMTimeScale, (Long)values.get("timescale"));
                directory.setLong(AppleRunTimeMakernoteDirectory.CMTimeValue, (Long)values.get("value"));
            }
        }
    }
}
