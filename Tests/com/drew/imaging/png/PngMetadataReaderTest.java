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

import com.drew.lang.KeyValuePair;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.png.PngDirectory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngMetadataReaderTest
{
    @NotNull
    private static Metadata processFile(@NotNull String filePath) throws PngProcessingException, IOException
    {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return PngMetadataReader.readMetadata(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testGimpGreyscaleWithManyChunks() throws Exception
    {
        TimeZone timeZone = TimeZone.getDefault();

        try {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

            Metadata metadata = processFile("Tests/Data/gimp-8x12-greyscale-alpha-time-background.png");
            Collection<PngDirectory> directories = metadata.getDirectoriesOfType(PngDirectory.class);

            assertNotNull(directories);
            assertEquals(6, directories.size());

            PngDirectory[] dirs = new PngDirectory[directories.size()];
            directories.toArray(dirs);

            assertEquals(PngChunkType.IHDR, dirs[0].getPngChunkType());
            assertEquals(8, dirs[0].getInt(PngDirectory.TAG_IMAGE_WIDTH));
            assertEquals(12, dirs[0].getInt(PngDirectory.TAG_IMAGE_HEIGHT));
            assertEquals(8, dirs[0].getInt(PngDirectory.TAG_BITS_PER_SAMPLE));
            assertEquals(4, dirs[0].getInt(PngDirectory.TAG_COLOR_TYPE));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_COMPRESSION_TYPE));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_FILTER_METHOD));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_INTERLACE_METHOD));

            assertEquals(PngChunkType.gAMA, dirs[1].getPngChunkType());
            assertEquals(0.45455, dirs[1].getDouble(PngDirectory.TAG_GAMMA), 0.00001);

            assertEquals(PngChunkType.bKGD, dirs[2].getPngChunkType());
            assertArrayEquals(new byte[]{0, 52}, dirs[2].getByteArray(PngDirectory.TAG_BACKGROUND_COLOR));

            //noinspection ConstantConditions
            assertEquals(PngChunkType.pHYs, dirs[3].getPngChunkType());
            assertEquals(1, dirs[3].getInt(PngDirectory.TAG_UNIT_SPECIFIER));
            assertEquals(2835, dirs[3].getInt(PngDirectory.TAG_PIXELS_PER_UNIT_X));
            assertEquals(2835, dirs[3].getInt(PngDirectory.TAG_PIXELS_PER_UNIT_Y));

            assertEquals(PngChunkType.tIME, dirs[4].getPngChunkType());
            assertEquals("2013:01:01 04:08:30", dirs[4].getString(PngDirectory.TAG_LAST_MODIFICATION_TIME));

            java.util.Date modTime = dirs[4].getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME);
            SimpleDateFormat formatter = new SimpleDateFormat("EE MMM DD HH:mm:ss z yyyy", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            assertEquals("Tue Jan 01 04:08:30 GMT 2013", formatter.format(modTime));
            assertNotNull(modTime);
            assertEquals(1357013310000L, modTime.getTime());

            assertEquals(PngChunkType.iTXt, dirs[5].getPngChunkType());
            @SuppressWarnings("unchecked")
            List<KeyValuePair> pairs = (List<KeyValuePair>)dirs[5].getObject(PngDirectory.TAG_TEXTUAL_DATA);
            assertNotNull(pairs);
            assertEquals(1, pairs.size());
            assertEquals("Comment", pairs.get(0).getKey().toString());
            assertEquals("Created with GIMP", pairs.get(0).getValue().toString());
        } finally {
            TimeZone.setDefault(timeZone);
        }
    }
}
