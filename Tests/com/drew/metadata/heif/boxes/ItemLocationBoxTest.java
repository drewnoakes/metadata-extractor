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
package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class ItemLocationBoxTest
{
    /**
     * An {@code iloc} box may declare {@code offset_size == 0}, meaning the per-extent offset field is
     * absent and its value is 0 (the location is carried entirely by {@code base_offset}). This is valid
     * per ISO/IEC 14496-12 and is emitted by real AVIF encoders (e.g. link-u/cavif).
     *
     * Previously {@code getIntFromUnknownByte(0, ...)} returned {@code null}, which was then unboxed into
     * the primitive {@code long extentOffset}, throwing a {@link NullPointerException}.
     */
    @Test public void testZeroOffsetSizeDoesNotThrow() throws Exception
    {
        // A version-0 iloc box with offset_size=0, length_size=4, base_offset_size=4 and a single item
        // holding a single extent. Bytes taken from link-u's avif-sample-images
        // (fox.profile0.10bpc.yuv420.monochrome.avif).
        final byte[] ilocData = new byte[] {
            0x00, 0x00, 0x00, 0x1E,             // box size = 30
            0x69, 0x6C, 0x6F, 0x63,             // box type = 'iloc'
            0x00, 0x00, 0x00, 0x00,             // version = 0, flags = 0
            0x04,                               // offset_size = 0, length_size = 4
            0x40,                               // base_offset_size = 4, (reserved)
            0x00, 0x01,                         // item_count = 1
            0x00, 0x01,                         // item_ID = 1
            0x00, 0x00,                         // data_reference_index = 0
            0x00, 0x00, 0x01, 0x43,             // base_offset = 323
            0x00, 0x01,                         // extent_count = 1
            0x00, 0x00, 0x17, (byte) 0x87       // extent_length = 6023 (offset field absent: offset_size=0)
        };

        SequentialReader reader = new SequentialByteArrayReader(ilocData);
        Box box = new Box(reader);
        assertEquals("iloc", box.type);

        ItemLocationBox itemLocationBox = new ItemLocationBox(reader, box);

        assertEquals(1, itemLocationBox.getExtents().size());
        ItemLocationBox.Extent extent = itemLocationBox.getExtents().first();
        assertEquals(1, extent.getItemId());
        assertNull(extent.index);                       // no index when index_size is absent
        assertEquals(323, extent.getOffset());          // base_offset (323) + extent offset (0)
        assertEquals(6023, extent.getLength());
    }
}
