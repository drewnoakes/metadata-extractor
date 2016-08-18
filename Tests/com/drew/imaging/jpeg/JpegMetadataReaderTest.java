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
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    }

        @Test
    public void testExtractPanasonicMakernoteMetadata() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Tests/Data/P1050007.jpg"));
        // obtain the Exif directory
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(directory);
        // query the tag's value
        PanasonicMakernoteDirectory pmd = metadata.getFirstDirectoryOfType(PanasonicMakernoteDirectory.class);
        if (pmd != null) {
            String country = "";
            String city = "";
            String state = "";
            String landmark = "";
            for (Tag tag : pmd.getTags()) {
                if (tag.getTagType() == PanasonicMakernoteDirectory.TAG_COUNTRY) {
                    country = tag.getDescription();
                    assertEquals("DENMARK", country);
                } else if (tag.getTagType() == PanasonicMakernoteDirectory.TAG_STATE) {
                    
                    state = tag.getDescription();
                    assertEquals("SJÆLLAND", state);
                } else if (tag.getTagType() == PanasonicMakernoteDirectory.TAG_CITY) {
                    
                    city = tag.getDescription();
                    assertEquals("ODSHERRED", city);
                } else if (tag.getTagType() == PanasonicMakernoteDirectory.TAG_LANDMARK) {

                    landmark = tag.getDescription();
                    if (!landmark.equals("---")) {
                        System.out.println(landmark);
                    }
                }
            }

        }
        
    }

    private void validate(Metadata metadata)
    {
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(directory);
        assertEquals("80", directory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
    }
}
