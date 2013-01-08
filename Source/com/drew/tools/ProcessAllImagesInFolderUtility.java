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

package com.drew.tools;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.*;
import java.util.*;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ProcessAllImagesInFolderUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length == 0) {
            System.err.println("Expects one or more directories as arguments.");
            System.exit(1);
        }

        // If one of the arguments is "-write" then we write the discovered metadata into a sub-folder relative to the image
        List<String> directories = new ArrayList<String>();

        FileHandler handler = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-write")) {
                handler = new TextFileOutputHandler();
            } else {
                directories.add(arg);
            }
        }

        if (handler == null) {
            handler = new BasicFileHandler();
        }

        long start = System.nanoTime();

        for (String directory : directories) {
            processDirectory(new File(directory), handler);
        }

        handler.onCompleted();

        System.out.println(String.format("Completed in %d ms", (System.nanoTime() - start) / 1000000));
    }

    private static void processDirectory(@NotNull File path, @NotNull FileHandler handler)
    {
        String[] pathItems = path.list();

        if (pathItems == null) {
            return;
        }

        for (String pathItem : pathItems) {
            File file = new File(path, pathItem);

            if (file.isDirectory()) {
                processDirectory(file, handler);
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

        private int processedFileCount = 0;
        private int exceptionCount = 0;
        private int errorCount = 0;
        private long processedByteCount = 0;

        public boolean shouldProcess(@NotNull File file)
        {
            return _supportedExtensions.contains(getExtension(file.getName()));
        }

        public void onProcessingStarting(@NotNull File file)
        {
            processedFileCount++;
            processedByteCount += file.length();
        }

        public void onException(@NotNull File file, @NotNull Throwable throwable)
        {
            exceptionCount++;

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

        public void onExtracted(@NotNull File file, @NotNull Metadata metadata)
        {
            if (metadata.hasErrors()) {
                System.err.println(file);
                for (Directory directory : metadata.getDirectories()) {
                    if (!directory.hasErrors())
                        continue;
                    for (String error : directory.getErrors()) {
                        System.err.printf("\t[%s] %s%n", directory.getName(), error);
                        errorCount++;
                    }
                }
            }
        }

        public void onCompleted()
        {
            if (processedFileCount > 0) {
                System.out.println(String.format(
                    "Processed %,d files (%,d bytes) with %,d exceptions and %,d file errors",
                    processedFileCount, processedByteCount, exceptionCount, errorCount
                ));
            }
        }

        @Nullable
        private String getExtension(@NotNull String path)
        {
            int i = path.lastIndexOf('.');
            if (i == -1)
                return null;
            if (i == path.length() - 1)
                return null;
            return path.substring(i + 1);
        }
    }

    /**
     * Writes a text file containing the extracted metadata for each input file.
     */
    static class TextFileOutputHandler extends FileHandlerBase
    {
        @Override
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata)
        {
            super.onExtracted(file, metadata);

            try {
                writeOutputFile(file, metadata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeOutputFile(File file, Metadata metadata) throws IOException
        {
            FileWriter writer = null;
            try
            {
                String outputPath = String.format("%s\\metadata\\%s.txt", file.getParent(), file.getName());
                writer = new FileWriter(outputPath, false);
                writer.write("FILE: " + file.getName() + "\n");
                writer.write("\n");

                if (metadata.hasErrors()) {
                    for (Directory directory : metadata.getDirectories()) {
                        if (!directory.hasErrors())
                            continue;
                        for (String error : directory.getErrors()) {
                            writer.write(String.format("[ERROR: %s] %s\n", directory.getName(), error));
                        }
                    }
                    writer.write("\n");
                }

                // Iterate through all values
                for (Directory directory : metadata.getDirectories()) {
                    String directoryName = directory.getName();
                    for (Tag tag : directory.getTags()) {
                        String tagName = tag.getTagName();
                        String description = tag.getDescription();
                        writer.write(String.format("[%s - %s] %s = %s%n", directoryName, tag.getTagTypeHex(), tagName, description));
                    }
                    if (directory.getTagCount() != 0) {
                        writer.write("\n");
                    }
                }
            } finally {
                if (writer != null) {
                    writer.write("Generated using metadata-extractor\n");
                    writer.write("http://drewnoakes.com/code/exif/\n");
                    writer.close();
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
        public void onExtracted(@NotNull File file, @NotNull Metadata metadata)
        {
            super.onExtracted(file, metadata);

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
