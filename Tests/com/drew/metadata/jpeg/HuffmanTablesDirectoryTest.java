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

import static org.junit.Assert.*;

import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable;
import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable.HuffmanTableClass;

/**
 * @author Nadahar
 */
public class HuffmanTablesDirectoryTest
{
    private HuffmanTablesDirectory _directory;

    @Before
    public void setUp()
    {
        _directory = new HuffmanTablesDirectory();
    }

    @Test
    public void testSetAndGetValue() throws Exception
    {
        _directory.setInt(32, 8);
        assertEquals(8, _directory.getInt(32));
    }

    @Test
    public void testGetComponent_NotAdded()
    {
        try {
            _directory.getTable(1);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected exception
        }
    }

    @Test
    public void testGetNumberOfTables() throws Exception
    {
        _directory.setInt(HuffmanTablesDirectory.TAG_NUMBER_OF_TABLES, 9);
        assertEquals(9,_directory.getNumberOfTables());
        assertEquals("9 Huffman tables", _directory.getDescription(HuffmanTablesDirectory.TAG_NUMBER_OF_TABLES));
    }

    @Test
    public void testIsTypical() throws Exception
    {
        _directory.tables.add(new HuffmanTable(
            HuffmanTableClass.AC,
            0,
            HuffmanTablesDirectory.TYPICAL_CHROMINANCE_AC_LENGTHS,
            HuffmanTablesDirectory.TYPICAL_CHROMINANCE_AC_VALUES
        ));
        _directory.tables.add(new HuffmanTable(
            HuffmanTableClass.DC,
            0,
            HuffmanTablesDirectory.TYPICAL_LUMINANCE_DC_LENGTHS,
            HuffmanTablesDirectory.TYPICAL_LUMINANCE_DC_VALUES
        ));

        assertTrue(_directory.getTable(0).isTypical());
        assertFalse(_directory.getTable(0).isOptimized());
        assertTrue(_directory.getTable(1).isTypical());
        assertFalse(_directory.getTable(1).isOptimized());

        assertTrue(_directory.isTypical());
        assertFalse(_directory.isOptimized());
    }
}
