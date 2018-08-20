package com.drew.testing.canon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.avi.AviDirectory;

/**
 * Unit tests for {@link AviDirectory}.
 * 
 * @author PEBAL
 */
public class AviDateTimeTest {
	private static String aviFilePath = "Tests/Data/CanonDigitalIXUS970IS_IDIT_DateTimeOriginal.avi";

	@Test
	public void testExtractMetadata() throws Exception {
		Metadata metadata = ImageMetadataReader.readMetadata(new File(aviFilePath));
		AviDirectory directory = metadata.getFirstDirectoryOfType(AviDirectory.class);
		assertNotNull(directory);
		assertFalse(directory.hasErrors());
		assertEquals("AVI", directory.getName());
		assertNotNull(directory.getString(AviDirectory.TAG_HEIGHT));
		assertNotNull(directory.getString(AviDirectory.TAG_WIDTH));
	}

	// @Ignore
	@Test
	public void testDateTime() throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(new File(aviFilePath));
		AviDirectory directory = metadata.getFirstDirectoryOfType(AviDirectory.class);
//		for (Tag tag : directory.getTags()) {
//			System.out.println(tag.toString());
//		}
		assertNotNull(directory.getString(AviDirectory.TAG_DATETIME_ORIGINAL));
		assertEquals("Mon Jul 25 13:58:34 2016", directory.getString(AviDirectory.TAG_DATETIME_ORIGINAL));
	}

}
