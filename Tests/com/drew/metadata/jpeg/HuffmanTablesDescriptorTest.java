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
package com.drew.metadata.jpeg;

import org.junit.Before;
import org.junit.Test;

import static com.drew.metadata.jpeg.HuffmanTablesDirectory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Nadahar
 */
public class HuffmanTablesDescriptorTest
{
    private HuffmanTablesDirectory _directory;
    private HuffmanTablesDescriptor _descriptor;

    @Before
    public void setUp() throws Exception
    {
        _directory = new HuffmanTablesDirectory();
        _descriptor = new HuffmanTablesDescriptor(_directory);
    }

    @Test
    public void testGetNumberOfTablesDescription() throws Exception
    {
        assertNull(_descriptor.getNumberOfTablesDescription());
        _directory.setInt(TAG_NUMBER_OF_TABLES, 0);
        assertEquals("0 Huffman tables", _descriptor.getNumberOfTablesDescription());
        assertEquals("0 Huffman tables", _descriptor.getDescription(TAG_NUMBER_OF_TABLES));
        _directory.setInt(TAG_NUMBER_OF_TABLES, 1);
        assertEquals("1 Huffman table", _descriptor.getNumberOfTablesDescription());
        assertEquals("1 Huffman table", _descriptor.getDescription(TAG_NUMBER_OF_TABLES));
        _directory.setInt(TAG_NUMBER_OF_TABLES, 3);
        assertEquals("3 Huffman tables", _descriptor.getNumberOfTablesDescription());
        assertEquals("3 Huffman tables", _descriptor.getDescription(TAG_NUMBER_OF_TABLES));

    }
}
