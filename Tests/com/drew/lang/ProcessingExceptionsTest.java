package com.drew.lang;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;

/**
 * Created because of Issue 91: Use specialized exception for unsupported file formats
 *
 * @author Ricardo
 */
public class ProcessingExceptionsTest {

	@Test
	public void testImageProcessionExceptionNoMagicNumber() throws Exception {
		try {
			ImageMetadataReader.readMetadata(new File("Tests/Data/empty.txt"));
		} catch (final ImageProcessingException e) {
			assertEquals(e.getMessage(), ImageProcessingException.messageMagicNumberNotFound);
		}
	}

	@Test
	public void testImageProcessionExceptionFileFormatNotSupported() throws Exception {
		try {
			ImageMetadataReader.readMetadata(new File("Tests/Data/wrong_format.txt"));
		} catch (final ImageProcessingException e) {
			assertEquals(e.getMessage(), ImageProcessingException.messageFileFormatNotSupported);
		}
	}
}
