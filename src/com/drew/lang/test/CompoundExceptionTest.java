/*
 * Created by dnoakes on 10-Dec-2002 12:22:38 using IntelliJ IDEA.
 */
package com.drew.lang.test;

import com.drew.lang.CompoundException;
import junit.framework.TestCase;

import java.io.IOException;
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
                e.printStackTrace();
                e.printStackTrace(System.err);
                e.printStackTrace(new PrintWriter(System.err));
            } catch (Exception e1) {
                fail("Exception during printStackTrace for CompoundException with no inner exception");
            }
        }
    }
}
