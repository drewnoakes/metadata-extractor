/*
 * Created by dnoakes on 22-Nov-2002 08:26:26 using IntelliJ IDEA.
 */
package com.drew.metadata.iptc.test;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.iptc.IptcReader;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class IptcReaderTest extends TestCase
{
    public IptcReaderTest(String s)
    {
        super(s);
    }

    public void testDescription_City() throws Exception
    {
        File iptcFile = new File("src/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("City", directory.getDescription(IptcDirectory.TAG_CITY));
    }

    public void testDescription_Caption() throws Exception
    {
        File iptcFile = new File("src/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("Caption", directory.getDescription(IptcDirectory.TAG_CAPTION));
    }

    public void testDescription_Category() throws Exception
    {
        File iptcFile = new File("src/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("Supl. Category2 Supl. Category1 Cat", directory.getDescription(IptcDirectory.TAG_CATEGORY));
    }

    // TODO Wrap more tests around the Iptc reader
}
