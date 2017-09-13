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
package com.drew.imaging;

import com.drew.imaging.avi.AviMetadataReader;
import com.drew.imaging.bmp.BmpMetadataReader;
import com.drew.imaging.eps.EpsMetadataReader;
import com.drew.imaging.gif.GifMetadataReader;
import com.drew.imaging.ico.IcoMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.imaging.quicktime.QuickTimeMetadataReader;
import com.drew.imaging.pcx.PcxMetadataReader;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.imaging.psd.PsdMetadataReader;
import com.drew.imaging.raf.RafMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.imaging.wav.WavMetadataReader;
import com.drew.imaging.webp.WebpMetadataReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.file.FileTypeDirectory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Reads metadata from any supported file format.
 * <p>
 * This class a lightweight wrapper around other, specific metadata processors.
 * During extraction, the file type is determined from the first few bytes of the file.
 * Parsing is then delegated to one of:
 *
 * <ul>
 *     <li>{@link AviMetadataReader} for AVI files</li>
 *     <li>{@link BmpMetadataReader} for BMP files</li>
 *     <li>{@link FileSystemMetadataReader} for metadata from the file system when a {@link File} is provided</li>
 *     <li>{@link GifMetadataReader} for GIF files</li>
 *     <li>{@link IcoMetadataReader} for ICO files</li>
 *     <li>{@link JpegMetadataReader} for JPEG files</li>
 *     <li>{@link Mp4MetadataReader} for MPEG-4 files</li>
 *     <li>{@link PcxMetadataReader} for PCX files</li>
 *     <li>{@link PngMetadataReader} for PNG files</li>
 *     <li>{@link PsdMetadataReader} for Photoshop files</li>
 *     <li>{@link QuickTimeMetadataReader} for QuickTime files</li>
 *     <li>{@link RafMetadataReader} for RAF files</li>
 *     <li>{@link TiffMetadataReader} for TIFF and (most) RAW files</li>
 *     <li>{@link WavMetadataReader} for WAV files</li>
 *     <li>{@link WebpMetadataReader} for WebP files</li>
 * </ul>
 *
 * If you know the file type you're working with, you may use one of the above processors directly.
 * For most scenarios it is simpler, more convenient and more robust to use this class.
 * <p>
 * {@link FileTypeDetector} is used to determine the provided image's file type, and therefore
 * the appropriate metadata reader to use.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ImageMetadataReader
{
    /**
     * Reads metadata from an {@link InputStream}.
     *
     * @param inputStream a stream from which the file data may be read.  The stream must be positioned at the
     *                    beginning of the file's data.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException if the file type is unknown, or for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull final InputStream inputStream) throws ImageProcessingException, IOException
    {
        return readMetadata(inputStream, -1);
    }

    /**
     * Reads metadata from an {@link InputStream} of known length.
     *
     * @param inputStream a stream from which the file data may be read.  The stream must be positioned at the
     *                    beginning of the file's data.
     * @param streamLength the length of the stream, if known, otherwise -1.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException if the file type is unknown, or for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull final InputStream inputStream, final long streamLength) throws ImageProcessingException, IOException
    {
        BufferedInputStream bufferedInputStream = inputStream instanceof BufferedInputStream
            ? (BufferedInputStream)inputStream
            : new BufferedInputStream(inputStream);

        FileType fileType = FileTypeDetector.detectFileType(bufferedInputStream);

        Metadata metadata = readMetadata(bufferedInputStream, streamLength, fileType);

        metadata.addDirectory(new FileTypeDirectory(fileType));

        return metadata;
    }

    /**
     * Reads metadata from an {@link InputStream} of known length and file type.
     *
     * @param inputStream a stream from which the file data may be read.  The stream must be positioned at the
     *                    beginning of the file's data.
     * @param streamLength the length of the stream, if known, otherwise -1.
     * @param fileType the file type of the data stream.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException if the file type is unknown, or for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull final InputStream inputStream, final long streamLength, final FileType fileType) throws IOException, ImageProcessingException
    {
        switch (fileType) {
            case Jpeg:
                return JpegMetadataReader.readMetadata(inputStream);
            case Tiff:
            case Arw:
            case Cr2:
            case Nef:
            case Orf:
            case Rw2:
                return TiffMetadataReader.readMetadata(new RandomAccessStreamReader(inputStream, RandomAccessStreamReader.DEFAULT_CHUNK_LENGTH, streamLength));
            case Psd:
                return PsdMetadataReader.readMetadata(inputStream);
            case Png:
                return PngMetadataReader.readMetadata(inputStream);
            case Bmp:
                return BmpMetadataReader.readMetadata(inputStream);
            case Gif:
                return GifMetadataReader.readMetadata(inputStream);
            case Ico:
                return IcoMetadataReader.readMetadata(inputStream);
            case Pcx:
                return PcxMetadataReader.readMetadata(inputStream);
            case WebP:
                return WebpMetadataReader.readMetadata(inputStream);
            case Raf:
                return RafMetadataReader.readMetadata(inputStream);
            case Avi:
                return AviMetadataReader.readMetadata(inputStream);
            case Wav:
                return WavMetadataReader.readMetadata(inputStream);
            case Mov:
                return QuickTimeMetadataReader.readMetadata(inputStream);
            case Mp4:
                return Mp4MetadataReader.readMetadata(inputStream);
            case Eps:
                return EpsMetadataReader.readMetadata(inputStream);
            case Unknown:
                throw new ImageProcessingException("File format could not be determined");
            default:
                return new Metadata();
        }
    }

    /**
     * Reads {@link Metadata} from a {@link File} object.
     *
     * @param file a file from which the image data may be read.
     * @return a populated {@link Metadata} object containing directories of tags with values and any processing errors.
     * @throws ImageProcessingException for general processing errors.
     */
    @NotNull
    public static Metadata readMetadata(@NotNull final File file) throws ImageProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream, file.length());
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    private ImageMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    /**
     * An application entry point.  Takes the name of one or more files as arguments and prints the contents of all
     * metadata directories to <code>System.out</code>.
     * <p>
     * If <code>-thumb</code> is passed, then any thumbnail data will be written to a file with name of the
     * input file having <code>.thumb.jpg</code> appended.
     * <p>
     * If <code>-markdown</code> is passed, then output will be in markdown format.
     * <p>
     * If <code>-hex</code> is passed, then the ID of each tag will be displayed in hexadecimal.
     *
     * @param args the command line arguments
     */
    public static void main(@NotNull String[] args) throws MetadataException, IOException
    {
        Collection<String> argList = new ArrayList<String>(Arrays.asList(args));
        boolean markdownFormat = argList.remove("-markdown");
        boolean showHex = argList.remove("-hex");

        if (argList.size() < 1) {
            String version = ImageMetadataReader.class.getPackage().getImplementationVersion();
            System.out.println("metadata-extractor version " + version);
            System.out.println();
            System.out.println(String.format("Usage: java -jar metadata-extractor-%s.jar <filename> [<filename>] [-thumb] [-markdown] [-hex]", version == null ? "a.b.c" : version));
            System.exit(1);
        }

        for (String filePath : argList) {
            long startTime = System.nanoTime();
            File file = new File(filePath);

            if (!markdownFormat && argList.size()>1)
                System.out.printf("\n***** PROCESSING: %s%n%n", filePath);

            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(file);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            long took = System.nanoTime() - startTime;
            if (!markdownFormat)
                System.out.printf("Processed %.3f MB file in %.2f ms%n%n", file.length() / (1024d * 1024), took / 1000000d);

            if (markdownFormat) {
                String fileName = file.getName();
                String urlName = StringUtil.urlEncode(filePath);
                ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                String make = exifIFD0Directory == null ? "" : exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE);
                String model = exifIFD0Directory == null ? "" : exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL);
                System.out.println();
                System.out.println("---");
                System.out.println();
                System.out.printf("# %s - %s%n", make, model);
                System.out.println();
                System.out.printf("<a href=\"https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s\">%n", urlName);
                System.out.printf("<img src=\"https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s\" width=\"300\"/><br/>%n", urlName);
                System.out.println(fileName);
                System.out.println("</a>");
                System.out.println();
                System.out.println("Directory | Tag Id | Tag Name | Extracted Value");
                System.out.println(":--------:|-------:|----------|----------------");
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

                    if (markdownFormat) {
                        System.out.printf("%s|0x%s|%s|%s%n",
                                directoryName,
                                Integer.toHexString(tag.getTagType()),
                                tagName,
                                description);
                    } else {
                        // simple formatting
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
        }
    }
}
