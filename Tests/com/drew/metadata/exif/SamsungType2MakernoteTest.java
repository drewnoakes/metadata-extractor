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

package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.SamsungType2MakernoteDirectory;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertNotNull;

public class SamsungType2MakernoteTest
{
    @Test
    public void testProcessMakernoteSamsungMakerDirectoryWithLowerCaseMaker() throws Exception
    {
        Metadata metadata = new Metadata();
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new StreamReader(new BufferedInputStream(new FileInputStream(new File("Tests/Data/makerlowerCaseSamsung.jpg")))), new ExifReader().getSegmentTypes());
        new ExifReader().readJpegSegments(segmentData.getSegments(JpegSegmentType.APP1), metadata, JpegSegmentType.APP1);
        assertNotNull(metadata);
        assertNotNull(metadata.getFirstDirectoryOfType(SamsungType2MakernoteDirectory.class));
    }
}
