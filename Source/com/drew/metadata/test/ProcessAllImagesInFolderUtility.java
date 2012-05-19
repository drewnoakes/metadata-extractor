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

package com.drew.metadata.test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;

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

        for (String directory : args)
            processDirectory(directory);

        System.out.println("Completed.");
    }

    private static void processDirectory(String pathName)
    {
        File path = new File(pathName);
        String[] pathItems = path.list();
        if (pathItems==null)
            return;

        int processedCount = 0;
        int errorCount = 0;

        for (String pathItem : pathItems) {
            String subItem = pathItem.toLowerCase();
            File file = new File(path, subItem);

            if (file.isDirectory()) {
                processDirectory(file.getAbsolutePath());
            } else if (subItem.endsWith(".jpg") || subItem.endsWith(".jpeg") || subItem.endsWith(".nef") || subItem.endsWith(".crw") || subItem.endsWith(".cr2") || subItem.endsWith(".tif")) {
                // process this item
                processedCount++;

                // Read metadata
                final Metadata metadata;
                try {
                    metadata = ImageMetadataReader.readMetadata(file);
                } catch (ImageProcessingException e) {
                    // this is an error in the Jpeg segment structure.  we're looking for bad handling of
                    // metadata segments.  in this case, we didn't even get a segment.
                    errorCount++;
                    System.err.println(e.getClass().getName() + ": " + file + " [Error Extracting Metadata]" + "\n\t" + e.getMessage());
                    continue;
                } catch (Throwable t) {
                    // general, uncaught exception during processing of jpeg segments
                    errorCount++;
                    System.err.println(t.getClass().getName() + ": " + file + " [Error Extracting Metadata]");
                    t.printStackTrace(System.err);
                    continue;
                }

                if (metadata.hasErrors()) {
                    System.err.println(file);
                    for (Directory directory : metadata.getDirectories()) {
                        if (!directory.hasErrors())
                            continue;
                        for (String error : directory.getErrors())
                            System.err.println("[" + directory.getName() + "] " + error);
                    }
                }

                // Iterate through all values
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        // call the code that would obtain the value, just to flush out any potential exceptions
                        tag.toString();
                        tag.getDescription();
                    }
                }
            }
        }

        if (processedCount > 0)
            System.out.println(String.format("Processed %d files with %d errors in %s", processedCount, errorCount, path));
    }
}
