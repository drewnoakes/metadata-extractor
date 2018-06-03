package com.drew.metadata.exif;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.avi.AviDirectory;

/**
    Unit tests for {@link AviDirectory}.
    @author PEBAL
    */

    @SuppressWarnings("ConstantConditions")
    public class AviDateTest {
        @test
        public void testGetDirectoryName() throws Exception {
            Directory aviDirectory = new AviDirectory();

        assertFalse(aviDirectory.hasErrors());

        assertEquals("AVI", aviDirectory.getName());
    }

    @test
    public void testDateTime() throws IOException, ImageProcessingException {
        File file = new File("Tests/Data/CanonCanonPowerShotSX30IS_DateTimeOriginal.avi");
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        AviDirectory aviDirectory = metadata.getFirstDirectoryOfType(AviDirectory.class);
        assertEquals("Mon Jul 25 13:15:45 2016", aviDirectory.getString(AviDirectory.TAG_DATETIME_ORIGINAL));
    }

}
