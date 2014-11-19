package com.drew.lang;

import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class StreamReaderTest extends SequentialAccessTestBase
{
    @SuppressWarnings({"ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullStreamThrows()
    {
        new StreamReader(null);
    }

    @Override
    protected SequentialReader createReader(byte[] bytes)
    {
        return new StreamReader(new ByteArrayInputStream(bytes));
    }
}
