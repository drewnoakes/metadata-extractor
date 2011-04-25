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
 * Created by Darren Salomons & Drew Noakes.
 */
package com.drew.imaging;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
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
 */
public class ImageMetadataReader
{
    private static final int JPEG_FILE_MAGIC_NUMBER = 0xFFD8;
    private static final int MOTOROLLA_TIFF_MAGIC_NUMBER = 0x4D4D;  // "MM"
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
    public static Metadata readMetadata(BufferedInputStream inputStream) throws ImageProcessingException
    {
        int magicNumber = readMagicNumber(inputStream);
        return readMetadata(inputStream, null, magicNumber);
    }

    /**
     * Reads metadata from a file.  The file is examined to determine its type and consequently the appropriate
     * method to extract the data, though this is transparent to the caller.
     *
     * @param file a file from which the image data may be read.
     * @return a populated Metadata error containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException for general processing errors.
     */
    public static Metadata readMetadata(File file) throws ImageProcessingException
    {
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ImageProcessingException("File not found: " + file.getPath(), e);
        }

        int magicNumber = readMagicNumber(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new ImageProcessingException("Error closing file: " + file.getPath(), e);
        }

        return readMetadata(null, file, magicNumber);
    }

    private static Metadata readMetadata(BufferedInputStream inputStream, File file, int magicNumber) throws ImageProcessingException
    {
        if ((magicNumber & JPEG_FILE_MAGIC_NUMBER) == JPEG_FILE_MAGIC_NUMBER) {
            if (inputStream != null)
                return JpegMetadataReader.readMetadata(inputStream);
            else
                return JpegMetadataReader.readMetadata(file);
        } else if (magicNumber == INTEL_TIFF_MAGIC_NUMBER || magicNumber == MOTOROLLA_TIFF_MAGIC_NUMBER) {
            if (inputStream != null)
                return TiffMetadataReader.readMetadata(inputStream);
            else
                return TiffMetadataReader.readMetadata(file);
        } else {
            throw new ImageProcessingException("File is not the correct format");
        }
    }

    private static int readMagicNumber(BufferedInputStream inputStream) throws ImageProcessingException
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
    public static void main(String[] args) throws MetadataException, IOException
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
                ExifDirectory directory = (ExifDirectory) metadata.getDirectory(ExifDirectory.class);
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
