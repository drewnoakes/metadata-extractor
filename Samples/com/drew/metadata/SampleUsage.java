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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;

import java.io.File;
import java.io.IOException;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGDecodeParam;
//import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * Shows example usages of the metadata-extractor library.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class SampleUsage
{
    /**
     * Constructor which executes multiple sample usages, each of which return the same output.  This class showcases
     * multiple usages of this metadata class library.
     * @param fileName path to a file upon which to operate
     */
    public SampleUsage(String fileName)
    {
        File file = new File(fileName);

        // There are multiple ways to get a Metadata object for a file

        // Approach 1
        // This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
        // readers.  In most cases, this is the most appropriate usage.  This will handle Jpeg, TIFF and RAW
        // (CRW/CR2/NEF) files and extract Exif & IPTC metadata as available.
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            printImageTags(1, metadata);
        } catch (ImageProcessingException e) {
            System.err.println("error 1a: " + e);
        } catch (IOException e) {
            System.err.println("error 1b: " + e);
        }

        // Approach 2
        // If you know the file to be a Jpeg, you may invoke the JpegMetadataReader, rather than the generic reader
        // used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image, you might use TiffMetadataReader.
        // Using the specific reader offers a very, very slight performance improvement.
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file);
            printImageTags(1, metadata);
        } catch (JpegProcessingException e) {
            System.err.println("error 2a: " + e);
        } catch (IOException e) {
            System.err.println("error 2b: " + e);
        }

        // Approach 3
        // As fast as approach 1 (this is what goes on inside the JpegMetadataReader's readMetadata() method), this code
        // is handy if you want to look into other Jpeg segments as well.
        try {
            JpegSegmentReader segmentReader = new JpegSegmentReader(file);
            byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
            byte[] iptcSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD);
            Metadata metadata = new Metadata();
            if (exifSegment != null)
                new ExifReader().extract(exifSegment, metadata);
            if (iptcSegment != null)
                new IptcReader().extract(iptcSegment, metadata);
            printImageTags(3, metadata);
        } catch (JpegProcessingException e) {
            System.err.println("error 3a: " + e);
        } catch (IOException e) {
            System.err.println("error 3b: " + e);
        }
        
/*
        // Approach 4
        // This approach is the slowest, because it decodes the Jpeg image.  Of course you now have a decoded image to
        // play with.  In some instances this will be most appropriate.
        // The compiler will produce warnings about using com.sun.image.codec.jpeg, but this example is shown in case it's useful.
        try {
            JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(file));
            BufferedImage image = jpegDecoder.decodeAsBufferedImage();
            // now you can use the image
            JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
            final Metadata metadata = new Metadata();

            // We should only really be seeing Exif in exifSegment[0]... the 2D array exists because markers can theoretically appear multiple times in the file.
            byte[][] exifSegment = decodeParam.getMarkerData(JPEGDecodeParam.APP1_MARKER);
            if (exifSegment != null && exifSegment[0].length>0) {
                new ExifReader(exifSegment[0]).extract(metadata);
            }

            // similarly, use only the first IPTC segment
            byte[][] iptcSegment = decodeParam.getMarkerData(JPEGDecodeParam.APPD_MARKER);
            if (iptcSegment != null && iptcSegment[0].length>0) {
                new IptcReader(iptcSegment[0]).extract(metadata);
            }

            // NOTE: Unable to utilise JpegReader for the SOF0 frame here, as the decodeParam doesn't contain the byte[]

            // similarly, use only the first Jpeg Comment segment
            byte[][] jpegCommentSegment = decodeParam.getMarkerData(JPEGDecodeParam.COMMENT_MARKER);
            if (jpegCommentSegment != null && jpegCommentSegment[0].length>0) {
                new JpegCommentReader(jpegCommentSegment[0]).extract(metadata);
            }

            printImageTags(4, metadata);
        } catch (FileNotFoundException e) {
            System.err.println("error 4a: " + e);
        } catch (IOException e) {
            System.err.println("error 4b: " + e);
        }
*/
    }

    private void printImageTags(int approachCount, Metadata metadata)
    {
        System.out.println();
        System.out.println("*** APPROACH " + approachCount + " ***");
        System.out.println();
        // iterate over the exif data and print to System.out
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags())
                System.out.println(tag);
            for (String error : directory.getErrors())
                System.err.println("ERROR: " + error);
        }
    }

    /**
     * Executes the sample usage program.
     * @param args command line parameters
     */
    public static void main(String[] args)
    {
        new SampleUsage("Source/com/drew/metadata/test/withIptcExifGps.jpg");
    }
}
