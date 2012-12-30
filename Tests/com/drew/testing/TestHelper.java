/*
 * Copyright 2002-2012 Drew Noakes
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
package com.drew.testing;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import junit.framework.TestCase;

import java.io.*;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class TestHelper
{
    public static void assertEqualArrays(@NotNull byte[] array1, @NotNull byte[] array2)
    {
        TestCase.assertEquals("Equal array length", array1.length, array2.length);

        for (int i = 0; i<array1.length; i++)
            TestCase.assertEquals("Equal value at index " + i, array1[i], array2[i]);
    }

    @NotNull
    public static byte[] loadFileBytes(@NotNull File file) throws IOException
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

    @NotNull
    public static Metadata readJpegMetadataFile(String path) throws JpegProcessingException, IOException, ClassNotFoundException
    {
        final JpegSegmentData data = JpegSegmentData.fromFile(new File(path));

        Metadata metadata = new Metadata();
        JpegMetadataReader.processJpegSegmentData(metadata, JpegMetadataReader.ALL_READERS, data);
        return metadata;
    }

    public static byte[] readFileBytes(File file) throws IOException
    {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read;
            while ((read = ios.read(buffer)) != -1)
                ous.write(buffer, 0, read);
        } finally {
            if (ous != null)
                ous.close();
            if (ios != null)
                ios.close();
        }
        return ous.toByteArray();
    }

    public static byte[] readFileBytes(String fileName) throws IOException
    {
        return readFileBytes(new File(fileName));
    }

    public static byte[] skipBytes(byte[] input, int countToSkip)
    {
        if (input.length - countToSkip < 0) {
            throw new IllegalArgumentException("Attempting to skip more bytes than exist in the array.");
        }

        byte[] output = new byte[input.length - countToSkip];
        System.arraycopy(input, countToSkip, output, 0, input.length - countToSkip);
        return output;
    }
}
