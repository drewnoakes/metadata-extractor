package com.drew.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ByteConvertTest
{
    @Test
    public void toInt32BigEndian()
    {
        assertEquals(0x01020304, ByteConvert.toInt32BigEndian(new byte[]{1, 2, 3, 4}));
        assertEquals(0x01020304, ByteConvert.toInt32BigEndian(new byte[]{1, 2, 3, 4, 5}));
    }

    @Test
    public void toInt32LittleEndian()
    {
        assertEquals(0x04030201, ByteConvert.toInt32LittleEndian(new byte[]{1, 2, 3, 4}));
        assertEquals(0x04030201, ByteConvert.toInt32LittleEndian(new byte[]{1, 2, 3, 4, 5}));
    }
}
