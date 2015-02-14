/*
 * Copyright 2002-2015 Drew Noakes
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

import java.io.*;
import java.util.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class ProcessAllImagesInFolderUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length == 0) {
            System.err.println("Expects one or more directories as arguments.");
            System.exit(1);
        }

        List<String> directories = new ArrayList<String>();

        FileHandler handler = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-text")) {
                // If "-text" is specified, write the discovered metadata into a sub-folder relative to the image
                handler = new TextFileOutputHandler();
            } else if (arg.equalsIgnoreCase("-markdown")) {
                // If "-markdown" is specified, write a summary table in markdown format to standard out
                handler = new MarkdownTableOutputHandler();
            } else if (arg.equalsIgnoreCase("-unknown")) {
                // If "-unknown" is specified, write CSV tallying unknown tag counts
                handler = new UnknownTagHandler();
            } else {
                // Treat this argument as a directory
                directories.add(arg);
            }
        }

        if (handler == null) {
            handler = new BasicFileHandler();
        }

        long start = System.nanoTime();

        // Order alphabetically so that output is stable across invocations
        Collections.sort(directories);

        for (String directory : directories) {
            processDirectory(new File(directory), handler, "");
        }

        handler.onCompleted();

        System.out.println(String.format("Completed in %d ms", (System.nanoTime() - start) / 1000000));
    }

    private static void processDirectory(@NotNull File path, @NotNull FileHandler handler, @NotNull String relativePath)
    {
        String[] pathItems = path.list();

        if (pathItems == null) {
            return;
        }

        // Order alphabetically so that output is stable across invocations
        Arrays.sort(pathItems);

        for (String pathItem : pathItems) {
            File file = new File(path, pathItem);

            if (file.isDirectory()) {
                processDirectory(file, handler, relativePath.length() == 0 ? pathItem : relativePath + "/" + pathItem);
            } else if (handler.shouldProcess(file)) {

                handler.onProcessingStarting(file);

                // Read metadata
                final Metadata metadata;
                try {
                    metadata = ImageMetadataReader.readMetadata(file);
                } catch (Throwable t) {
                    handler.onException(file, t);
                    continue;
                }

                handler.onExtracted(file, metadata, relativePath);
            }
        }
    }

    interface FileHandler
    {
        boolean shouldProcess(@NotNull File file);
        void onException(@NotNull File file, @NotNull Throwable throwable);
        void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath);
        void onCompleted();

        void onProcessingStarting(@NotNull File file);
    }

    abstract static class FileHandlerBase implements FileHandler
    {
        private final Set<String> _supportedExtensions = new HashSet<String>(
            Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp", "ico", "webp", "pcx", "ai", "eps",
                "nef", "crw", "cr2", "orf", "arw", "raf", "srw", "x3f", "rw2", "rwl",
                "tif", "tiff", "psd", "dng"));

        private int _processedFileCount = 0;
        private int _exceptionCount = 0;
        private int _errorCount = 0;
        private long _processedByteCount = 0;

        public boolean shouldProcess(@NotNull File file)
        {
            String extension = getExtension(file);
            return extension != null && _supportedExtensions.contains(extension.toLowerCase());
        }

        public void onProcessingStarting(@NotNull File file)
        {
            _processedFileCount++;
            _processedByteCount += file.length();
        }

        public void onException(@NotNull File file, @NotNull Throwable throwable)
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

        public void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
        {
            if (metadata.hasErrors()) {
                System.err.println(file);
                for (Directory directory : metadata.getDirectories()) {
                    if (!directory.hasErrors())
                        continue;
                    for (String error : directory.getErrors()) {
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
        protected String getExtension(@NotNull File file)
        {
            String fileName = file.getName();
            int i = fileName.lastIndexOf('.');
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
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
        {
            super.onExtracted(file, metadata, relativePath);

            try {
                PrintWriter writer = null;
                try
                {
                    writer = openWriter(file);

                    // Build a list of all directories
                    List<Directory> directories = new ArrayList<Directory>();
                    for (Directory directory : metadata.getDirectories())
                        directories.add(directory);

                    // Sort them by name
                    Collections.sort(directories, new Comparator<Directory>()
                    {
                        public int compare(Directory o1, Directory o2)
                        {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    // Write any errors
                    if (metadata.hasErrors()) {
                        for (Directory directory : directories) {
                            if (!directory.hasErrors())
                                continue;
                            for (String error : directory.getErrors())
                                writer.format("[ERROR: %s] %s\n", directory.getName(), error);
                        }
                        writer.write("\n");
                    }

                    // Write tag values for each directory
                    for (Directory directory : directories) {
                        String directoryName = directory.getName();
                        for (Tag tag : directory.getTags()) {
                            String tagName = tag.getTagName();
                            String description = tag.getDescription();
                            writer.format("[%s - %s] %s = %s%n", directoryName, tag.getTagTypeHex(), tagName, description);
                        }
                        if (directory.getTagCount() != 0)
                            writer.write('\n');
                    }
                } finally {
                    closeWriter(writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onException(@NotNull File file, @NotNull Throwable throwable)
        {
            super.onException(file, throwable);

            try {
                PrintWriter writer = null;
                try {
                    writer = openWriter(file);
                    throwable.printStackTrace(writer);
                    writer.write('\n');
                } finally {
                    closeWriter(writer);
                }
            } catch (IOException e) {
                System.err.printf("IO exception writing metadata file: %s%n", e.getMessage());
            }
        }

        @NotNull
        private static PrintWriter openWriter(@NotNull File file) throws IOException
        {
            // Create the output directory if it doesn't exist
            File metadataDir = new File(String.format("%s/metadata", file.getParent()));
            if (!metadataDir.exists())
                metadataDir.mkdir();

            String outputPath = String.format("%s/metadata/%s.txt", file.getParent(), file.getName().toLowerCase());
            FileWriter writer = new FileWriter(outputPath, false);
            writer.write("FILE: " + file.getName() + "\n");
            writer.write('\n');

            return new PrintWriter(writer);
        }

        private static void closeWriter(@Nullable Writer writer) throws IOException
        {
            if (writer != null) {
                writer.write("Generated using metadata-extractor\n");
                writer.write("https://drewnoakes.com/code/exif/\n");
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * Creates a table describing sample images using Wiki markdown.
     */
    static class MarkdownTableOutputHandler extends FileHandlerBase
    {
        private final Map<String, String> _extensionEquivalence = new HashMap<String, String>();
        private final Map<String, List<Row>> _rowListByExtension = new HashMap<String, List<Row>>();

        class Row
        {
            final File file;
            final Metadata metadata;
            @NotNull final String relativePath;
            @Nullable private String manufacturer;
            @Nullable private String model;
            @Nullable private String exifVersion;
            @Nullable private String thumbnail;
            @Nullable private String makernote;

            Row(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
            {
                this.file = file;
                this.metadata = metadata;
                this.relativePath = relativePath;

                ExifIFD0Directory ifd0Dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                ExifSubIFDDirectory subIfdDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                ExifThumbnailDirectory thumbDir = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
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
                    Integer width = thumbDir.getInteger(ExifThumbnailDirectory.TAG_IMAGE_WIDTH);
                    Integer height = thumbDir.getInteger(ExifThumbnailDirectory.TAG_IMAGE_HEIGHT);
                    thumbnail = width != null && height != null
                        ? String.format("Yes (%s x %s)", width, height)
                        : "Yes";
                }
                for (Directory directory : metadata.getDirectories()) {
                    if (directory.getClass().getName().contains("Makernote")) {
                        makernote = directory.getName().replace("Makernote", "").trim();
                    }
                }
                if (makernote == null) {
                    makernote = hasMakernoteData ? "(Unknown)" : "N/A";
                }
            }
        }

        public MarkdownTableOutputHandler()
        {
            _extensionEquivalence.put("jpeg", "jpg");
        }

        @Override
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
        {
            super.onExtracted(file, metadata, relativePath);

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
            list.add(new Row(file, metadata, relativePath));
        }

        @Override
        public void onCompleted()
        {
            super.onCompleted();

            OutputStream outputStream = null;
            PrintStream stream = null;
            try {
                outputStream = new FileOutputStream("../wiki/ImageDatabaseSummary.md", false);
                stream = new PrintStream(outputStream, false);
                writeOutput(stream);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null)
                    stream.close();
                if (outputStream != null)
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        private void writeOutput(@NotNull PrintStream stream) throws IOException
        {
            Writer writer = new OutputStreamWriter(stream);
            writer.write("# Image Database Summary\n\n");

            for (String extension : _rowListByExtension.keySet()) {
                writer.write("## " + extension.toUpperCase() + " Files\n\n");

                writer.write("File|Manufacturer|Model|Dir Count|Exif?|Makernote|Thumbnail|All Data\n");
                writer.write("----|------------|-----|---------|-----|---------|---------|--------\n");
                List<Row> rows = _rowListByExtension.get(extension);

                // Order by manufacturer, then model
                Collections.sort(rows, new Comparator<Row>() {
                    public int compare(Row o1, Row o2)
                    {
                        int c1 = StringUtil.compare(o1.manufacturer, o2.manufacturer);
                        return c1 != 0 ? c1 : StringUtil.compare(o1.model, o2.model);
                    }
                });

                for (Row row : rows) {
                    writer.write(String.format("[%s](https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s/%s)|%s|%s|%d|%s|%s|%s|[metadata](https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s/metadata/%s.txt)%n",
                            row.file.getName(),
                            row.relativePath,
                            StringUtil.urlEncode(row.file.getName()),
                            row.manufacturer == null ? "" : row.manufacturer,
                            row.model == null ? "" : row.model,
                            row.metadata.getDirectoryCount(),
                            row.exifVersion == null ? "" : row.exifVersion,
                            row.makernote == null ? "" : row.makernote,
                            row.thumbnail == null ? "" : row.thumbnail,
                            row.relativePath,
                            StringUtil.urlEncode(row.file.getName()).toLowerCase()
                    ));
                }

                writer.write('\n');
            }
            writer.flush();
        }
    }

    /**
     * Keeps track of unknown tags.
     */
    static class UnknownTagHandler extends FileHandlerBase
    {
        private HashMap<String, HashMap<Integer, Integer>> _occurrenceCountByTagByDirectory = new HashMap<String, HashMap<Integer, Integer>>();

        @Override
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
        {
            super.onExtracted(file, metadata, relativePath);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {

                    // Only interested in unknown tags (those without names)
                    if (tag.hasTagName())
                        continue;

                    HashMap<Integer, Integer> occurrenceCountByTag = _occurrenceCountByTagByDirectory.get(directory.getName());
                    if (occurrenceCountByTag == null) {
                        occurrenceCountByTag = new HashMap<Integer, Integer>();
                        _occurrenceCountByTagByDirectory.put(directory.getName(), occurrenceCountByTag);
                    }

                    Integer count = occurrenceCountByTag.get(tag.getTagType());
                    if (count == null) {
                        count = 0;
                        occurrenceCountByTag.put(tag.getTagType(), 0);
                    }

                    occurrenceCountByTag.put(tag.getTagType(), count + 1);
                }
            }
        }

        @Override
        public void onCompleted()
        {
            super.onCompleted();

            for (Map.Entry<String, HashMap<Integer, Integer>> pair1 : _occurrenceCountByTagByDirectory.entrySet()) {
                String directoryName = pair1.getKey();
                List<Map.Entry<Integer, Integer>> counts = new ArrayList<Map.Entry<Integer, Integer>>(pair1.getValue().entrySet());
                Collections.sort(counts, new Comparator<Map.Entry<Integer, Integer>>()
                {
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
                    {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                for (Map.Entry<Integer, Integer> pair2 : counts) {
                    Integer tagType = pair2.getKey();
                    Integer count = pair2.getValue();
                    System.out.format("%s, 0x%04X, %d\n", directoryName, tagType, count);
                }
            }
        }
    }

    /**
     * Does nothing with the output except enumerate it in memory and format descriptions. This is useful in order to
     * flush out any potential exceptions raised during the formatting of extracted value descriptions.
     */
    static class BasicFileHandler extends FileHandlerBase
    {
        @Override
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath)
        {
            super.onExtracted(file, metadata, relativePath);

            // Iterate through all values, calling toString to flush out any formatting exceptions
            for (Directory directory : metadata.getDirectories()) {
                directory.getName();
                for (Tag tag : directory.getTags()) {
                    tag.getTagName();
                    tag.getDescription();
                }
            }
        }
    }
}
