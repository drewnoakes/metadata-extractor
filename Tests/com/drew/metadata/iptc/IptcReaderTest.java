/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata.iptc;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.tools.FileUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link IptcReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class IptcReaderTest
{
    @NotNull
    public static IptcDirectory processBytes(@NotNull String filePath) throws IOException
    {
        Metadata metadata = new Metadata();
        byte[] bytes = FileUtil.readBytes(filePath);
        new IptcReader().extract(new SequentialByteArrayReader(bytes), metadata, bytes.length);
        IptcDirectory directory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testIptc1BytesFromFile() throws Exception
    {
        IptcDirectory directory = processBytes("Tests/Data/iptc1.jpg.appd");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(16, tags.length);

        assertEquals(IptcDirectory.TAG_CATEGORY, tags[0].getTagType());
        assertArrayEquals(new String[] { "Supl. Category2", "Supl. Category1", "Cat" }, directory.getStringArray(tags[0].getTagType()));

        assertEquals(IptcDirectory.TAG_COPYRIGHT_NOTICE, tags[1].getTagType());
        assertEquals("Copyright", directory.getObject(tags[1].getTagType()));

        assertEquals(IptcDirectory.TAG_SPECIAL_INSTRUCTIONS, tags[2].getTagType());
        assertEquals("Special Instr.", directory.getObject(tags[2].getTagType()));

        assertEquals(IptcDirectory.TAG_HEADLINE, tags[3].getTagType());
        assertEquals("Headline", directory.getObject(tags[3].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION_WRITER, tags[4].getTagType());
        assertEquals("CaptionWriter", directory.getObject(tags[4].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION, tags[5].getTagType());
        assertEquals("Caption", directory.getObject(tags[5].getTagType()));

        assertEquals(IptcDirectory.TAG_ORIGINAL_TRANSMISSION_REFERENCE, tags[6].getTagType());
        assertEquals("Transmission", directory.getObject(tags[6].getTagType()));

        assertEquals(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME, tags[7].getTagType());
        assertEquals("Country", directory.getObject(tags[7].getTagType()));

        assertEquals(IptcDirectory.TAG_PROVINCE_OR_STATE, tags[8].getTagType());
        assertEquals("State", directory.getObject(tags[8].getTagType()));

        assertEquals(IptcDirectory.TAG_CITY, tags[9].getTagType());
        assertEquals("City", directory.getObject(tags[9].getTagType()));

        assertEquals(IptcDirectory.TAG_DATE_CREATED, tags[10].getTagType());
        assertEquals(new java.util.GregorianCalendar(2000, 0, 1).getTime(), directory.getObject(tags[10].getTagType()));

        assertEquals(IptcDirectory.TAG_OBJECT_NAME, tags[11].getTagType());
        assertEquals("ObjectName", directory.getObject(tags[11].getTagType()));

        assertEquals(IptcDirectory.TAG_SOURCE, tags[12].getTagType());
        assertEquals("Source", directory.getObject(tags[12].getTagType()));

        assertEquals(IptcDirectory.TAG_CREDIT, tags[13].getTagType());
        assertEquals("Credits", directory.getObject(tags[13].getTagType()));

        assertEquals(IptcDirectory.TAG_BY_LINE_TITLE, tags[14].getTagType());
        assertEquals("BylineTitle", directory.getObject(tags[14].getTagType()));

        assertEquals(IptcDirectory.TAG_BY_LINE, tags[15].getTagType());
        assertEquals("Byline", directory.getObject(tags[15].getTagType()));
    }

    @Test
    public void testIptc2Photoshop6BytesFromFile() throws Exception
    {
        IptcDirectory directory = processBytes("Tests/Data/iptc2-photoshop6.jpg.appd");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(17, tags.length);

        assertEquals(IptcDirectory.TAG_APPLICATION_RECORD_VERSION, tags[0].getTagType());
        assertEquals(2, directory.getObject(tags[0].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION, tags[1].getTagType());
        assertEquals("Caption PS6", directory.getObject(tags[1].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION_WRITER, tags[2].getTagType());
        assertEquals("CaptionWriter", directory.getObject(tags[2].getTagType()));

        assertEquals(IptcDirectory.TAG_HEADLINE, tags[3].getTagType());
        assertEquals("Headline", directory.getObject(tags[3].getTagType()));

        assertEquals(IptcDirectory.TAG_SPECIAL_INSTRUCTIONS, tags[4].getTagType());
        assertEquals("Special Instr.", directory.getObject(tags[4].getTagType()));

        assertEquals(IptcDirectory.TAG_BY_LINE, tags[5].getTagType());
        assertEquals("Byline", directory.getObject(tags[5].getTagType()));

        assertEquals(IptcDirectory.TAG_BY_LINE_TITLE, tags[6].getTagType());
        assertEquals("BylineTitle", directory.getObject(tags[6].getTagType()));

        assertEquals(IptcDirectory.TAG_CREDIT, tags[7].getTagType());
        assertEquals("Credits", directory.getObject(tags[7].getTagType()));

        assertEquals(IptcDirectory.TAG_SOURCE, tags[8].getTagType());
        assertEquals("Source", directory.getObject(tags[8].getTagType()));

        assertEquals(IptcDirectory.TAG_OBJECT_NAME, tags[9].getTagType());
        assertEquals("ObjectName", directory.getObject(tags[9].getTagType()));

        assertEquals(IptcDirectory.TAG_CITY, tags[10].getTagType());
        assertEquals("City", directory.getObject(tags[10].getTagType()));

        assertEquals(IptcDirectory.TAG_PROVINCE_OR_STATE, tags[11].getTagType());
        assertEquals("State", directory.getObject(tags[11].getTagType()));

        assertEquals(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME, tags[12].getTagType());
        assertEquals("Country", directory.getObject(tags[12].getTagType()));

        assertEquals(IptcDirectory.TAG_ORIGINAL_TRANSMISSION_REFERENCE, tags[13].getTagType());
        assertEquals("Transmission", directory.getObject(tags[13].getTagType()));

        assertEquals(IptcDirectory.TAG_CATEGORY, tags[14].getTagType());
        assertEquals("Cat", directory.getObject(tags[14].getTagType()));

        assertEquals(IptcDirectory.TAG_SUPPLEMENTAL_CATEGORIES, tags[15].getTagType());
        assertArrayEquals(new String[] { "Supl. Category1", "Supl. Category2" }, directory.getStringArray(tags[15].getTagType()));

        assertEquals(IptcDirectory.TAG_COPYRIGHT_NOTICE, tags[16].getTagType());
        assertEquals("Copyright", directory.getObject(tags[16].getTagType()));
    }

    @Test
    public void testIptcEncodingUtf8() throws Exception
    {
        IptcDirectory directory = processBytes("Tests/Data/iptc-encoding-defined-utf8.bytes");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(4, tags.length);

        assertEquals(IptcDirectory.TAG_ENVELOPE_RECORD_VERSION, tags[0].getTagType());
        assertEquals(2, directory.getObject(tags[0].getTagType()));

        assertEquals(IptcDirectory.TAG_CODED_CHARACTER_SET, tags[1].getTagType());
        assertEquals("UTF-8", directory.getObject(tags[1].getTagType()));

        assertEquals(IptcDirectory.TAG_APPLICATION_RECORD_VERSION, tags[2].getTagType());
        assertEquals(2, directory.getObject(tags[2].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION, tags[3].getTagType());
        assertEquals("In diesem Text sind Umlaute enthalten, nämlich öfter als üblich: ÄÖÜäöüß\r", directory.getObject(tags[3].getTagType()));
    }

    @Test
    public void testIptcEncodingUndefinedIso() throws Exception
    {
        IptcDirectory directory = processBytes("Tests/Data/iptc-encoding-undefined-iso.bytes");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(3, tags.length);

        assertEquals(IptcDirectory.TAG_ENVELOPE_RECORD_VERSION, tags[0].getTagType());
        assertEquals(2, directory.getObject(tags[0].getTagType()));

        assertEquals(IptcDirectory.TAG_APPLICATION_RECORD_VERSION, tags[1].getTagType());
        assertEquals(2, directory.getObject(tags[1].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION, tags[2].getTagType());
        assertEquals("In diesem Text sind Umlaute enthalten, nämlich öfter als üblich: ÄÖÜäöüß\r", directory.getObject(tags[2].getTagType()));
    }

    @Test
    public void testIptcEncodingUnknown() throws Exception
    {
        IptcDirectory directory = processBytes("Tests/Data/iptc-encoding-unknown.bytes");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(3, tags.length);

        assertEquals(IptcDirectory.TAG_APPLICATION_RECORD_VERSION, tags[0].getTagType());
        assertEquals(2, directory.getObject(tags[0].getTagType()));

        assertEquals(IptcDirectory.TAG_CAPTION, tags[1].getTagType());
        assertEquals("Das Encoding dieser Metadaten ist nicht deklariert und lässt sich nur schwer erkennen.", directory.getObject(tags[1].getTagType()));

        assertEquals(IptcDirectory.TAG_KEYWORDS, tags[2].getTagType());
        assertArrayEquals(new String[]{"häufig", "üblich", "Lösung", "Spaß"}, directory.getStringArray(tags[2].getTagType()));
    }

    @Test
    public void testIptcEncodingUnknown2() throws Exception
    {
        // This metadata has an encoding of three characters [ \ESC '%' '5' ]
        // It's not clear what to do with this, so it should be ignored.
        // Version 2.7.0 tripped up on this and threw an exception.
        IptcDirectory directory = processBytes("Tests/Data/iptc-encoding-unknown-2.bytes");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(37, tags.length);

        assertEquals("MEDWAS,MEDLON,MEDTOR,RONL,ASIA,AONL,APC,USA,CAN,SAM,BIZ", directory.getString(IptcDirectory.TAG_DESTINATION));
    }
}
