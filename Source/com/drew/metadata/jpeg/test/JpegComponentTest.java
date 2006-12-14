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
import junit.framework.TestCase;

/**
 * 
 */
public class JpegComponentTest extends TestCase
{
    public JpegComponentTest(String s)
    {
        super(s);
    }

    public void testGetComponentCharacter() throws Exception
    {
        JpegComponent component;

        component = new JpegComponent(1,2,3);
        assertEquals("Y", component.getComponentName());

        component = new JpegComponent(2,2,3);
        assertEquals("Cb", component.getComponentName());

        component = new JpegComponent(3,2,3);
        assertEquals("Cr", component.getComponentName());

        component = new JpegComponent(4,2,3);
        assertEquals("I", component.getComponentName());

        component = new JpegComponent(5,2,3);
        assertEquals("Q", component.getComponentName());
    }
}
