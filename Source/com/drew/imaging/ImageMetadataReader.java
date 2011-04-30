/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.imaging;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Obtains metadata from all supported file formats, including JPEG, RAW (NEF/CRw/CR2) and TIFF.
 * If the caller knows their file to be of a particular type, they may prefer to use the dedicated MetadataReader
 * directly (<code>JpegMetadataReader</code> for Jpeg files, or <code>TiffMetadataReader</code> for TIFF and RAW files).
 * The dedicated readers offer a very slight performance improvement, though for most scenarios it is simpler,
 * more convenient and more robust to use this class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ImageMetadataReader
{
    private static final int JPEG_FILE_MAGIC_NUMBER = 0xFFD8;
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D;  // "MM"
    private static final int INTEL_TIFF_MAGIC_NUMBER = 0x4949;      // "II"

    /**
     * Reads metadata from an input stream.  The file inputStream examined to determine its type and consequently the
     * appropriate method to extract the data, though this inputStream transparent to the caller.
     *
     * @param inputStream a stream from which the image data may be read.  The stream must be positioned at the
     *                    beginning of the image data.
     * @return a populated Metadata error containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull BufferedInputStream inputStream, boolean waitForBytes) throws ImageProcessingException, IOException
    {
        int magicNumber = readMagicNumber(inputStream);
        return readMetadata(inputStream, null, magicNumber, waitForBytes);
    }

    /**
     * Reads metadata from a file.  The file is examined to determine its type and consequently the appropriate
     * method to extract the data, though this is transparent to the caller.
     *
     * @param file a file from which the image data may be read.
     * @return a populated Metadata error containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws ImageProcessingException, IOException
    {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        int magicNumber = readMagicNumber(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new ImageProcessingException("Error closing file: " + file.getPath(), e);
        }

        return readMetadata(null, file, magicNumber, false);
    }

    @NotNull
    private static Metadata readMetadata(@Nullable BufferedInputStream inputStream, @Nullable File file, int magicNumber, boolean waitForBytes) throws ImageProcessingException, IOException
    {
        if ((magicNumber & JPEG_FILE_MAGIC_NUMBER) == JPEG_FILE_MAGIC_NUMBER) {
            if (inputStream != null)
                return JpegMetadataReader.readMetadata(inputStream, waitForBytes);
            else
                return JpegMetadataReader.readMetadata(file);
        }

        if (magicNumber == INTEL_TIFF_MAGIC_NUMBER || magicNumber == MOTOROLA_TIFF_MAGIC_NUMBER) {
            if (inputStream != null)
                return TiffMetadataReader.readMetadata(inputStream, waitForBytes);
            else
                return TiffMetadataReader.readMetadata(file);
        }

        throw new ImageProcessingException("File is not the correct format");
    }

    private static int readMagicNumber(@NotNull BufferedInputStream inputStream) throws ImageProcessingException
    {
        int magicNumber;
        try {
            inputStream.mark(2);
            magicNumber = inputStream.read() << 8;
            magicNumber |= inputStream.read();
            inputStream.reset();
        } catch (IOException e) {
            throw new ImageProcessingException("Error reading image data", e);
        }
        return magicNumber;
    }

    private ImageMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    /**
     * An application entry point.  Takes the name of one or more files as arguments and prints the contents of all
     * metadata directories to System.out.  If <code>/thumb</code> is passed, then any thumbnail data will be
     * written to a file with name of the input file having '.thumb.jpg' appended.
     *
     * @param args the command line arguments
     */
    public static void main(@NotNull String[] args) throws MetadataException, IOException
    {
        if (args.length < 1) {
            System.out.println("Usage: java -jar metadata-extractor-a.b.c.jar <filename> [<filename>] [/thumb]");
            System.exit(1);
        }

        List<String> argList = Arrays.asList(args);
        boolean thumbRequested = argList.remove("/thumb");

        for (String file : argList) {
            if (argList.size()>1)
                System.out.println("***** PROCESSING: " + file);

            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(new File(file));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }

            // iterate over the exif data and print to System.out
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    try {
                        System.out.println("[" + directory.getName() + "] " + tag.getTagName() + " = " + tag.getDescription());
                    } catch (MetadataException e) {
                        System.err.println(e.getMessage());
                        System.err.println(tag.getDirectoryName() + " " + tag.getTagName() + " (error)");
                    }
                }

                // print out any errors
                for (String error : directory.getErrors())
                    System.err.println("ERROR: " + error);
            }

            if (args.length > 1 && thumbRequested) {
                ExifDirectory directory = metadata.getOrCreateDirectory(ExifDirectory.class);
                if (directory.containsThumbnail()) {
                    System.out.println("Writing thumbnail...");
                    directory.writeThumbnail(args[0].trim() + ".thumb.jpg");
                } else {
                    System.out.println("No thumbnail data exists in this image");
                }
            }
        }
    }
}
