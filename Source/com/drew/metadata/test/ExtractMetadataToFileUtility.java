/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 07-May-2005 12:38:18 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;

import java.io.File;
import java.io.IOException;

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

        JpegSegmentData segmentData = new JpegSegmentReader(new File(filePath)).getSegmentData();
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_DHT);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_DQT);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_SOF0);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_SOI);

        System.out.println("Writing output to: " + outputFilePath);
        JpegSegmentData.toFile(new File(outputFilePath), segmentData);
    }
}