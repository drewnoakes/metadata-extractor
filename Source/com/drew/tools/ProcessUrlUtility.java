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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility that extracts metadata found at a given URL.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ProcessUrlUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length == 0) {
            System.err.println("Expects one or more URLs as arguments.");
            System.exit(1);
        }

        for (String url : args)
            processUrl(new URL(url));

        System.out.println("Completed.");
    }

    private static void processUrl(URL url) throws IOException
    {
        URLConnection con = url.openConnection();
//        con.setConnectTimeout(connectTimeout);
//        con.setReadTimeout(readTimeout);
        InputStream in = con.getInputStream();

        // Read metadata
        final Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(in);
        } catch (ImageProcessingException e) {
            // this is an error in the Jpeg segment structure.  we're looking for bad handling of
            // metadata segments.  in this case, we didn't even get a segment.
            System.err.printf("%s: %s [Error Extracting Metadata]%n\t%s%n", e.getClass().getName(), url, e.getMessage()); return;
        } catch (Throwable t) {
            // general, uncaught exception during processing of jpeg segments
            System.err.printf("%s: %s [Error Extracting Metadata]%n", t.getClass().getName(), url);
            t.printStackTrace(System.err);
            return;
        }

        if (metadata.hasErrors()) {
            System.err.println(url);
            for (Directory directory : metadata.getDirectories()) {
                if (!directory.hasErrors())
                    continue;
                for (String error : directory.getErrors()) {
                    System.err.printf("\t[%s] %s%n", directory.getName(), error);
                }
            }
        }

        // Iterate through all values
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                String directoryName = directory.getName();
                String description = tag.getDescription();

                // truncate the description if it's too long
                if (description != null && description.length() > 1024) {
                    description = description.substring(0, 1024) + "...";
                }

                System.out.printf("[%s] %s = %s%n", directoryName, tagName, description);
            }
        }

//        if (processedCount > 0)
//            System.out.println(String.format("Processed %,d files (%,d bytes) with %,d exceptions and %,d file errors in %s", processedCount, byteCount, exceptionCount, errorCount, path));
    }
}
