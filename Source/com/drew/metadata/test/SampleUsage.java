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
 * Created by dnoakes on 05-Nov-2002 18:57:14 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Shows example usages of the metadata-extractor library.
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
        }

        // Approach 3
        // As fast as approach 1 (this is what goes on inside the JpegMetadataReader's readMetadata() method), this code
        // is handy if you want to look into other Jpeg segments as well.
        try {
            JpegSegmentReader segmentReader = new JpegSegmentReader(file);
            byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
            byte[] iptcSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD);
            Metadata metadata = new Metadata();
            new ExifReader(exifSegment).extract(metadata);
            new IptcReader(iptcSegment).extract(metadata);
            printImageTags(3, metadata);
        } catch (JpegProcessingException e) {
            System.err.println("error 3a: " + e);
        }
        
        // Approach 4
        // This approach is the slowest, because it decodes the Jpeg image.  Of course you now have a decoded image to
        // play with.  In some instances this will be most appropriate.
        try {
            JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(file));
            BufferedImage image = jpegDecoder.decodeAsBufferedImage();
            // now you can use the image
            JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
            Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
            printImageTags(4, metadata);
        } catch (FileNotFoundException e) {
            System.err.println("error 4a: " + e);
        } catch (IOException e) {
            System.err.println("error 4b: " + e);
        }
    }

    private void printImageTags(int approachCount, Metadata metadata)
    {
        System.out.println();
        System.out.println("*** APPROACH " + approachCount + " ***");
        System.out.println();
        // iterate over the exif data and print to System.out
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            Iterator tags = directory.getTagIterator();
            while (tags.hasNext()) {
                Tag tag = (Tag)tags.next();
                System.out.println(tag);
            }
            if (directory.hasErrors()) {
                Iterator errors = directory.getErrors();
                while (errors.hasNext()) {
                    System.out.println("ERROR: " + errors.next());
                }
            }
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
