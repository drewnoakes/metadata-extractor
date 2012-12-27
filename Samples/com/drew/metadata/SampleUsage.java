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

import java.io.File;
import java.io.IOException;

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
     *
     * @param fileName path to a file upon which to operate
     */
    public SampleUsage(String fileName)
    {
        File file = new File(fileName);

        // There are multiple ways to get a Metadata object for a file

        // Approach 1
        // This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
        // readers.  In most cases, this is the most appropriate usage.  This will handle Jpeg, TIFF and RAW
        // (CRW/CR2/NEF/RW2) files and extract Exif & IPTC metadata as available.
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
     *
     * @param args command line parameters
     */
    public static void main(String[] args)
    {
        new SampleUsage("Source/com/drew/metadata/test/withIptcExifGps.jpg");
    }
}
