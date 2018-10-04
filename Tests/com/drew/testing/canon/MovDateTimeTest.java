package com.drew.testing.canon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.avi.AviDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;

/**
 * Unit tests for {@link AviDirectory}.
 * 
 * @author PerB
 */
public class MovDateTimeTest
{
    private static final String movFilePath = "Tests/Data/CanonPowerShotSX30IS_CNTH_DateTime.mov";

    @Test
    public void testExtractMetadata() throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(new File(movFilePath));
        QuickTimeDirectory quickTimedir = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
        assertNotNull(quickTimedir);
        if (quickTimedir.hasErrors()) {
            String errs = "";
            for (String err : quickTimedir.getErrors()) {
                errs += err + ", ";
            }
            assertFalse(errs, quickTimedir.hasErrors());
        }
        assertEquals("QuickTime", quickTimedir.getName());
        assertNotNull(quickTimedir.getString(QuickTimeDirectory.TAG_MAJOR_BRAND));
        assertNotNull(quickTimedir.getString(QuickTimeDirectory.TAG_MINOR_VERSION));
    }

    @Test
    public void testDateTime() throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(new File(movFilePath));
        QuickTimeDirectory quickTimedir = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
        assertNotNull(quickTimedir.getString(QuickTimeDirectory.TAG_CANON_THUMBNAIL_DT));
        assertEquals("2015:07:27 16:22:41", quickTimedir.getString(QuickTimeDirectory.TAG_CANON_THUMBNAIL_DT));
    }

}
