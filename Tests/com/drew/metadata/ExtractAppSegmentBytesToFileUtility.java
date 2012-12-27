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

package com.drew.metadata;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.StreamReader;

import java.io.*;

/** @author Drew Noakes http://drewnoakes.com */
public class ExtractAppSegmentBytesToFileUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length < 2) {
            System.err.println("Expects at least two arguments:\n\n    <filename> <appSegmentNumber> [<segmentOccurrence>]");
            System.exit(1);
        }

        byte segmentNumber = Byte.parseByte(args[1]);
        if (segmentNumber > 0xF) {
            System.err.println("Segment number must be between 0 (App0) and 15 (AppF).");
            System.exit(1);
        }
        byte segment = (byte)(JpegSegmentType.APP0.byteValue + segmentNumber);

        String filePath = args[0];

        JpegSegmentData segmentData;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            segmentData = JpegSegmentReader.readSegments(new StreamReader(inputStream), null);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        final int segmentCount = segmentData.getSegmentCount(segment);
        if (segmentCount == 0) {
            System.err.printf("No data was found in app segment %d.\n", segmentNumber);
            System.exit(1);
        }
        if (segmentCount != 1 && args.length == 2) {
            System.err.printf("%d occurrences of segment %d found.  You must specify which index to use (zero based).\n", segmentCount, segmentNumber);
            System.exit(1);
        }

        int occurrence = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        if (occurrence >= segmentCount) {
            System.err.printf("Invalid occurrence number.  Requested %d but only %d segments exist.\n", occurrence, segmentCount);
            System.exit(1);
        }

        String outputFilePath = filePath + ".app" + segmentNumber + "bytes";

        final byte[] bytes = segmentData.getSegment(segment, occurrence);

        System.out.println("Writing output to: " + outputFilePath);

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(outputFilePath);
            stream.write(bytes);
        } finally {
            if (stream != null)
                stream.close();
        }
    }

    public static byte[] read(File file) throws IOException
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
}
