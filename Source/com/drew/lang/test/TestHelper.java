/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.lang.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.metadata.Metadata;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class TestHelper
{
    public static void assertEqualArrays(byte[] array1, byte[] array2)
    {
        TestCase.assertEquals("Equal array length", array1.length, array2.length);

        for (int i = 0; i<array1.length; i++)
            TestCase.assertEquals("Equal value at index " + i, array1[i], array2[i]);
    }

    public static byte[] loadFileBytes(File file) throws IOException
    {
        long length = file.length();

        if (length > Integer.MAX_VALUE)
            throw new IOException("File is too large to be read into memory.");

        byte[] bytes = new byte[(int)length];

        InputStream stream = new FileInputStream(file);
        try {
        // Read in the bytes
        int offset = 0;
        int numRead;
        while (offset < bytes.length && (numRead = stream.read(bytes, offset, bytes.length - offset)) >= 0)
            offset += numRead;

        if (offset < length)
            throw new IOException("Could not completely read file " + file.getName());
        } finally {
            stream.close();
        }
        return bytes;
    }

    public static Metadata readJpegMetadataFile(String path) throws ClassNotFoundException, IOException
    {
        final File file = new File(path);
        final JpegSegmentData data = JpegSegmentData.fromFile(file);
        return JpegMetadataReader.extractMetadataFromJpegSegmentReader(data);
    }
}
