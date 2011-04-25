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
 * Created by dnoakes on 12-Nov-2002 18:51:36 using IntelliJ IDEA.
 * Modified 15-Jan-2008 by Torsten Skadell
 * - extractMetadataFromJpegSegmentReader reads SEGMENT_APP1 two times, first for Exif, second for Xmp
 */
package com.drew.imaging.jpeg;

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
 */
public class JpegMetadataReader
{
    // TODO investigate supporting javax.imageio
//    public static Metadata readMetadata(IIOMetadata metadata) throws JpegProcessingException {}
//    public static Metadata readMetadata(ImageInputStream in) throws JpegProcessingException{}
//    public static Metadata readMetadata(IIOImage image) throws JpegProcessingException{}
//    public static Metadata readMetadata(ImageReader reader) throws JpegProcessingException{}

    public static Metadata readMetadata(InputStream in) throws JpegProcessingException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(in);
        return extractMetadataFromJpegSegmentReader(segmentReader);
    }

    public static Metadata readMetadata(File file) throws JpegProcessingException
    {
        JpegSegmentReader segmentReader = new JpegSegmentReader(file);
        return extractMetadataFromJpegSegmentReader(segmentReader);
    }

    public static Metadata extractMetadataFromJpegSegmentReader(JpegSegmentReader segmentReader)
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
            if (i!=4 && i!= 12) {
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
