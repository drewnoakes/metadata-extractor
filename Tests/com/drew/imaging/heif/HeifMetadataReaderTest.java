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
package com.drew.imaging.heif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.heif.HeifDirectory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import static com.drew.metadata.heif.HeifDirectory.*;
import static org.junit.Assert.*;

public class HeifMetadataReaderTest
{
    @NotNull
    private static Metadata processFile(@NotNull String filePath) throws IOException
    {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return HeifMetadataReader.readMetadata(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private <D extends Directory> int getIntInAnyDir(Collection<D> directories, int tagType, int defaultValue) throws MetadataException {
        for (D dir : directories) {
            if (dir.containsTag(tagType)) {
                return dir.getInt(tagType);
            }
        }
        return defaultValue;
    }

    private void doTestImage(
        @NotNull String filePath, int expectedWidth, int expectedHeight, int expectedRotation
    ) throws Exception
    {
        Metadata metadata = processFile(filePath);
        Collection<HeifDirectory> dirs = metadata.getDirectoriesOfType(HeifDirectory.class);

        assertNotNull(dirs);
        assertFalse(dirs.isEmpty());

        assertEquals(expectedWidth, getIntInAnyDir(dirs, TAG_IMAGE_WIDTH, -1));
        assertEquals(expectedHeight, getIntInAnyDir(dirs, TAG_IMAGE_HEIGHT, -1));
        assertEquals(expectedRotation, getIntInAnyDir(dirs, TAG_IMAGE_ROTATION, -1));
    }

    @Test
    public void testStillImage() throws Exception
    {
        doTestImage("Tests/Data/heif/still-images/autumn_1440x960.heic", 1440, 960, -1);
    }

    @Test
    public void testDerivedImageGrid() throws Exception
    {
        doTestImage("Tests/Data/heif/image-derivations/grid_960x640.heic", 960, 640, -1);
    }

    @Test
    public void testDerivedImageOverlay() throws Exception
    {
        doTestImage("Tests/Data/heif/image-derivations/overlay_1000x680.heic", 1000, 680, -1);
    }

    @Test
    public void testImageCollection1() throws Exception
    {
        doTestImage("Tests/Data/heif/image-collections/random_collection_1440x960.heic", 1440, 960, -1);
    }

    @Test
    public void testImageCollection2() throws Exception
    {
        doTestImage("Tests/Data/heif/image-collections/season_collection_1440x960.heic", 1440, 960, -1);
    }

    @Test
    public void testImageSequence1() throws Exception
    {
        doTestImage("Tests/Data/heif/image-sequences/sea1_animation.heic", 256, 144, -1);
    }

    @Test
    public void testImageSequence2() throws Exception
    {
        doTestImage("Tests/Data/heif/image-sequences/starfield_animation.heic", 256, 144, -1);
    }

    @Test
    public void testAuxiliaryImageStorage() throws Exception
    {
        doTestImage("Tests/Data/heif/auxiliary-image-storage/alpha_1440x960.heic", 1440, 960, -1);
    }

    // Does not currently work
    /*
    @Test
    public void testImageBurst1() throws Exception
    {
        doTestImage("Tests/Data/heif/image-bursts/bird_burst.heic", 640, 360, -1);
    }

    @Test
    public void testImageBurst2() throws Exception
    {
        doTestImage("Tests/Data/heif/image-bursts/rally_burst.heic", 640, 360, -1);
    }
    */
}
