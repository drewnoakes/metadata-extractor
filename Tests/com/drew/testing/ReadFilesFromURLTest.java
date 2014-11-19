package com.drew.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

public class ReadFilesFromURLTest {

	/**
	 * Just reads the metadata from a web image and checks if it contains any errors. See also
	 * {@link #ReadFilesFromURLTest.testReadImagesFromWebsitesWithCorrectMetaData()} for a test which
	 * compares the meta data with the original image.
	 *
	 * @throws Exception
	 */
	@Test
	public void testReadMetaDataFromWebImages() throws Exception {
		final String[] urls = {
				"http://upload.wikimedia.org/wikipedia/commons/2/24/Prismas_Bas%C3%A1lticos%2C_Huasca_de_Ocampo%2C_Hidalgo%2C_M%C3%A9xico%2C_2013-10-10%2C_DD_42.JPG",
				"http://sample-images.metadata-extractor.googlecode.com/git/Sony%20Cybershot%20(2).jpg",
				"https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Commons-emblem-notice.svg/1000px-Commons-emblem-notice.svg.png",
				"https://upload.wikimedia.org/wikipedia/commons/3/36/Sunflower_as_GIF.gif",
		"https://upload.wikimedia.org/wikipedia/commons/7/7f/PhilcUK-1274438506.jpg"};

		for (final String string : urls) {
			final Metadata metadata = ImageMetadataReader.readMetadata(new URL(string));
			assertNotNull(metadata);
			assertFalse(metadata.hasErrors());
		}
	}

	@Test(expected = ImageProcessingException.class)
	public void testBigInvalidFile() throws Exception {
		final URL url = new URL("http://ftp-archive.freebsd.org/pub/FreeBSD-Archive/old-releases/amd64/10.0-RELEASE/src.txz");
		ImageMetadataReader.readMetadata(url);
	}

	@Test
	public void testReadImagesFromWebsitesWithCorrectMetaData() throws Exception {
		// TODO use some sample images and check if the extracted metadata is correct
	}

}
