/**
 * Created by IntelliJ IDEA.
 * User: dnoakes
 * Date: Dec 15, 2002
 * Time: 3:30:02 PM
 * To change this template use Options | File Templates.
 */
package com.drew.lang.test;

import junit.framework.TestCase;

import java.io.OutputStream;

import com.drew.lang.NullOutputStream;

public class NullOutputStreamTest extends TestCase
{
    public NullOutputStreamTest(String s)
    {
        super(s);
    }

    public void testCreateNullOutputStream() throws Exception
    {
        OutputStream out = new NullOutputStream();
        out.write(1);
    }
}
