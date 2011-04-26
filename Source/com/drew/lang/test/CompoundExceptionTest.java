/*
 * Copyright 2002-2011 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.lang.test;

import com.drew.lang.CompoundException;
import com.drew.lang.NullOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class CompoundExceptionTest
{
    @Test
    public void testGetMessage_NonNested() throws Exception
    {
        try {
            throw new CompoundException("message");
        } catch (CompoundException e) {
            Assert.assertEquals("message", e.getMessage());
        }
    }

    @Test
    public void testGetMessage_Nested() throws Exception
    {
        try {
            try {
                throw new IOException("io");
            } catch (IOException e) {
                throw new CompoundException("compound", e);
            }
        } catch (CompoundException e) {
            Assert.assertEquals("compound", e.getMessage());
            Assert.assertEquals("io", e.getInnerException().getMessage());
        }
    }

    @Test
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
                Assert.fail("Exception during printStackTrace for CompoundException with no inner exception");
            }
        }
    }
}
