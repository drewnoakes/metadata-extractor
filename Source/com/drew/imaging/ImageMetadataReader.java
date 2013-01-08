/*
 * Copyright 2002-2013 Drew Noakes
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

import com.drew.imaging.bmp.BmpMetadataReader;
import com.drew.imaging.gif.GifMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.imaging.psd.PsdMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifThumbnailDirectory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Obtains {@link Metadata} from all supported file formats.
 * <p/>
 * This class a lightweight wrapper around specific file type processors:
 * <ul>
 *     <li>{@link JpegMetadataReader} for JPEG files</li>
 *     <li>{@link TiffMetadataReader} for TIFF and (most) RAW files</li>
 *     <li>{@link PsdMetadataReader} for Photoshop files</li>
 *     <li>{@link PngMetadataReader} for BMP files</li>
 *     <li>{@link BmpMetadataReader} for BMP files</li>
 *     <li>{@link GifMetadataReader} for GIF files</li>
 * </ul>
 * If you know the file type you're working with, you may use one of the above processors directly.
 * For most scenarios it is simpler, more convenient and more robust to use this class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ImageMetadataReader
{
    private static final int JPEG_FILE_MAGIC_NUMBER = 0xFFD8;
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D;  // "MM"
    private static final int INTEL_TIFF_MAGIC_NUMBER = 0x4949;     // "II"
    private static final int PSD_MAGIC_NUMBER = 0x3842;            // "8B" // TODO the full magic number is 8BPS
    private static final int PNG_MAGIC_NUMBER = 0x8950;            // "?P" // TODO the full magic number is six bytes long
    private static final int BMP_MAGIC_NUMBER = 0x424D;            // "BM" // TODO technically there are other very rare magic numbers for OS/2 BMP files...
    private static final int GIF_MAGIC_NUMBER = 0x4749;            // "GI" // TODO the full magic number is GIF or possibly GIF89a/GIF87a

    /**
     * Reads metadata from an {@link InputStream}.
     * <p/>
     * The file type is determined by inspecting the leading bytes of the stream, and parsing of the file
     * is delegated to one of:
     * <ul>
     *     <li>{@link JpegMetadataReader} for JPEG files</li>
     *     <li>{@link TiffMetadataReader} for TIFF and (most) RAW files</li>
     *     <li>{@link PsdMetadataReader} for Photoshop files</li>
     *     <li>{@link PngMetadataReader} for PNG files</li>
     *     <li>{@link BmpMetadataReader} for BMP files</li>
     *     <li>{@link GifMetadataReader} for GIF files</li>
     * </ul>
     *
     * @param inputStream a stream from which the file data may be read.  The stream must be positioned at the
     *                    beginning of the file's data.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException if the file type is unknown, or for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws ImageProcessingException, IOException
    {
        InputStream bufferedInputStream = new BufferedInputStream(inputStream);

        int magicNumber = peekMagicNumber(bufferedInputStream);

        if (magicNumber == -1)
            throw new ImageProcessingException("Could not determine file's magic number.");

        // This covers all JPEG files
        if ((magicNumber & JPEG_FILE_MAGIC_NUMBER) == JPEG_FILE_MAGIC_NUMBER) {
            return JpegMetadataReader.readMetadata(bufferedInputStream);
        }

        // This covers all TIFF and camera RAW files
        if (magicNumber == INTEL_TIFF_MAGIC_NUMBER || magicNumber == MOTOROLA_TIFF_MAGIC_NUMBER) {
            return TiffMetadataReader.readMetadata(bufferedInputStream);
        }

        // This covers PSD files
        // TODO we should really check all 4 bytes of the PSD magic number
        if (magicNumber == PSD_MAGIC_NUMBER) {
            return PsdMetadataReader.readMetadata(bufferedInputStream);
        }

        // This covers BMP files
        if (magicNumber == PNG_MAGIC_NUMBER) {
            return PngMetadataReader.readMetadata(bufferedInputStream);
        }

        // This covers BMP files
        if (magicNumber == BMP_MAGIC_NUMBER) {
            return BmpMetadataReader.readMetadata(bufferedInputStream);
        }

        // This covers GIF files
        if (magicNumber == GIF_MAGIC_NUMBER) {
            return GifMetadataReader.readMetadata(bufferedInputStream);
        }

        throw new ImageProcessingException("File format is not supported");
    }

    /**
     * Reads {@link Metadata} from a {@link File} object.
     * <p/>
     * The file type is determined by inspecting the leading bytes of the stream, and parsing of the file
     * is delegated to one of:
     * <ul>
     *     <li>{@link JpegMetadataReader} for JPEG files</li>
     *     <li>{@link TiffMetadataReader} for TIFF and (most) RAW files</li>
     *     <li>{@link PsdMetadataReader} for Photoshop files</li>
     *     <li>{@link PngMetadataReader} for PNG files</li>
     *     <li>{@link BmpMetadataReader} for BMP files</li>
     *     <li>{@link GifMetadataReader} for GIF files</li>
     * </ul>
     *
     * @param file a file from which the image data may be read.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws ImageProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        try {
            return readMetadata(inputStream);
        } finally {
            inputStream.close();
        }
    }

    /**
     * Reads the first two bytes from <code>inputStream</code>, then rewinds.
     */
    private static int peekMagicNumber(@NotNull InputStream inputStream) throws IOException
    {
        inputStream.mark(2);
        final int byte1 = inputStream.read();
        final int byte2 = inputStream.read();
        inputStream.reset();

        if (byte1 == -1 || byte2 == -1)
            return -1;

        return byte1 << 8 | byte2;
    }

    private ImageMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    /**
     * An application entry point.  Takes the name of one or more files as arguments and prints the contents of all
     * metadata directories to <code>System.out</code>.
     * <p/>
     * If <code>-thumb</code> is passed, then any thumbnail data will be written to a file with name of the
     * input file having <code>.thumb.jpg</code> appended.
     * <p/>
     * If <code>-wiki</code> is passed, then output will be in a format suitable for Google Code's wiki.
     * <p/>
     * If <code>-hex</code> is passed, then the ID of each tag will be displayed in hexadecimal.
     *
     * @param args the command line arguments
     */
    public static void main(@NotNull String[] args) throws MetadataException, IOException
    {
        Collection<String> argList = new ArrayList<String>(Arrays.asList(args));
        boolean thumbRequested = argList.remove("-thumb");
        boolean wikiFormat = argList.remove("-wiki");
        boolean showHex = argList.remove("-hex");

        if (argList.size() < 1) {
            System.out.println("Usage: java -jar metadata-extractor-a.b.c.jar <filename> [<filename>] [-thumb] [-wiki] [-hex]");
            System.exit(1);
        }

        for (String filePath : argList) {
            long startTime = System.nanoTime();
            File file = new File(filePath);

            if (!wikiFormat && argList.size()>1)
                System.out.printf("\n***** PROCESSING: %s\n%n", filePath);

            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(file);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            long took = System.nanoTime() - startTime;
            if (!wikiFormat)
                System.out.printf("Processed %.3f MB file in %.2f ms%n%n", file.length() / (1024d * 1024), took / 1000000d);

            if (wikiFormat) {
                String fileName = file.getName();
                String urlName = StringUtil.urlEncode(fileName);
                ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
                String make = exifIFD0Directory == null ? "" : StringUtil.escapeForWiki(exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE));
                String model = exifIFD0Directory == null ? "" : StringUtil.escapeForWiki(exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL));
                System.out.println();
                System.out.println("-----");
                System.out.println();
                System.out.printf("= %s - %s =%n", make, model);
                System.out.println();
                System.out.printf("<a href=\"http://sample-images.metadata-extractor.googlecode.com/git/%s\">%n", urlName);
                System.out.printf("<img src=\"http://sample-images.metadata-extractor.googlecode.com/git/%s\" width=\"300\"/><br/>%n", urlName);
                System.out.println(StringUtil.escapeForWiki(fileName));
                System.out.println("</a>");
                System.out.println();
                System.out.println("|| *Directory* || *Tag Id* || *Tag Name* || *Extracted Value* ||");
            }

            // iterate over the metadata and print to System.out
            for (Directory directory : metadata.getDirectories()) {
                String directoryName = directory.getName();
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    String description = tag.getDescription();

                    // truncate the description if it's too long
                    if (description != null && description.length() > 1024) {
                        description = description.substring(0, 1024) + "...";
                    }

                    if (wikiFormat) {
                        System.out.printf("||%s||0x%s||%s||%s||%n",
                                StringUtil.escapeForWiki(directoryName),
                                Integer.toHexString(tag.getTagType()),
                                StringUtil.escapeForWiki(tagName),
                                StringUtil.escapeForWiki(description));
                    }
                    else
                    {
                        if (showHex) {
                            System.out.printf("[%s - %s] %s = %s%n", directoryName, tag.getTagTypeHex(), tagName, description);
                        } else {
                            System.out.printf("[%s] %s = %s%n", directoryName, tagName, description);
                        }
                    }
                }

                // print out any errors
                for (String error : directory.getErrors())
                    System.err.println("ERROR: " + error);
            }

            if (args.length > 1 && thumbRequested) {
                ExifThumbnailDirectory directory = metadata.getDirectory(ExifThumbnailDirectory.class);
                if (directory!=null && directory.hasThumbnailData()) {
                    System.out.println("Writing thumbnail...");
                    directory.writeThumbnail(args[0].trim() + ".thumb.jpg");
                } else {
                    System.out.println("No thumbnail data exists in this image");
                }
            }
        }
    }
}
