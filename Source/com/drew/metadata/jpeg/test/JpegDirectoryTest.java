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
 * Created by dnoakes on 09-Oct-2003 15:22:23 using IntelliJ IDEA.
 */
package com.drew.metadata.jpeg.test;

import com.drew.metadata.jpeg.JpegComponent;
import com.drew.metadata.jpeg.JpegDirectory;
import junit.framework.TestCase;

/**
 * 
 */
public class JpegDirectoryTest extends TestCase
{
    private JpegDirectory _directory;

    public JpegDirectoryTest(String s)
    {
        super(s);
    }

    public void setUp()
    {
        _directory = new JpegDirectory();
    }

    public void testSetAndGetValue() throws Exception
    {
        _directory.setInt(123, 8);
        assertEquals(8, _directory.getInt(123));
    }

    public void testGetComponent_NotAdded()
    {
        assertNull(_directory.getComponent(1));
    }

    // NOTE tests for individual tag values exist in JpegReaderTest.java

    public void testGetImageWidth() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, 123);
        assertEquals(123, _directory.getImageWidth());
    }

    public void testGetImageHeight() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, 123);
        assertEquals(123, _directory.getImageHeight());
    }


    public void testGetNumberOfComponents() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS, 3);
        assertEquals(3, _directory.getNumberOfComponents());
        assertEquals("3", _directory.getDescription(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS));
    }

    public void testGetComponent() throws Exception
    {
        JpegComponent component1 = new JpegComponent(1, 2, 3);
        JpegComponent component2 = new JpegComponent(1, 2, 3);
        JpegComponent component3 = new JpegComponent(1, 2, 3);
        JpegComponent component4 = new JpegComponent(1, 2, 3);

        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1, component1);
        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_2, component2);
        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_3, component3);
        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_4, component4);

        // component numbers are zero-indexed for this method
        assertSame(component1, _directory.getComponent(0));
        assertSame(component2, _directory.getComponent(1));
        assertSame(component3, _directory.getComponent(2));
        assertSame(component4, _directory.getComponent(3));
    }
}
