/*
 * Copyright 2002-2014 Drew Noakes
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

package com.drew.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ProcessAllImagesInFolderUtility
{
    public static void main(final String[] args) throws IOException, JpegProcessingException
    {
        if (args.length == 0) {
            System.err.println("Expects one or more directories as arguments.");
            System.exit(1);
        }

        final List<String> directories = new ArrayList<String>();

        FileHandler handler = null;

        for (final String arg : args) {
            if (arg.equalsIgnoreCase("-text")) {
                // If "-test" is specified, write the discovered metadata into a sub-folder relative to the image
                handler = new TextFileOutputHandler();
            } else if (arg.equalsIgnoreCase("-wiki")) {
                handler = new WikiTableOutputHandler();
            } else {
                directories.add(arg);
            }
        }

        if (handler == null) {
            handler = new BasicFileHandler();
        }

        final long start = System.nanoTime();

        // Order alphabetically so that output is stable across invocations
        Collections.sort(directories);

        for (final String directory : directories) {
            processDirectory(new File(directory), handler);
        }

        handler.onCompleted();

        System.out.println(String.format("Completed in %d ms", (System.nanoTime() - start) / 1000000));
    }

    private static void processDirectory(@NotNull final File path, @NotNull final FileHandler handler)
    {
        final String[] pathItems = path.list();

        if (pathItems == null) {
            return;
        }

        // Order alphabetically so that output is stable across invocations
        Arrays.sort(pathItems);

        for (final String pathItem : pathItems) {
            final File file = new File(path, pathItem);

            if (file.isDirectory()) {
                processDirectory(file, handler);
            } else if (handler.shouldProcess(file)) {

                handler.onProcessingStarting(file);

                // Read metadata
                final Metadata metadata;
                try {
                    metadata = ImageMetadataReader.readMetadata(file);
                } catch (final Throwable t) {
                    handler.onException(file, t);
                    continue;
                }

                handler.onExtracted(file, metadata);
            }
        }
    }

    interface FileHandler
    {
        boolean shouldProcess(@NotNull File file);
        void onException(@NotNull File file, @NotNull Throwable throwable);
        void onExtracted(@NotNull File file, @NotNull Metadata metadata);
        void onCompleted();

        void onProcessingStarting(@NotNull File file);
    }

    abstract static class FileHandlerBase implements FileHandler
    {
        private final Set<String> _supportedExtensions = new HashSet<String>(
            Arrays.asList("jpg", "jpeg", "nef", "crw", "cr2", "orf", "tif", "tiff", "png", "gif", "bmp"));

        private int _processedFileCount = 0;
        private int _exceptionCount = 0;
        private int _errorCount = 0;
        private long _processedByteCount = 0;

        public boolean shouldProcess(@NotNull final File file)
        {
            final String extension = getExtension(file);
            return extension != null && _supportedExtensions.contains(extension.toLowerCase());
        }

        public void onProcessingStarting(@NotNull final File file)
        {
            _processedFileCount++;
            _processedByteCount += file.length();
        }

        public void onException(@NotNull final File file, @NotNull final Throwable throwable)
        {
            _exceptionCount++;

            if (throwable instanceof ImageProcessingException) {
                // this is an error in the Jpeg segment structure.  we're looking for bad handling of
                // metadata segments.  in this case, we didn't even get a segment.
                System.err.printf("%s: %s [Error Extracting Metadata]\n\t%s%n", throwable.getClass().getName(), file, throwable.getMessage());
            } else {
                // general, uncaught exception during processing of jpeg segments
                System.err.printf("%s: %s [Error Extracting Metadata]%n", throwable.getClass().getName(), file);
                throwable.printStackTrace(System.err);
            }
        }

        public void onExtracted(@NotNull final File file, @NotNull final Metadata metadata)
        {
            if (metadata.hasErrors()) {
                System.err.println(file);
                for (final Directory directory : metadata.getDirectories()) {
                    if (!directory.hasErrors())
                        continue;
                    for (final String error : directory.getErrors()) {
                        System.err.printf("\t[%s] %s%n", directory.getName(), error);
                        _errorCount++;
                    }
                }
            }
        }

        public void onCompleted()
        {
            if (_processedFileCount > 0) {
                System.out.println(String.format(
                    "Processed %,d files (%,d bytes) with %,d exceptions and %,d file errors",
                    _processedFileCount, _processedByteCount, _exceptionCount, _errorCount
                ));
            }
        }

        @Nullable
        protected String getExtension(@NotNull final File file)
        {
            final String fileName = file.getName();
            final int i = fileName.lastIndexOf('.');
            if (i == -1)
                return null;
            if (i == fileName.length() - 1)
                return null;
            return fileName.substring(i + 1);
        }
    }

    /**
     * Writes a text file containing the extracted metadata for each input file.
     */
    static class TextFileOutputHandler extends FileHandlerBase
    {
        @Override
        public void onExtracted(@NotNull final File file, @NotNull final Metadata metadata)
        {
            super.onExtracted(file, metadata);

            try {
                writeOutputFile(file, metadata);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

		private static void writeOutputFile(final File file, final Metadata metadata) throws IOException
        {
            FileWriter writer = null;
            try
            {
                final String outputPath = String.format("%s/metadata/%s.txt", file.getParent(), file.getName()).toLowerCase();
                writer = new FileWriter(outputPath, false);
                writer.write("FILE: " + file.getName() + "\n");
                writer.write("\n");

                if (metadata.hasErrors()) {
                    for (final Directory directory : metadata.getDirectories()) {
                        if (!directory.hasErrors())
                            continue;
                        for (final String error : directory.getErrors()) {
                            writer.write(String.format("[ERROR: %s] %s\n", directory.getName(), error));
                        }
                    }
                    writer.write("\n");
                }

                // Iterate through all values
                for (final Directory directory : metadata.getDirectories()) {
                    final String directoryName = directory.getName();
                    for (final Tag tag : directory.getTags()) {
                        final String tagName = tag.getTagName();
                        final String description = tag.getDescription();
                        writer.write(String.format("[%s - %s] %s = %s%n", directoryName, tag.getTagTypeHex(), tagName, description));
                    }
                    if (directory.getTagCount() != 0) {
                        writer.write("\n");
                    }
                }
            } finally {
                if (writer != null) {
                    writer.write("Generated using metadata-extractor\n");
                    writer.write("https://drewnoakes.com/code/exif/\n");
                    writer.flush();
                    writer.close();
                }
            }
        }
    }

    /**
     * Creates a table describing sample images using Wiki markdown.
     */
    static class WikiTableOutputHandler extends FileHandlerBase
    {
        private final Map<String, String> _extensionEquivalence = new HashMap<String, String>();
        private final Map<String, List<Row>> _rowListByExtension = new HashMap<String, List<Row>>();

        class Row
        {
            File file;
            Metadata metadata;
            @Nullable private String manufacturer;
            @Nullable private String model;
            @Nullable private String exifVersion;
            @Nullable private String thumbnail;
            @Nullable private String makernote;

            Row(@NotNull final File file, @NotNull final Metadata metadata)
            {
                this.file = file;
                this.metadata = metadata;

                final ExifIFD0Directory ifd0Dir = metadata.getDirectory(ExifIFD0Directory.class);
                final ExifSubIFDDirectory subIfdDir = metadata.getDirectory(ExifSubIFDDirectory.class);
                final ExifThumbnailDirectory thumbDir = metadata.getDirectory(ExifThumbnailDirectory.class);
                if (ifd0Dir != null) {
                    manufacturer = ifd0Dir.getDescription(ExifIFD0Directory.TAG_MAKE);
                    model = ifd0Dir.getDescription(ExifIFD0Directory.TAG_MODEL);
                }
                boolean hasMakernoteData = false;
                if (subIfdDir != null) {
                    exifVersion = subIfdDir.getDescription(ExifSubIFDDirectory.TAG_EXIF_VERSION);
                    hasMakernoteData = subIfdDir.containsTag(ExifSubIFDDirectory.TAG_MAKERNOTE);
                }
                if (thumbDir != null) {
                    final Integer width = thumbDir.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_IMAGE_WIDTH);
                    final Integer height = thumbDir.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT);
                    thumbnail = width != null && height != null
                        ? String.format("Yes (%s x %s)", width, height)
                        : "Yes";
                }
                for (final Directory directory : metadata.getDirectories()) {
                    if (directory.getClass().getName().contains("Makernote")) {
                        makernote = directory.getName().replace("Makernote", "").trim();
                    }
                }
                if (makernote == null) {
                    makernote = hasMakernoteData ? "(Unknown)" : "N/A";
                }
            }
        }

        public WikiTableOutputHandler()
        {
            _extensionEquivalence.put("jpeg", "jpg");
        }

        @Override
        public void onExtracted(@NotNull final File file, @NotNull final Metadata metadata)
        {
            super.onExtracted(file, metadata);

            String extension = getExtension(file);

            if (extension == null) {
                return;
            }

            // Sanitise the extension
            extension = extension.toLowerCase();
            if (_extensionEquivalence.containsKey(extension))
                extension =_extensionEquivalence.get(extension);

            List<Row> list = _rowListByExtension.get(extension);
            if (list == null) {
                list = new ArrayList<Row>();
                _rowListByExtension.put(extension, list);
            }
            list.add(new Row(file, metadata));
        }

        @Override
        public void onCompleted()
        {
            super.onCompleted();

            OutputStream outputStream = null;
            PrintStream stream = null;
            try {
                outputStream = new FileOutputStream("../wiki/ImageDatabaseSummary.wiki", false);
                stream = new PrintStream(outputStream, false);
                writeOutput(stream);
                stream.flush();
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null)
                    stream.close();
                if (outputStream != null)
                    try {
                        outputStream.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        private void writeOutput(@NotNull final PrintStream stream) throws IOException
        {
            final Writer writer = new OutputStreamWriter(stream);
            writer.write("#summary Tabular summary of metadata found in the image database\n\n");
            writer.write("= Image Database Summary =\n\n");

            for (final String extension : _rowListByExtension.keySet()) {
                writer.write("== " + extension.toUpperCase() + " Files ==\n\n");

                writer.write("|| *File* || *Manufacturer* || *Model* || *Dir Count* || *Exif?* || *Makernote*  || *Thumbnail* || *All Data* ||\n");
                final List<Row> rows = _rowListByExtension.get(extension);

                // Order by manufacturer, then model
                Collections.sort(rows, new Comparator<Row>() {
                    public int compare(final Row o1, final Row o2)
                    {
                        final int c1 = StringUtil.compare(o1.manufacturer, o2.manufacturer);
                        return c1 != 0 ? c1 : StringUtil.compare(o1.model, o2.model);
                    }
                });

                for (final Row row : rows) {
                    writer.write(String.format("|| [http://sample-images.metadata-extractor.googlecode.com/git/%s %s] || %s || %s || %d || %s ||  %s || %s || [http://sample-images.metadata-extractor.googlecode.com/git/metadata/%s.txt metadata] ||%n",
                        StringUtil.urlEncode(row.file.getName()),
                        row.file.getName(),
                        row.manufacturer == null ? "" : StringUtil.escapeForWiki(row.manufacturer),
                        row.model == null ? "" : StringUtil.escapeForWiki(row.model),
                        row.metadata.getDirectoryCount(),
                        row.exifVersion == null ? "" : row.exifVersion,
                        row.makernote == null ? "" : row.makernote,
                        row.thumbnail == null ? "" : row.thumbnail,
                        StringUtil.urlEncode(row.file.getName()).toLowerCase()
                    ));
                }

                writer.write('\n');
            }
            writer.flush();
        }
    }

    /**
     * Does nothing with the output except enumerate it in memory and format descriptions. This is useful in order to
     * flush out any potential exceptions raised during the formatting of extracted value descriptions.
     */
    static class BasicFileHandler extends FileHandlerBase
    {
        @Override
        public void onExtracted(@NotNull final File file, @NotNull final Metadata metadata)
        {
            super.onExtracted(file, metadata);

            // Iterate through all values, calling toString to flush out any formatting exceptions
            for (final Directory directory : metadata.getDirectories()) {
                directory.getName();
                for (final Tag tag : directory.getTags()) {
                    tag.getTagName();
                    tag.getDescription();
                }
            }
        }
    }
}
