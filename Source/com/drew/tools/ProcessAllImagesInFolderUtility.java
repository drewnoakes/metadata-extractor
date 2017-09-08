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

package com.drew.tools;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
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
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.xmp.XmpDirectory;

import java.io.*;
import java.util.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class ProcessAllImagesInFolderUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        List<String> directories = new ArrayList<String>();

        FileHandler handler = null;
        PrintStream log = System.out;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--text")) {
                // If "--text" is specified, write the discovered metadata into a sub-folder relative to the image
                handler = new TextFileOutputHandler();
            } else if (arg.equalsIgnoreCase("--markdown")) {
                // If "--markdown" is specified, write a summary table in markdown format to standard out
                handler = new MarkdownTableOutputHandler();
            } else if (arg.equalsIgnoreCase("--unknown")) {
                // If "--unknown" is specified, write CSV tallying unknown tag counts
                handler = new UnknownTagHandler();
            } else if (arg.equalsIgnoreCase("--log-file")) {
                if (i == args.length - 1) {
                    printUsage();
                    System.exit(1);
                }
                log = new PrintStream(new FileOutputStream(args[++i], false), true);
            } else {
                // Treat this argument as a directory
                directories.add(arg);
            }
        }

        if (directories.isEmpty()) {
            System.err.println("Expects one or more directories as arguments.");
            printUsage();
            System.exit(1);
        }

        if (handler == null) {
            handler = new BasicFileHandler();
        }

        long start = System.nanoTime();

        for (String directory : directories) {
            processDirectory(new File(directory), handler, "", log);
        }

        handler.onScanCompleted(log);

        System.out.println(String.format("Completed in %d ms", (System.nanoTime() - start) / 1000000));

        if (log != System.out) {
            log.close();
        }
    }

    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println();
        System.out.println("  java com.drew.tools.ProcessAllImagesInFolderUtility [--text|--markdown|--unknown] [--log-file <file-name>]");
    }

    private static void processDirectory(@NotNull File path, @NotNull FileHandler handler, @NotNull String relativePath, PrintStream log)
    {
        handler.onStartingDirectory(path);

        String[] pathItems = path.list();

        if (pathItems == null) {
            return;
        }

        // Order alphabetically so that output is stable across invocations
        Arrays.sort(pathItems);

        for (String pathItem : pathItems) {
            File file = new File(path, pathItem);

            if (file.isDirectory()) {
                processDirectory(file, handler, relativePath.length() == 0 ? pathItem : relativePath + "/" + pathItem, log);
            } else if (handler.shouldProcess(file)) {

                handler.onBeforeExtraction(file, log, relativePath);

                // Read metadata
                final Metadata metadata;
                try {
                    metadata = ImageMetadataReader.readMetadata(file);
                } catch (Throwable t) {
                    handler.onExtractionError(file, t, log);
                    continue;
                }

                handler.onExtractionSuccess(file, metadata, relativePath, log);
            }
        }
    }

    interface FileHandler
    {
        /** Called when the scan is about to start processing files in directory <code>path</code>. */
        void onStartingDirectory(@NotNull File directoryPath);

        /** Called to determine whether the implementation should process <code>filePath</code>. */
        boolean shouldProcess(@NotNull File file);

        /** Called before extraction is performed on <code>filePath</code>. */
        void onBeforeExtraction(@NotNull File file, @NotNull PrintStream log, @NotNull String relativePath);

        /** Called when extraction on <code>filePath</code> completed without an exception. */
        void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log);

        /** Called when extraction on <code>filePath</code> resulted in an exception. */
        void onExtractionError(@NotNull File file, @NotNull Throwable throwable, @NotNull PrintStream log);

        /** Called when all files have been processed. */
        void onScanCompleted(@NotNull PrintStream log);
    }

    abstract static class FileHandlerBase implements FileHandler
    {
        // TODO obtain these from FileType enum directly
        private final Set<String> _supportedExtensions = new HashSet<String>(
            Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp", "ico", "webp", "pcx", "ai", "eps",
                "nef", "crw", "cr2", "orf", "arw", "raf", "srw", "x3f", "rw2", "rwl",
                "tif", "tiff", "psd", "dng",
                "3g2", "3gp", "m4v", "mov", "mp4",
                "pbm", "pnm", "pgm"));

        private int _processedFileCount = 0;
        private int _exceptionCount = 0;
        private int _errorCount = 0;
        private long _processedByteCount = 0;

        public void onStartingDirectory(@NotNull File directoryPath)
        {}

        public boolean shouldProcess(@NotNull File file)
        {
            String extension = getExtension(file);
            return extension != null && _supportedExtensions.contains(extension.toLowerCase());
        }

        public void onBeforeExtraction(@NotNull File file, @NotNull PrintStream log, @NotNull String relativePath)
        {
            _processedFileCount++;
            _processedByteCount += file.length();
        }

        public void onExtractionError(@NotNull File file, @NotNull Throwable throwable, @NotNull PrintStream log)
        {
            _exceptionCount++;
            log.printf("\t[%s] %s\n", throwable.getClass().getName(), throwable.getMessage());
        }

        public void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log)
        {
            if (metadata.hasErrors()) {
                log.print(file);
                log.print('\n');
                for (Directory directory : metadata.getDirectories()) {
                    if (!directory.hasErrors())
                        continue;
                    for (String error : directory.getErrors()) {
                        log.printf("\t[%s] %s\n", directory.getName(), error);
                        _errorCount++;
                    }
                }
            }
        }

        public void onScanCompleted(@NotNull PrintStream log)
        {
            if (_processedFileCount > 0) {
                log.print(String.format(
                    "Processed %,d files (%,d bytes) with %,d exceptions and %,d file errors\n",
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
        /** Standardise line ending so that generated files can be more easily diffed. */
        private static final String NEW_LINE = "\n";

        @Override
        public void onStartingDirectory(@NotNull File directoryPath)
        {
            super.onStartingDirectory(directoryPath);

            // Delete any existing 'metadata' folder
            File metadataDirectory = new File(directoryPath + "/metadata");
            if (metadataDirectory.exists())
                deleteRecursively(metadataDirectory);
        }

        private static void deleteRecursively(@NotNull File directory)
        {
            if (!directory.isDirectory())
                throw new IllegalArgumentException("Must be a directory.");

            if (directory.exists()) {
                String[] list = directory.list();
                if (list != null) {
                    for (String item : list) {
                        File file = new File(item);
                        if (file.isDirectory())
                            deleteRecursively(file);
                        else
                            file.delete();
                    }
                }
            }

            directory.delete();
        }

        @Override
        public void onBeforeExtraction(@NotNull File file, @NotNull PrintStream log, @NotNull String relativePath)
        {
            super.onBeforeExtraction(file, log, relativePath);
            log.print(file.getAbsoluteFile());
            log.print(NEW_LINE);
        }

        @Override
        public void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log)
        {
            super.onExtractionSuccess(file, metadata, relativePath, log);

            try {
                PrintWriter writer = null;
                try
                {
                    writer = openWriter(file);

                    // Write any errors
                    if (metadata.hasErrors()) {
                        for (Directory directory : metadata.getDirectories()) {
                            if (!directory.hasErrors())
                                continue;
                            for (String error : directory.getErrors())
                                writer.format("[ERROR: %s] %s%s", directory.getName(), error, NEW_LINE);
                        }
                        writer.write(NEW_LINE);
                    }

                    // Write tag values for each directory
                    for (Directory directory : metadata.getDirectories()) {
                        String directoryName = directory.getName();
                        // Write the directory's tags
                        for (Tag tag : directory.getTags()) {
                            String tagName = tag.getTagName();
                            String description = tag.getDescription();
                            if (description == null)
                                description = "";
                            // Skip the file write-time as this changes based on the time at which the regression test image repository was cloned
                            if (directory instanceof FileSystemDirectory && tag.getTagType() == FileSystemDirectory.TAG_FILE_MODIFIED_DATE)
                                description = "<omitted for regression testing as checkout dependent>";
                            writer.format("[%s - %s] %s = %s%s", directoryName, tag.getTagTypeHex(), tagName, description, NEW_LINE);
                        }
                        if (directory.getTagCount() != 0)
                            writer.write(NEW_LINE);
                        // Special handling for XMP directory data
                        if (directory instanceof XmpDirectory) {
                            boolean wrote = false;
                            XmpDirectory xmpDirectory = (XmpDirectory)directory;
                            XMPMeta xmpMeta = xmpDirectory.getXMPMeta();
                            try {
                                XMPIterator iterator = xmpMeta.iterator();
                                while (iterator.hasNext()) {
                                    XMPPropertyInfo prop = (XMPPropertyInfo)iterator.next();
                                    String ns = prop.getNamespace();
                                    String path = prop.getPath();
                                    String value = prop.getValue();

                                    if (ns == null)
                                        ns = "";
                                    if (path == null)
                                        path = "";

                                    final int MAX_XMP_VALUE_LENGTH = 512;
                                    if (value == null)
                                        value = "";
                                    else if (value.length() > MAX_XMP_VALUE_LENGTH)
                                        value = String.format("%s <truncated from %d characters>", value.substring(0, MAX_XMP_VALUE_LENGTH), value.length());

                                    writer.format("[XMPMeta - %s] %s = %s%s", ns, path, value, NEW_LINE);
                                    wrote = true;
                                }
                            } catch (XMPException e) {
                                e.printStackTrace();
                            }
                            if (wrote)
                                writer.write(NEW_LINE);
                        }
                    }

                    // Write file structure
                    writeHierarchyLevel(metadata, writer, null, 0);

                    writer.write(NEW_LINE);
                } finally {
                    closeWriter(writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void writeHierarchyLevel(@NotNull Metadata metadata, @NotNull PrintWriter writer, @Nullable Directory parent, int level)
        {
            final int indent = 4;

            for (Directory child : metadata.getDirectories()) {
                if (parent == null) {
                    if (child.getParent() != null)
                        continue;
                } else if (!parent.equals(child.getParent())) {
                    continue;
                }

                for (int i = 0; i < level*indent; i++) {
                    writer.write(' ');
                }
                writer.write("- ");
                writer.write(child.getName());
                writer.write(NEW_LINE);
                writeHierarchyLevel(metadata, writer, child, level + 1);
            }
        }

        @Override
        public void onExtractionError(@NotNull File file, @NotNull Throwable throwable, @NotNull PrintStream log)
        {
            super.onExtractionError(file, throwable, log);

            try {
                PrintWriter writer = null;
                try {
                    writer = openWriter(file);
                    writer.write("EXCEPTION: " + throwable.getMessage() + NEW_LINE);
                    writer.write(NEW_LINE);
                } finally {
                    closeWriter(writer);
                }
            } catch (IOException e) {
                log.printf("IO exception writing metadata file: %s%s", e.getMessage(), NEW_LINE);
            }
        }

        @NotNull
        private static PrintWriter openWriter(@NotNull File file) throws IOException
        {
            // Create the output directory if it doesn't exist
            File metadataDir = new File(String.format("%s/metadata", file.getParent()));
            if (!metadataDir.exists())
                metadataDir.mkdir();

            String outputPath = String.format("%s/metadata/%s.txt", file.getParent(), file.getName());
            Writer writer = new OutputStreamWriter(
                new FileOutputStream(outputPath),
                "UTF-8"
            );
            writer.write("FILE: " + file.getName() + NEW_LINE);

            // Detect file type
            BufferedInputStream stream = null;
            try {
                stream = new BufferedInputStream(new FileInputStream(file));
                FileType fileType = FileTypeDetector.detectFileType(stream);
                writer.write(String.format("TYPE: %s" + NEW_LINE, fileType.toString().toUpperCase()));
                writer.write(NEW_LINE);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return new PrintWriter(writer);
        }

        private static void closeWriter(@Nullable Writer writer) throws IOException
        {
            if (writer != null) {
                writer.write("Generated using metadata-extractor" + NEW_LINE);
                writer.write("https://drewnoakes.com/code/exif/" + NEW_LINE);
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

        static class Row
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
                        break;
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
        public void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log)
        {
            super.onExtractionSuccess(file, metadata, relativePath, log);

            String extension = getExtension(file);

            if (extension == null) {
                return;
            }

            // Sanitise the extension
            extension = extension.toLowerCase();
            if (_extensionEquivalence.containsKey(extension))
                extension = _extensionEquivalence.get(extension);

            List<Row> list = _rowListByExtension.get(extension);
            if (list == null) {
                list = new ArrayList<Row>();
                _rowListByExtension.put(extension, list);
            }
            list.add(new Row(file, metadata, relativePath));
        }

        @Override
        public void onScanCompleted(@NotNull PrintStream log)
        {
            super.onScanCompleted(log);

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

            for (Map.Entry<String, List<Row>> entry : _rowListByExtension.entrySet()) {
                String extension = entry.getKey();
                writer.write("## " + extension.toUpperCase() + " Files\n\n");

                writer.write("File|Manufacturer|Model|Dir Count|Exif?|Makernote|Thumbnail|All Data\n");
                writer.write("----|------------|-----|---------|-----|---------|---------|--------\n");

                List<Row> rows = entry.getValue();

                // Order by manufacturer, then model
                Collections.sort(rows, new Comparator<Row>() {
                    public int compare(Row o1, Row o2)
                    {
                        int c1 = StringUtil.compare(o1.manufacturer, o2.manufacturer);
                        return c1 != 0 ? c1 : StringUtil.compare(o1.model, o2.model);
                    }
                });

                for (Row row : rows) {
                    writer.write(String.format("[%s](https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s/%s)|%s|%s|%d|%s|%s|%s|[metadata](https://raw.githubusercontent.com/drewnoakes/metadata-extractor-images/master/%s/metadata/%s.txt)\n",
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
        public void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log)
        {
            super.onExtractionSuccess(file, metadata, relativePath, log);

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
        public void onScanCompleted(@NotNull PrintStream log)
        {
            super.onScanCompleted(log);

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
                    log.format("%s, 0x%04X, %d\n", directoryName, tagType, count);
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
        public void onExtractionSuccess(@NotNull File file, @NotNull Metadata metadata, @NotNull String relativePath, @NotNull PrintStream log)
        {
            super.onExtractionSuccess(file, metadata, relativePath, log);

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
