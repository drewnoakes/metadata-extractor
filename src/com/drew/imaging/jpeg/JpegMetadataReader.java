/*
 * Created by dnoakes on 12-Nov-2002 18:51:36 using IntelliJ IDEA.
 */
package com.drew.imaging.jpeg;

import com.drew.metadata.*;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.sun.image.codec.jpeg.JPEGDecodeParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 *
 */
public class JpegMetadataReader
{
    public static Metadata readMetadata(File file) throws FileNotFoundException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(file);
        Metadata metadata = new Metadata();
        try {
            byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
            new ExifReader(exifSegment).extract(metadata);
        } catch (Exception e) {
            // in the interests of catching as much data as possible, continue
            // TODO lodge error message within exif directory
        }
        try {
            byte[] iptcSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD);
            new IptcReader(iptcSegment).extract(metadata);
        } catch (Exception e) {
            // in the interests of catching as much data as possible, continue
            // TODO lodge error message within iptc directory
        }
        return metadata;
    }

    public static Metadata readMetadata(JPEGDecodeParam decodeParam)
    {
        Metadata metadata = new Metadata();

        /* We should only really be seeing Exif in _data[0]... the 2D array exists
         * because markers can theoretically appear multiple times in the file.
         */
        try {
            byte[][] exifSegment = decodeParam.getMarkerData(JPEGDecodeParam.APP1_MARKER);
            if (exifSegment != null) {
                new ExifReader(exifSegment[0]).extract();
            }
        } catch (MetadataException e) {
            // continue
        }
        // similarly, use only the first IPTC segment
        try {
            byte[][] iptcSegment = decodeParam.getMarkerData(JPEGDecodeParam.APPD_MARKER);
            if (iptcSegment != null) {
                new IptcReader(iptcSegment[0]).extract();
            }
        } catch (MetadataException e) {
            // continue
        }
        return metadata;
    }

    private JpegMetadataReader()
    {
    }

    public static void main(String[] args)
    {
        JpegMetadataReader jpegReader = new JpegMetadataReader();
        Metadata metadata = null;
        try {
            metadata = jpegReader.readMetadata(new File(args[0]));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        // iterate over the exif data and print to System.out
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            Iterator tags = directory.getTagIterator();
            while (tags.hasNext()) {
                Tag tag = (Tag)tags.next();
                try {
                    System.out.println("[" + directory.getName() + "] " + tag.getTagName() + " = " + tag.getDescription());
                } catch (MetadataException e) {
                    System.err.println(e.getMessage());
                    System.err.println(tag.getDirectoryName() + " " + tag.getTagName() + " <error>");
                }
            }
        }
    }
}
