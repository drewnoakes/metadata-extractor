package com.drew.metadata.jpeg.test;

import com.drew.metadata.jpeg.JpegComponent;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: dnoakes
 * Date: 09-Oct-2003
 * Time: 15:22:23
 * To change this template use Options | File Templates.
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
