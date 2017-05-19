/*
 * Copyright 2002-2017 Drew Noakes
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
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.HuffmanTablesDirectory;
import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegMetadataReaderTest
{
    @Test
    public void testExtractMetadata() throws Exception
    {
        validate(JpegMetadataReader.readMetadata(new File("Tests/Data/withExif.jpg")));
    }

    @Test
    public void testExtractMetadataUsingInputStream() throws Exception
    {
        validate(JpegMetadataReader.readMetadata(new FileInputStream((new File("Tests/Data/withExif.jpg")))));
    }

    @Test
    public void testExtractXmpMetadata() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Tests/Data/withXmp.jpg"));
        Directory directory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        assertNotNull(directory);
        directory = metadata.getFirstDirectoryOfType(HuffmanTablesDirectory.class);
        assertNotNull(directory);
        assertTrue(((HuffmanTablesDirectory) directory).isOptimized());
    }

    @Test
    public void testTypicalHuffman() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Tests/Data/withTypicalHuffman.jpg"));
        Directory directory = metadata.getFirstDirectoryOfType(HuffmanTablesDirectory.class);
        assertNotNull(directory);
        assertTrue(((HuffmanTablesDirectory) directory).isTypical());
        assertFalse(((HuffmanTablesDirectory) directory).isOptimized());
        for (int i = 0; i < ((HuffmanTablesDirectory) directory).getNumberOfTables(); i++) {
            HuffmanTable table = ((HuffmanTablesDirectory) directory).getTable(i);
            assertTrue(table.isTypical());
            assertFalse(table.isOptimized());
        }
    }

    @Test
    public void testFStopFormatting() throws Exception
    {
        Locale previousLocale = Locale.getDefault();

        // Setting this locale would cause an f-number with a comma:
        Locale.setDefault(new Locale("nl", "NL"));

        try {
            Metadata metadata = JpegMetadataReader.readMetadata(new File("Tests/Data/withExif.jpg"));
            final ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            assertEquals("f/4.0", subIFDDirectory.getDescription(ExifDirectoryBase.TAG_FNUMBER));
        } finally {
            // Reset the locale:
            Locale.setDefault(previousLocale);
        }
    }

    private void validate(Metadata metadata)
    {
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(directory);
        assertEquals("80", directory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        directory = metadata.getFirstDirectoryOfType(HuffmanTablesDirectory.class);
        assertNotNull(directory);
        assertTrue(((HuffmanTablesDirectory) directory).isOptimized());
    }
}
