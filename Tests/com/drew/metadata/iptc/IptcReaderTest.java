/*
 * Copyright 2002-2012 Drew Noakes
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
package com.drew.metadata.iptc;

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.TestHelper;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class IptcReaderTest
{
    @Test public void testIptc1BytesFromFile() throws Exception
    {
        final byte[] bytes = TestHelper.loadFileBytes(new File("Tests/com/drew/metadata/iptc/iptc1.bytes"));

        Metadata metadata = new Metadata();
        IptcReader reader = new IptcReader();
        reader.extract(bytes, metadata, JpegSegmentType.APPD);

        Assert.assertEquals(1, metadata.getDirectoryCount());
        IptcDirectory directory = metadata.getDirectory(IptcDirectory.class);
        Assert.assertNotNull(directory);
        Assert.assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        Assert.assertEquals(16, tags.length);

        Assert.assertEquals(IptcDirectory.TAG_CATEGORY, tags[0].getTagType());
        Assert.assertArrayEquals(new String[] { "Supl. Category2", "Supl. Category1", "Cat" }, directory.getStringArray(tags[0].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_COPYRIGHT_NOTICE, tags[1].getTagType());
        Assert.assertEquals("Copyright", directory.getObject(tags[1].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_SPECIAL_INSTRUCTIONS, tags[2].getTagType());
        Assert.assertEquals("Special Instr.", directory.getObject(tags[2].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_HEADLINE, tags[3].getTagType());
        Assert.assertEquals("Headline", directory.getObject(tags[3].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CAPTION_WRITER, tags[4].getTagType());
        Assert.assertEquals("CaptionWriter", directory.getObject(tags[4].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CAPTION, tags[5].getTagType());
        Assert.assertEquals("Caption", directory.getObject(tags[5].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_ORIGINAL_TRANSMISSION_REFERENCE, tags[6].getTagType());
        Assert.assertEquals("Transmission", directory.getObject(tags[6].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME, tags[7].getTagType());
        Assert.assertEquals("Country", directory.getObject(tags[7].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_PROVINCE_OR_STATE, tags[8].getTagType());
        Assert.assertEquals("State", directory.getObject(tags[8].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CITY, tags[9].getTagType());
        Assert.assertEquals("City", directory.getObject(tags[9].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_DATE_CREATED, tags[10].getTagType());
        Assert.assertEquals(new java.util.GregorianCalendar(2000, 0, 1).getTime(), directory.getObject(tags[10].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_OBJECT_NAME, tags[11].getTagType());
        Assert.assertEquals("ObjectName", directory.getObject(tags[11].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_SOURCE, tags[12].getTagType());
        Assert.assertEquals("Source", directory.getObject(tags[12].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CREDIT, tags[13].getTagType());
        Assert.assertEquals("Credits", directory.getObject(tags[13].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_BY_LINE_TITLE, tags[14].getTagType());
        Assert.assertEquals("BylineTitle", directory.getObject(tags[14].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_BY_LINE, tags[15].getTagType());
        Assert.assertEquals("Byline", directory.getObject(tags[15].getTagType()));
    }

    @Test public void testIptc2Photoshop6BytesFromFile() throws Exception
    {
        final byte[] bytes = TestHelper.loadFileBytes(new File("Tests/com/drew/metadata/iptc/iptc2-photoshop6.bytes"));

        Metadata metadata = new Metadata();
        new IptcReader().extract(new SequentialByteArrayReader(bytes), metadata, bytes.length);

        Assert.assertEquals(1, metadata.getDirectoryCount());
        IptcDirectory directory = metadata.getDirectory(IptcDirectory.class);
        Assert.assertNotNull(directory);
        Assert.assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        Assert.assertEquals(17, tags.length);

        Assert.assertEquals(IptcDirectory.TAG_APPLICATION_RECORD_VERSION, tags[0].getTagType());
        Assert.assertEquals(2, directory.getObject(tags[0].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CAPTION, tags[1].getTagType());
        Assert.assertEquals("Caption PS6", directory.getObject(tags[1].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CAPTION_WRITER, tags[2].getTagType());
        Assert.assertEquals("CaptionWriter", directory.getObject(tags[2].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_HEADLINE, tags[3].getTagType());
        Assert.assertEquals("Headline", directory.getObject(tags[3].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_SPECIAL_INSTRUCTIONS, tags[4].getTagType());
        Assert.assertEquals("Special Instr.", directory.getObject(tags[4].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_BY_LINE, tags[5].getTagType());
        Assert.assertEquals("Byline", directory.getObject(tags[5].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_BY_LINE_TITLE, tags[6].getTagType());
        Assert.assertEquals("BylineTitle", directory.getObject(tags[6].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CREDIT, tags[7].getTagType());
        Assert.assertEquals("Credits", directory.getObject(tags[7].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_SOURCE, tags[8].getTagType());
        Assert.assertEquals("Source", directory.getObject(tags[8].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_OBJECT_NAME, tags[9].getTagType());
        Assert.assertEquals("ObjectName", directory.getObject(tags[9].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CITY, tags[10].getTagType());
        Assert.assertEquals("City", directory.getObject(tags[10].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_PROVINCE_OR_STATE, tags[11].getTagType());
        Assert.assertEquals("State", directory.getObject(tags[11].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME, tags[12].getTagType());
        Assert.assertEquals("Country", directory.getObject(tags[12].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_ORIGINAL_TRANSMISSION_REFERENCE, tags[13].getTagType());
        Assert.assertEquals("Transmission", directory.getObject(tags[13].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_CATEGORY, tags[14].getTagType());
        Assert.assertEquals("Cat", directory.getObject(tags[14].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_SUPPLEMENTAL_CATEGORIES, tags[15].getTagType());
        Assert.assertArrayEquals(new String[] { "Supl. Category1", "Supl. Category2" }, directory.getStringArray(tags[15].getTagType()));

        Assert.assertEquals(IptcDirectory.TAG_COPYRIGHT_NOTICE, tags[16].getTagType());
        Assert.assertEquals("Copyright", directory.getObject(tags[16].getTagType()));
    }
}
