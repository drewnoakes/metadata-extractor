/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
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
        File iptcFile = new File("Source/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("City", directory.getDescription(IptcDirectory.TAG_CITY));
    }

    public void testDescription_Caption() throws Exception
    {
        File iptcFile = new File("Source/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("Caption", directory.getDescription(IptcDirectory.TAG_CAPTION));
    }

    public void testDescription_Category() throws Exception
    {
        File iptcFile = new File("Source/com/drew/metadata/iptc/test/withIptc.jpg");
        MetadataReader reader = new IptcReader(iptcFile);
        Metadata metadata = reader.extract();
        assertTrue(metadata.containsDirectory(IptcDirectory.class));
        Directory directory = metadata.getDirectory(IptcDirectory.class);
        assertEquals("Supl. Category2 Supl. Category1 Cat", directory.getDescription(IptcDirectory.TAG_CATEGORY));
    }

    // TODO Wrap more tests around the Iptc reader
}
