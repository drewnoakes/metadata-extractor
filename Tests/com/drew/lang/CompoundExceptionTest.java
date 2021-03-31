/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.lang;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class CompoundExceptionTest
{
    @Test
    public void testGetMessage_NonNested() throws Exception
    {
        try {
            throw new CompoundException("message");
        } catch (CompoundException e) {
            assertEquals("message", e.getMessage());
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
            assertEquals("compound", e.getMessage());
            final Throwable innerException = e.getInnerException();
            assertNotNull(innerException);
            assertEquals("io", innerException.getMessage());
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
                fail("Exception during printStackTrace for CompoundException with no inner exception");
            }
        }
    }
}
