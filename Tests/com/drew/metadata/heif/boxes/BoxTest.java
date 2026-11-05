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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.heif.boxes;

import com.drew.imaging.isobmff.IsoBox;
import com.drew.imaging.isobmff.IsoBoxIterator;
import com.drew.lang.SequentialByteArrayReader;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BoxTest
{
    @Test
    public void testFromIsoBoxMatchesAReadHeader() throws Exception
    {
        byte[][] headers = {
            ByteBuffer.allocate(12).putInt(12).put("data".getBytes("ISO-8859-1")).array(),
            ByteBuffer.allocate(20).putInt(1).put("data".getBytes("ISO-8859-1")).putLong(20).array(),
            ByteBuffer.allocate(28).putInt(28).put("uuid".getBytes("ISO-8859-1")).put(new byte[16]).array()
        };
        for (byte[] header : headers) {
            Box read = new Box(new SequentialByteArrayReader(header));
            IsoBox iterated = new IsoBoxIterator(header, 0, header.length).next();
            Box converted = new Box(iterated);
            assertEquals(read.size, converted.size);
            assertEquals(read.type, converted.type);
            assertEquals(read.usertype, converted.usertype);
            assertEquals(read.headerSize, converted.headerSize);
        }
    }
}
