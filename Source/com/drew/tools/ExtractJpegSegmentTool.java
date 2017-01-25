/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.tools;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.Iterables;
import com.drew.lang.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extracts JPEG segments and writes them to individual files.
 * <p>
 * Extracting only the required segment(s) for use in unit testing has several benefits:
 * <ul>
 *     <li>Helps reduce the repository size. For example a small JPEG image may still be 20kB+ in size, yet its
 *         APPD (IPTC) segment may be as small as 200 bytes.</li>
 *     <li>Makes unit tests run more rapidly.</li>
 *     <li>Partially anonymises user-contributed data by removing image portions.</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExtractJpegSegmentTool
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String filePath = args[0];

        if (!new File(filePath).exists()) {
            System.err.println("File does not exist");
            printUsage();
            System.exit(1);
        }

        Set<JpegSegmentType> segmentTypes = new HashSet<JpegSegmentType>();

        for (int i = 1; i < args.length; i++) {
            JpegSegmentType segmentType = JpegSegmentType.valueOf(args[i].toUpperCase());
            if (!segmentType.canContainMetadata) {
                System.err.printf("WARNING: Segment type %s cannot contain metadata so it may not be necessary to extract it%n", segmentType);
            }
            segmentTypes.add(segmentType);
        }

        if (segmentTypes.size() == 0) {
            // If none specified, use all that could reasonably contain metadata
            segmentTypes.addAll(JpegSegmentType.canContainMetadataTypes);
        }

        System.out.println("Reading: " + filePath);

        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File(filePath), segmentTypes);

        saveSegmentFiles(filePath, segmentData);
    }

    public static void saveSegmentFiles(@NotNull String jpegFilePath, @NotNull JpegSegmentData segmentData) throws IOException
    {
        for (JpegSegmentType segmentType : segmentData.getSegmentTypes()) {
            List<byte[]> segments = Iterables.toList(segmentData.getSegments(segmentType));
            if (segments.size() == 0) {
                continue;
            }

            if (segments.size() > 1) {
                for (int i = 0; i < segments.size(); i++) {
                    String outputFilePath = String.format("%s.%s.%d", jpegFilePath, segmentType.toString().toLowerCase(), i);
                    System.out.println("Writing: " + outputFilePath);
                    FileUtil.saveBytes(new File(outputFilePath), segments.get(i));
                }
            } else {
                String outputFilePath = String.format("%s.%s", jpegFilePath, segmentType.toString().toLowerCase());
                System.out.println("Writing: " + outputFilePath);
                FileUtil.saveBytes(new File(outputFilePath), segments.get(0));
            }
        }
    }

    private static void printUsage()
    {
        System.out.println("USAGE:\n");
        System.out.println("\tjava com.drew.tools.ExtractJpegSegmentTool <filename> [<segment> ...]\n");

        System.out.print("Where <segment> is zero or more of:");
        for (JpegSegmentType segmentType : JpegSegmentType.class.getEnumConstants()) {
            if (segmentType.canContainMetadata) {
                System.out.print(" " + segmentType.toString());
            }
        }
        System.out.println();
    }
}
