/*
 * Created by dnoakes on 05-Nov-2002 18:57:14 using IntelliJ IDEA.
 */
package com.drew.metadata;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
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
 *
 */
public class SampleUsage
{
    /**
     * Constructor which executes multiple sample usages, each of which return the same output.  This class showcases
     * multiple usages of this metadata class library.
     * @param fileName path to a jpeg file upon which to operate
     */
    public SampleUsage(String fileName)
    {
        File jpegFile = new File(fileName);

        // There are multiple ways to get a Metadata object

        // Approach 1
        // This approach reads all types of known Jpeg metadata (at present,
        // Exif and Iptc) in a single call.  In most cases, this is the most
        // appropriate usage.
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
            printImageTags(1, metadata);
        } catch (JpegProcessingException e) {
            System.err.println("error 1a");
        }

        // Approach 2
        // This approach shows using individual MetadataReader implementations
        // to read a file.  This is less efficient than approach 1, as the file
        // is opened and read twice.
        try {
            Metadata metadata = new Metadata();
            new ExifReader(jpegFile).extract(metadata);
            new IptcReader(jpegFile).extract(metadata);
            printImageTags(2, metadata);
        } catch (JpegProcessingException jpe) {
            System.err.println("error 2a");
        } catch (FileNotFoundException e) {
            System.err.println("error 2b");
        }

        // Approach 3
        // As fast as approach 1 (this is what goes on inside the JpegMetadataReader's
        // readMetadata() method), this code is handy if you want to look into other
        // Jpeg segments too.
        try {
            JpegSegmentReader segmentReader = new JpegSegmentReader(jpegFile);
            byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
            byte[] iptcSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD);
            Metadata metadata = new Metadata();
            new ExifReader(exifSegment).extract(metadata);
            new IptcReader(iptcSegment).extract(metadata);
            printImageTags(3, metadata);
        } catch (JpegProcessingException jpe) {
            System.err.println("error 3a");
        }
        
        // Approach 4
        // This approach is the slowest, because it decodes the Jpeg image.  Of
        // course you now have a decoded image to play with.  In some instances
        // this will be most appropriate.
        try {
            JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(jpegFile));
            BufferedImage image = jpegDecoder.decodeAsBufferedImage();
            // now you can use the image
            JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
            Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
            printImageTags(4, metadata);
        } catch (FileNotFoundException e) {
            System.err.println("error 4a");
        } catch (IOException e) {
            System.err.println("error 4b");
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
        new SampleUsage("src/com/drew/metadata/test/withIptcExifGps.jpg");
    }
}
