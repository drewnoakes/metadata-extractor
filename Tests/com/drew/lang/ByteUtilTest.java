/*
 * Copyright 2002-2018 Drew Noakes
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
 * @author Nadahar
 */
public class ByteUtilTest
{
    @Test
    public void testGetInt16() {
        byte[] buffer = new byte[] {0x7F, (byte) 0xFF};
        assertEquals(0x7FFF, ByteUtil.getInt16(buffer, 0, true));
        assertEquals(0xFF7F, ByteUtil.getInt16(buffer, 0, false));

        buffer = new byte[] {(byte) 0xFF, (byte) 0xFF};
        assertEquals(0xFFFF, ByteUtil.getInt16(buffer, 0, true));
        assertEquals(0xFFFF, ByteUtil.getInt16(buffer, 0, false));

        buffer = new byte[] {0x1, 0x0};
        assertEquals(0x100, ByteUtil.getInt16(buffer, 0, true));
        assertEquals(0x1, ByteUtil.getInt16(buffer, 0, false));

        buffer = new byte[] {0x7F, (byte) 0xFF, 0x7F, (byte) 0xFF, 0x7F, (byte) 0xFF};
        assertEquals(0xFF7F, ByteUtil.getInt16(buffer, 1, true));
        assertEquals(0x7FFF, ByteUtil.getInt16(buffer, 1, false));
        assertEquals(0x7FFF, ByteUtil.getInt16(buffer, 2, true));
        assertEquals(0xFF7F, ByteUtil.getInt16(buffer, 2, false));
    }

    @Test
    public void testGetInt32() {
        byte[] buffer = new byte[] {0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertEquals(0x7FFFFFFF, ByteUtil.getInt32(buffer, 0, true));
        assertEquals(0xFFFFFF7F, ByteUtil.getInt32(buffer, 0, false));

        buffer = new byte[] {(byte) 0xFF, (byte) 0xFF, 0x0, 0x0};
        assertEquals(0xFFFF0000, ByteUtil.getInt32(buffer, 0, true));
        assertEquals(0xFFFF, ByteUtil.getInt32(buffer, 0, false));

        buffer = new byte[] {0x1, 0x0, 0x1, 0x0};
        assertEquals(0x1000100, ByteUtil.getInt32(buffer, 0, true));
        assertEquals(0x10001, ByteUtil.getInt32(buffer, 0, false));

        buffer = new byte[] {0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertEquals(0xFFFFFF7F, ByteUtil.getInt32(buffer, 1, true));
        assertEquals(0x7FFFFFFF, ByteUtil.getInt32(buffer, 1, false));
        assertEquals(0x7FFFFFFF, ByteUtil.getInt32(buffer, 4, true));
        assertEquals(0xFFFFFF7F, ByteUtil.getInt32(buffer, 4, false));
    }

    @Test
    public void testGetInt64() {
        byte[] buffer = new byte[] {
            0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        assertEquals(0x7FFFFFFFFFFFFFFFL, ByteUtil.getLong64(buffer, 0, true));
        assertEquals(0xFFFFFFFFFFFFFF7FL, ByteUtil.getLong64(buffer, 0, false));

        buffer = new byte[] {(byte) 0xFF, (byte) 0xFF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
        assertEquals(0xFFFF000000000000L, ByteUtil.getLong64(buffer, 0, true));
        assertEquals(0xFFFFL, ByteUtil.getLong64(buffer, 0, false));

        buffer = new byte[] {0x1, 0x0, 0x1, 0x0, 0x1, 0x0, 0x1, 0x0};
        assertEquals(0x100010001000100L, ByteUtil.getLong64(buffer, 0, true));
        assertEquals(0x1000100010001L, ByteUtil.getLong64(buffer, 0, false));

        buffer = new byte[] {
            0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        assertEquals(0xFFFFFF7FFFFFFF7FL, ByteUtil.getLong64(buffer, 1, true));
        assertEquals(0x7FFFFFFF7FFFFFFFL, ByteUtil.getLong64(buffer, 1, false));
        assertEquals(0x7FFFFFFF7FFFFFFFL, ByteUtil.getLong64(buffer, 8, true));
        assertEquals(0xFFFFFF7FFFFFFF7FL, ByteUtil.getLong64(buffer, 8, false));
    }
}
