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
package com.drew.imaging.png;

import com.drew.lang.Iterables;
import com.drew.lang.StreamReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChunkReaderTest
{
    public static List<PngChunk> processFile(String filePath) throws PngProcessingException, IOException
    {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return Iterables.toList(new PngChunkReader().extract(new StreamReader(inputStream), null));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testExtractMspaint() throws Exception
    {
        List<PngChunk> chunks = processFile("Tests/Data/mspaint-8x10.png");

        assertEquals(6, chunks.size());

        assertEquals(PngChunkType.IHDR, chunks.get(0).getType());
        assertEquals(13, chunks.get(0).getBytes().length);

        assertEquals(PngChunkType.sRGB, chunks.get(1).getType());
        assertEquals(1, chunks.get(1).getBytes().length);

        assertEquals(PngChunkType.gAMA, chunks.get(2).getType());
        assertEquals(4, chunks.get(2).getBytes().length);

        assertEquals(PngChunkType.pHYs, chunks.get(3).getType());
        assertEquals(9, chunks.get(3).getBytes().length);

        assertEquals(PngChunkType.IDAT, chunks.get(4).getType());
        assertEquals(17, chunks.get(4).getBytes().length);

        assertEquals(PngChunkType.IEND, chunks.get(5).getType());
        assertEquals(0, chunks.get(5).getBytes().length);
    }

    @Test
    public void testExtractPhotoshop() throws Exception
    {
        List<PngChunk> chunks = processFile("Tests/Data/photoshop-8x12-rgba32.png");

        assertEquals(5, chunks.size());

        assertEquals(PngChunkType.IHDR, chunks.get(0).getType());
        assertEquals(13, chunks.get(0).getBytes().length);

        assertEquals(PngChunkType.tEXt, chunks.get(1).getType());
        assertEquals(25, chunks.get(1).getBytes().length);

        assertEquals(PngChunkType.iTXt, chunks.get(2).getType());
        assertEquals(802, chunks.get(2).getBytes().length);

        assertEquals(PngChunkType.IDAT, chunks.get(3).getType());
        assertEquals(130, chunks.get(3).getBytes().length);

        assertEquals(PngChunkType.IEND, chunks.get(4).getType());
        assertEquals(0, chunks.get(4).getBytes().length);
    }
}
