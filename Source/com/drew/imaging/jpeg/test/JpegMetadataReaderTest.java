/*
 * Copyright 2002-2011 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegMetadataReaderTest
{
    @Test
    public void testExtractMetadata() throws Exception
    {
        File withExif = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(withExif);
        Assert.assertTrue(metadata.containsDirectory(ExifDirectory.class));
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals("80", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
    }

    @Test
    public void testExtractMetadataUsingInputStream() throws Exception
    {
        File withExif = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        InputStream in = new BufferedInputStream(new FileInputStream((withExif)));
        Metadata metadata = JpegMetadataReader.readMetadata(in);
        Assert.assertTrue(metadata.containsDirectory(ExifDirectory.class));
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals("80", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
    }
}
