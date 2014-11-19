package com.drew.lang;

import org.junit.Test;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class SequentialByteArrayReaderTest extends SequentialAccessTestBase
{
    @SuppressWarnings({"ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullStreamThrows()
    {
        new SequentialByteArrayReader(null);
    }

    @Override
    protected SequentialReader createReader(byte[] bytes)
    {
        return new SequentialByteArrayReader(bytes);
    }
}
