/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 07-May-2005 12:38:18 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;

import java.io.File;
import java.io.IOException;

public class ExtractMetadataToFileUtility
{
    public static void main(String[] args) throws IOException, JpegProcessingException
    {
        if (args.length != 1) {
            System.err.println("Expects a single argument of the file path to process.");
            System.exit(1);
        }

        String filePath = args[0];
        String outputFilePath = filePath + ".metadata";

        JpegSegmentData segmentData = new JpegSegmentReader(new File(filePath)).getSegmentData();
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_DHT);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_DQT);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_SOF0);
        segmentData.removeSegment(JpegSegmentReader.SEGMENT_SOI);

        System.out.println("Writing output to: " + outputFilePath);
        JpegSegmentData.toFile(new File(outputFilePath), segmentData);
    }

/*
    public void testScanFoldersForImagesThatCauseFailures() throws Exception
    {
//        String directory = "G:/Recovered Images/AquariumC"; // 1446 files 883 MB (done)
//        String directory = "G:/Recovered Images/AquariumF"; // 25,378 files 34.4 GB
//        String directory = "G:/Recovered Images/DesktopC"; // 41,518 files 8.73 GB
//        String directory = "G:/Recovered Images/DesktopF"; // 8,016 files 5.11 GB (done)
//        String directory = "C:/Documents and Settings/Drew/My Documents/IntelliJ Projects/MetadataExtractor/src/";
//        String directory = "C:/Documents and Settings/Drew/My Documents/IntelliJ Projects/MetadataExtractor/Sample Images";
        String directory = "f:/MetadataExtractor/trunk/SampleImages/Outbox";
        processDirectory(directory);
        System.out.println("Complete test successfully.");
    }

    private void processDirectory(String pathName)
    {
        File directory = new File(pathName);
        String[] directoryItems = directory.list();
        if (directoryItems==null)
            return;

        for (int i=0; i<directoryItems.length; i++) {
            String subItem = directoryItems[i].toLowerCase();
            File file = new File(directory, subItem);
            if (!file.exists())
                throw new RuntimeException("World gone nuts.");

            if (file.isDirectory())
            {
                processDirectory(file.getAbsolutePath());
            }
            else if (subItem.endsWith(".jpg") || subItem.endsWith(".jpeg"))
            {
                // process this item
                try
                {
                    JpegSegmentReader segmentReader = new JpegSegmentReader(file);
                    try
                    {
                        JpegMetadataReader.extractMetadataFromJpegSegmentReader(segmentReader);
                    }
                    catch (Throwable t)
                    {
                        // general, uncaught exception during processing of metadata
                        System.err.println(file + "[BadMetadata]");
                        System.err.println(t);
                        System.err.println(t.getMessage());
                        t.printStackTrace(System.err);
                    }
                }
                catch (JpegProcessingException e)
                {
                    System.err.println(file + "[BadSegments]");
                    // this is an error in the Jpeg segment structure.  we're looking for bad handling of
                    // metadata segments.  in this case, we didn't even get a segment.
                }
                catch (Throwable t)
                {
                    // general, uncaught exception during processing of jpeg segments
                    System.err.println(file + "[FAILURE]");
                    System.err.println(t);
                    System.err.println(t.getMessage());
                    t.printStackTrace(System.err);
                }
            }
        }
    }
*/
}