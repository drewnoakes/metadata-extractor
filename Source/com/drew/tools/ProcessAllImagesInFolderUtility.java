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
    private static final Set<String> _supportedExtensions = new HashSet<String>(Arrays.asList("jpg", "jpeg", "nef", "crw", "cr2", "orf", "tif", "tiff"));

    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length == 0) {
            System.err.println("Expects one or more directories as arguments.");
            System.exit(1);
        }

        // If one of the arguments is "-write" then we write the discovered metadata into a sub-folder relative to the image
        List<String> directories = new ArrayList<String>();
        boolean write = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-write")) {
                write = true;
            } else {
                directories.add(arg);
            }
        }

        long start = System.nanoTime();

        for (String directory : directories) {
            processDirectory(directory, write);
        }

        System.out.println(String.format("Completed in %d ms", (System.nanoTime() - start) / 1000000));
    }

    private static void processDirectory(@NotNull String pathName, boolean write)
    {
        File path = new File(pathName);
        String[] pathItems = path.list();
        if (pathItems == null)
            return;

        int processedFileCount = 0;
        int exceptionCount = 0;
        int errorCount = 0;
        long processedByteCount = 0;

        for (String pathItem : pathItems) {
            String subItem = pathItem.toLowerCase();
            File file = new File(path, subItem);

            if (file.isDirectory()) {
                processDirectory(file.getAbsolutePath(), write);
            } else if (_supportedExtensions.contains(getExtension(subItem))) {
                // process this item
                processedFileCount++;
                processedByteCount += file.length();

                // Read metadata
                final Metadata metadata;
                try {
                    metadata = ImageMetadataReader.readMetadata(file);
                } catch (ImageProcessingException e) {
                    // this is an error in the Jpeg segment structure.  we're looking for bad handling of
                    // metadata segments.  in this case, we didn't even get a segment.
                    exceptionCount++;
                    System.err.printf("%s: %s [Error Extracting Metadata]\n\t%s%n", e.getClass().getName(), file, e.getMessage());
                    continue;
                } catch (Throwable t) {
                    // general, uncaught exception during processing of jpeg segments
                    exceptionCount++;
                    System.err.printf("%s: %s [Error Extracting Metadata]%n", t.getClass().getName(), file);
                    t.printStackTrace(System.err);
                    continue;
                }

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

                try {
                    processMetadata(write, file, metadata);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        if (processedFileCount > 0)
            System.out.println(String.format("Processed %,d files (%,d bytes) with %,d exceptions and %,d file errors in %s", processedFileCount, processedByteCount, exceptionCount, errorCount, path));
    }

    private static void processMetadata(boolean write, File file, Metadata metadata) throws IOException
    {
        FileWriter writer = null;
        try
        {
            if (write)
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
            }

            // Iterate through all values
            for (Directory directory : metadata.getDirectories()) {
                String directoryName = directory.getName();
                for (Tag tag : directory.getTags()) {
                    if (writer != null) {
                        String tagName = tag.getTagName();
                        String description = tag.getDescription();
                        writer.write(String.format("[%s] %s = %s%n", directoryName, tagName, description));
                    } else {
                        // Call the code that would obtain the value, just to flush out any potential exceptions.
                        tag.getTagName();
                        tag.getDescription();
                    }
                }
                if (writer != null && directory.getTagCount() != 0) {
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

    private static String getExtension(String path)
    {
        int i = path.lastIndexOf('.');
        if (i == -1)
            return null;
        if (i == path.length() - 1)
            return null;
        return path.substring(i + 1);
    }
}
