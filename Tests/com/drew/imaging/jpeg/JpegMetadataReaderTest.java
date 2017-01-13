/*
 * Copyright 2002-2016 Drew Noakes
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.filter.MetadataFilter;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegMetadataReaderTest
{
    @Test
    public void testExtractMetadata() throws Exception
    {
        validate(JpegMetadataReader.readMetadata(new File("Tests/Data/withExif.jpg")));
        validateFiltered(JpegMetadataReader.readMetadata(new File("Tests/Data/withExif.jpg"), new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return directory instanceof JfifDirectory && tagType != JfifDirectory.TAG_UNITS;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return directory == JfifDirectory.class || directory == ExifIFD0Directory.class;
            }
        }));
    }

    @Test
    public void testExtractMetadataUsingInputStream() throws Exception
    {
        validate(JpegMetadataReader.readMetadata(new FileInputStream((new File("Tests/Data/withExif.jpg")))));
        validateFiltered(JpegMetadataReader.readMetadata(new FileInputStream((new File("Tests/Data/withExif.jpg"))), new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return directory instanceof JfifDirectory && tagType != JfifDirectory.TAG_UNITS;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return directory == JfifDirectory.class || directory == ExifIFD0Directory.class;
            }
        }));
    }

    @Test
    public void testExtractXmpMetadata() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Tests/Data/withXmp.jpg"));
        Directory directory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        assertNotNull(directory);
    }

    private void validate(Metadata metadata)
    {
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(directory);
        assertEquals("80", directory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
    }

    private void validateFiltered(Metadata metadata) throws MetadataException
    {
        assertTrue(metadata.getDirectoriesOfType(ExifSubIFDDirectory.class).isEmpty());
        Directory directory = metadata.getFirstDirectoryOfType(JfifDirectory.class);
        assertNotNull(directory);
        assertEquals(0x0102, ((JfifDirectory) directory).getVersion());
        assertFalse(directory.containsTag(JfifDirectory.TAG_UNITS));
        directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(directory);
        assertEquals(0, directory.getTagCount());
    }
}
