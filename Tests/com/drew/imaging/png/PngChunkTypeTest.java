package com.drew.imaging.png;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChunkTypeTest
{
    @Test
    public void testConstructorTooLong() throws Exception
    {
        try {
            new PngChunkType("TooLong");
            fail("Expecting exception");
        } catch (IllegalArgumentException ex) {
            assertEquals("PNG chunk type identifier must be four bytes in length", ex.getMessage());
        }
    }

    @Test
    public void testConstructorTooShort() throws Exception
    {
        try {
            new PngChunkType("foo");
            fail("Expecting exception");
        } catch (IllegalArgumentException ex) {
            assertEquals("PNG chunk type identifier must be four bytes in length", ex.getMessage());
        }
    }

    @Test
    public void testConstructorInvalidBytes() throws Exception
    {
        String[] invalidStrings = {"ABC1", "1234", "    ", "!£$%"};

        for (String invalidString : invalidStrings) {
            try {
                new PngChunkType(invalidString);
                fail("Expecting exception");
            } catch (IllegalArgumentException ex) {
                assertEquals("PNG chunk type identifier may only contain alphabet characters", ex.getMessage());
            }
        }
    }

    @Test
    public void testConstructorValidBytes() throws Exception
    {
        String[] validStrings = {"ABCD", "abcd", "wxyz", "WXYZ", "lkjh", "LKJH"};

        for (String validString : validStrings) {
            new PngChunkType(validString);
        }
    }

    @Test
    public void testIsCritical() throws Exception
    {
        assertTrue(new PngChunkType("ABCD").isCritical());
        assertFalse(new PngChunkType("aBCD").isCritical());
    }

    @Test
    public void testIsAncillary() throws Exception
    {
        assertFalse(new PngChunkType("ABCD").isAncillary());
        assertTrue(new PngChunkType("aBCD").isAncillary());
    }

    @Test
    public void testIsPrivate() throws Exception
    {
        assertTrue(new PngChunkType("ABCD").isPrivate());
        assertFalse(new PngChunkType("AbCD").isPrivate());
    }

    @Test
    public void testIsSafeToCopy() throws Exception
    {
        assertFalse(new PngChunkType("ABCD").isSafeToCopy());
        assertTrue(new PngChunkType("ABCd").isSafeToCopy());
    }

    @Test
    public void testAreMultipleAllowed() throws Exception
    {
        assertFalse(new PngChunkType("ABCD").areMultipleAllowed());
        assertFalse(new PngChunkType("ABCD", false).areMultipleAllowed());
        assertTrue(new PngChunkType("ABCD", true).areMultipleAllowed());
    }

    @Test
    public void testEquality() throws Exception
    {
        assertEquals(new PngChunkType("ABCD"), new PngChunkType("ABCD"));
        assertEquals(new PngChunkType("ABCD", true), new PngChunkType("ABCD", true));
        assertEquals(new PngChunkType("ABCD", false), new PngChunkType("ABCD", false));
        // NOTE we don't consider the 'allowMultiples' value in the equality test (or hash code)
        assertEquals(new PngChunkType("ABCD", true), new PngChunkType("ABCD", false));

        assertNotEquals(new PngChunkType("ABCD"), new PngChunkType("abcd"));
    }
}
