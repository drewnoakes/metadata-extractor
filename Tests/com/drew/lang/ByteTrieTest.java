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

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class ByteTrieTest
{
    @Test
    public void testBasics()
    {
        ByteTrie<String> trie = new ByteTrie<String>();

        String[] strings = {"HELLO", "HELLO WORLD", "HERBERT"};

        for (String s : strings)
            trie.addPath(s, s.getBytes());

        for (String s : strings)
            assertSame(s, trie.find(s.getBytes()));

        assertNull(trie.find("Not Included".getBytes()));
        assertNull(trie.find("HELL".getBytes()));
        assertNull(trie.find("H".getBytes()));
        assertEquals("HELLO", trie.find("HELLO MUM".getBytes()));

        assertEquals("HELLO WORLD".length(), trie.getMaxDepth());

        trie.setDefaultValue("DEFAULT");

        assertEquals("DEFAULT", trie.find("Also Not Included".getBytes()));
        assertEquals("DEFAULT", trie.find("H".getBytes()));
        assertEquals("DEFAULT", trie.find("HELL".getBytes()));
    }

    @Test
    public void testDisallowsAddingEmptyPath()
    {
        try {
            new ByteTrie<String>().addPath("Foo", new byte[0]);
            fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            new ByteTrie<String>().addPath("Foo", new byte[0], new byte[0], new byte[0]);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }
}
