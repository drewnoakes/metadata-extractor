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

import java.io.File;
import java.io.IOException;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExtractMetadataToFileUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length != 1) {
            System.err.println("Expects a single argument of the file path to process.");
            System.exit(1);
        }

        String filePath = args[0];
        String outputFilePath = filePath + ".metadata";

        JpegSegmentData segmentData = JpegSegmentReader.readSegments(filePath);
        segmentData.removeSegment(JpegSegmentType.DHT);
        segmentData.removeSegment(JpegSegmentType.DQT);
        segmentData.removeSegment(JpegSegmentType.SOF0);
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 1));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 2));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 3)); // No 4
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 5));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 6));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 7));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 8));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 9));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 10));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 11)); // No 12
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 13));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 14));
        segmentData.removeSegment((byte)(JpegSegmentType.SOF0.byteValue + 15));
        segmentData.removeSegment(JpegSegmentType.SOI);

        System.out.println("Writing output to: " + outputFilePath);
        JpegSegmentData.toFile(new File(outputFilePath), segmentData);
    }
}
