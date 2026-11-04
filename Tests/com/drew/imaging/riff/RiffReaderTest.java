/*
 * Copyright 2002-2026 Drew Noakes and contributors
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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.riff;

import com.drew.lang.StreamReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Claude (metadata-extractor contributor)
 */
public class RiffReaderTest
{
    @Test
    public void testDeeplyNestedListsReportErrorRatherThanOverflowingStack() throws Exception
    {
        // Every chunk is individually well formed, so only the nesting depth is abnormal. Without a
        // depth bound this recurses once per level and throws StackOverflowError (an Error, not an
        // IOException), which escapes the reader.
        CollectingHandler handler = read(nestedLists(10000));

        assertTrue(handler.errors.contains("RIFF nesting too deep."));
    }

    @Test
    public void testNestingLimit() throws Exception
    {
        assertFalse(read(nestedLists(100)).errors.contains("RIFF nesting too deep."));
        assertTrue(read(nestedLists(101)).errors.contains("RIFF nesting too deep."));
    }

    private static CollectingHandler read(byte[] bytes) throws Exception
    {
        CollectingHandler handler = new CollectingHandler();
        new RiffReader().processRiff(new StreamReader(new ByteArrayInputStream(bytes)), handler);
        return handler;
    }

    /** Builds a RIFF file whose body is {@code depth} LIST chunks nested one inside the next. */
    private static byte[] nestedLists(int depth)
    {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (int i = 0; i < depth; i++) {
            int deeper = depth - 1 - i;              // LIST chunks nested below this one
            fourCC(body, "LIST");
            int32LE(body, 4 + 12 * deeper);          // list type (4 bytes) + nested LIST chunks (12 bytes each)
            fourCC(body, "INFO");
        }
        byte[] b = body.toByteArray();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        fourCC(out, "RIFF");
        int32LE(out, 4 + b.length);                  // identifier (4 bytes) + body
        fourCC(out, "TEST");                         // RIFF identifier
        out.write(b, 0, b.length);
        return out.toByteArray();
    }

    private static void fourCC(ByteArrayOutputStream out, String value)
    {
        for (int i = 0; i < 4; i++) {
            out.write(value.charAt(i));
        }
    }

    private static void int32LE(ByteArrayOutputStream out, int value)
    {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    private static final class CollectingHandler implements RiffHandler
    {
        final List<String> errors = new ArrayList<String>();

        public boolean shouldAcceptRiffIdentifier(String identifier) { return true; }

        public boolean shouldAcceptChunk(String fourCC) { return false; }

        public boolean shouldAcceptList(String fourCC) { return true; }

        public void processChunk(String fourCC, byte[] payload) { }

        public void addError(String message) { errors.add(message); }
    }
}
