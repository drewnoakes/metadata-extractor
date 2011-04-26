package com.drew.metadata.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;

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
                try {
                    JpegSegmentReader segmentReader = new JpegSegmentReader(file);
                    try {
                        Metadata metadata = JpegMetadataReader.extractMetadataFromJpegSegmentReader(segmentReader);
                        for (Directory directory : metadata.getDirectories()) {
                            for (Tag tag : directory.getTags()) {
                                // call the code that would obtain the value, just to flush out any potential exceptions
                                tag.toString();
                                tag.getDescription();
                            }
                        }
                    } catch (Throwable t) {
                        // general, uncaught exception during processing of metadata
                        errorCount++;
                        System.err.println(t.getClass().getName() + ": " + file + " [BadMetadata]");
                        t.printStackTrace(System.err);
                    }
                } catch (JpegProcessingException e) {
                    // this is an error in the Jpeg segment structure.  we're looking for bad handling of
                    // metadata segments.  in this case, we didn't even get a segment.
                    errorCount++;
                    System.err.println(e.getClass().getName() + ": " + file + "\n\t" + e.getMessage());
                } catch (Throwable t) {
                    // general, uncaught exception during processing of jpeg segments
                    errorCount++;
                    System.err.println(t.getClass().getName() + ": " + file + " [FAILURE]");
                    t.printStackTrace(System.err);
                }
            }
        }

        if (processedCount > 0)
            System.out.println(String.format("Processed %d files with %d errors in %s", processedCount, errorCount, path));
    }
}
