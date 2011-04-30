/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.jfif.JfifReader;
import com.drew.metadata.jpeg.JpegCommentReader;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.jpeg.JpegReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains all available metadata from Jpeg formatted files.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegMetadataReader
{
    // TODO investigate supporting javax.imageio
//    public static Metadata readMetadata(IIOMetadata metadata) throws JpegProcessingException {}
//    public static Metadata readMetadata(ImageInputStream in) throws JpegProcessingException{}
//    public static Metadata readMetadata(IIOImage image) throws JpegProcessingException{}
//    public static Metadata readMetadata(ImageReader reader) throws JpegProcessingException{}

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws JpegProcessingException
    {
        return readMetadata(inputStream, true);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, final boolean waitForBytes) throws JpegProcessingException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(inputStream, waitForBytes);
        return extractMetadataFromJpegSegmentReader(segmentReader);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws JpegProcessingException, IOException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(file);
        return extractMetadataFromJpegSegmentReader(segmentReader);
    }

    @NotNull
    public static Metadata extractMetadataFromJpegSegmentReader(@NotNull JpegSegmentReader segmentReader)
    {
        final Metadata metadata = new Metadata();

        // Loop through looking for all SOFn segments.  When we find one, we know what type of compression
        // was used for the JPEG, and we can process the JPEG metadata in the segment too.
        for (byte i = 0; i < 16; i++) {
            // There are no SOF4 or SOF12 segments, so don't bother
            if (i == 4 || i == 12)
                continue;
            // Should never have more than one SOFn for a given 'n'.
            byte[] jpegSegment = segmentReader.readSegment((byte)(JpegSegmentReader.SEGMENT_SOF0 + i));
            if (jpegSegment == null)
                continue;
            JpegDirectory directory = metadata.getOrCreateDirectory(JpegDirectory.class);
            directory.setInt(JpegDirectory.TAG_JPEG_COMPRESSION_TYPE, i);
            new JpegReader(jpegSegment).extract(metadata);
            break;
        }

        // There should never be more than one COM segment.
        byte[] comSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_COM);
        if (comSegment != null)
            new JpegCommentReader(comSegment).extract(metadata);

        // Loop through all APP0 segments, looking for a JFIF segment.
        for (byte[] app0Segment : segmentReader.readSegments(JpegSegmentReader.SEGMENT_APP0)) {
            if (app0Segment.length > 3 && new String(app0Segment, 0, 4).equals("JFIF"))
                new JfifReader(app0Segment).extract(metadata);
        }

        // Loop through all APP1 segments, checking the leading bytes to identify the format of each.
        for (byte[] app1Segment : segmentReader.readSegments(JpegSegmentReader.SEGMENT_APP1)) {
            if (app1Segment.length > 3 && "EXIF".equalsIgnoreCase(new String(app1Segment, 0, 4)))
                new ExifReader(app1Segment).extract(metadata);

            if (app1Segment.length > 27 && "http://ns.adobe.com/xap/1.0/".equalsIgnoreCase(new String(app1Segment, 0, 28)))
                new XmpReader(app1Segment).extract(metadata);
        }

        // Loop through all APP2 segments, looking for something we can process.
        for (byte[] app2Segment : segmentReader.readSegments(JpegSegmentReader.SEGMENT_APP2)) {
            if (app2Segment.length > 10 && new String(app2Segment, 0, 11).equalsIgnoreCase("ICC_PROFILE")) {
                byte[] icc = new byte[app2Segment.length-14];
                System.arraycopy(app2Segment, 14, icc, 0, app2Segment.length-14);
                new IccReader(icc).extract(metadata);
            }
        }

        // Loop through all APPD segments, checking the leading bytes to identify the format of each.
        for (byte[] appdSegment : segmentReader.readSegments(JpegSegmentReader.SEGMENT_APPD)) {
            if (appdSegment.length > 12 && "Photoshop 3.0".compareTo(new String(appdSegment, 0, 13))==0) {
                new PhotoshopReader(appdSegment).extract(metadata);
            } else {
                // TODO might be able to check for a leading 0x1c02 for IPTC data...
                new IptcReader(appdSegment).extract(metadata);
            }
        }

        return metadata;
    }

    private JpegMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }
}

