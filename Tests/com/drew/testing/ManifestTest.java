package com.drew.testing;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Test;

public class ManifestTest {

	@Test
	public void testMetadataExtractorVersion() throws Exception 
	{
		FileInputStream stream = new FileInputStream(new File("META-INF/MANIFEST.MF"));
		try 
		{
			Assert.assertNotNull(stream);
			final Manifest manifest = new Manifest(stream);
			Assert.assertNotNull(manifest);
			assertEquals("metadata-extractor", manifest.getMainAttributes().getValue("Implementation-Title"));
			assertEquals("https://drewnoakes.com/code/exif/", manifest.getMainAttributes().getValue("Implementation-Vendor"));
			assertEquals("2.7.0-SNAPSHOT", manifest.getMainAttributes().getValue("Implementation-Version"));
		} finally 
		{
			stream.close();
		}
	}
}
