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
 * Created by dnoakes on 10-Dec-2002 12:22:38 using IntelliJ IDEA.
 */
package com.drew.lang.test;

import com.drew.lang.CompoundException;
import com.drew.lang.NullOutputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 */
public class CompoundExceptionTest extends TestCase
{
    public CompoundExceptionTest(String s)
    {
        super(s);
    }

    public void testUnnestedGetMessage() throws Exception
    {
        try {
            throw new CompoundException("message");
        } catch (CompoundException e) {
            assertEquals("message", e.getMessage());
        }
    }

    public void testNestedGetMessage() throws Exception
    {
        try {
            try {
                throw new IOException("io");
            } catch (IOException e) {
                throw new CompoundException("compound", e);
            }
        } catch (CompoundException e) {
            assertEquals("compound", e.getMessage());
            assertEquals("io", e.getInnerException().getMessage());
        }
    }

    public void testNoInnerException() throws Exception
    {
        try {
            throw new CompoundException("message", null);
        } catch (CompoundException e) {
            try {
                PrintStream nullStream = new PrintStream(new NullOutputStream());
                e.printStackTrace(nullStream);
                e.printStackTrace(new PrintWriter(nullStream));
            } catch (Exception e1) {
                fail("Exception during printStackTrace for CompoundException with no inner exception");
            }
        }
    }
}
