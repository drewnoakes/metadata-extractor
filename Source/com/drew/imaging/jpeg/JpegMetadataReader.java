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
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.jfif.JfifReader;
import com.drew.metadata.jpeg.JpegCommentReader;
import com.drew.metadata.jpeg.JpegReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.File;
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
    public static Metadata readMetadata(@NotNull File file) throws JpegProcessingException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(file);
        return extractMetadataFromJpegSegmentReader(segmentReader);
    }

    @NotNull
    public static Metadata extractMetadataFromJpegSegmentReader(@NotNull JpegSegmentReader segmentReader)
    {
        final Metadata metadata = new Metadata();

        // TODO Don't assume this ordering of APP1 segments. We might have XMP without Exif, for example.  Instead, investigate the leading bytes of sections to determine the contents and act accordingly.

        byte[] exifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1, 0);
        new ExifReader(exifSegment).extract(metadata);

        byte[] xmpSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1, 1);
        new XmpReader(xmpSegment).extract(metadata);

        byte[] iptcSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD);
        new IptcReader(iptcSegment).extract(metadata);

        byte[] jpegSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_SOF0);
        new JpegReader(jpegSegment).extract(metadata);

/*
        // alternative jpeg segment code proposed by YB.  untested.
        for (byte i=0;i<16;i++) {
            if (i!=4 && i!=12) {
                byte[] jpegSegment = segmentReader.readSegment((byte) (JpegSegmentReader.SEGMENT_SOF0 + i));
                if (jpegSegment!=null) {
                    JpegDirectory directory = (JpegDirectory)metadata.getDirectory(JpegDirectory.class);
                    directory.setInt(JpegDirectory.TAG_JPEG_COMPRESSION_TYPE, i);
                    new JpegReader(jpegSegment).extract(metadata);
                }
            }
        }
*/

        byte[] jfifSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP0);
        new JfifReader(jfifSegment).extract(metadata);

        byte[] jpegCommentSegment = segmentReader.readSegment(JpegSegmentReader.SEGMENT_COM);
        new JpegCommentReader(jpegCommentSegment).extract(metadata);

        return metadata;
    }

    private JpegMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }
}
