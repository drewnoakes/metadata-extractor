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
package com.drew.imaging.jpeg;

import com.drew.lang.ByteTrie;
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessStream;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.adobe.AdobeJpegReader;
import com.drew.metadata.photoshop.DuckyReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.jfif.JfifReader;
import com.drew.metadata.jfxx.JfxxReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Performs read functions of JPEG files, returning specific file segments.
 * <p>
 * JPEG files are composed of a sequence of consecutive JPEG 'segments'. Each is identified by one of a set of byte
 * values, modeled in the {@link JpegSegmentType} enumeration. Use <code>readSegments</code> to read out the some
 * or all segments into a {@link JpegSegmentData} object, from which the raw JPEG segment byte arrays may be accessed.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegSegmentReader
{
    /**
     * The 0xFF byte that signals the start of a segment.
     */
    private static final byte SEGMENT_IDENTIFIER = (byte) 0xFF;

    private static final ByteTrie<String> _appSegmentByPreambleBytes;
    private static final HashSet<Byte> _segmentMarkerBytes;
    
    static
    {
        _appSegmentByPreambleBytes = new ByteTrie<String>();
        _appSegmentByPreambleBytes.addPath(AdobeJpegReader.JPEG_SEGMENT_ID, AdobeJpegReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(DuckyReader.JPEG_SEGMENT_ID, DuckyReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(ExifReader.JPEG_SEGMENT_ID, ExifReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(IccReader.JPEG_SEGMENT_ID, IccReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(JfifReader.JPEG_SEGMENT_ID, JfifReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(JfxxReader.JPEG_SEGMENT_ID, JfxxReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(PhotoshopReader.JPEG_SEGMENT_ID, PhotoshopReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(XmpReader.JPEG_SEGMENT_ID, XmpReader.JPEG_SEGMENT_PREAMBLE.getBytes(Charsets.UTF_8));
        _appSegmentByPreambleBytes.addPath(XmpReader.JPEG_SEGMENT_EXTENSION_ID, XmpReader.JPEG_SEGMENT_PREAMBLE_EXTENSION.getBytes(Charsets.UTF_8));
        
        _segmentMarkerBytes = new HashSet<Byte>();
        _segmentMarkerBytes.add((byte)0x1C);
        
    }

    /**
     * Processes the provided JPEG data, and extracts the specified JPEG segments into a {@link JpegSegmentData} object.
     * <p>
     * Will not return SOS (start of scan) or EOI (end of image) segments.
     *
     * @param file a {@link File} from which the JPEG data will be read.
     * @param segmentTypes the set of JPEG segments types that are to be returned. If this argument is <code>null</code>
     *                     then all found segment types are returned.
     */
    
    @NotNull
    public static JpegSegmentData readSegments(@NotNull File file, @Nullable Collection<JpegSegmentType> segmentTypes) throws JpegProcessingException, IOException
    {
        return readSegments(file, segmentTypes, false);
    }

    @NotNull
    public static JpegSegmentData readSegments(@NotNull File file, @Nullable Collection<JpegSegmentType> segmentTypes, boolean holdBytes) throws JpegProcessingException, IOException
    {
        if(file.isFile())
        {
            RandomAccessFile raFile = null;            
            try {
                raFile = new RandomAccessFile(file, "r");
                return readSegments(new RandomAccessStream(raFile).createReader(), segmentTypes, holdBytes);
            } finally {
                if(raFile != null)
                    raFile.close();
            }
        }
        else
        {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                return readSegments(new RandomAccessStream(stream, file.length()).createReader(), segmentTypes, holdBytes);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }

    @NotNull
    public static JpegSegmentData readSegments(@NotNull final ReaderInfo reader, @Nullable Collection<JpegSegmentType> segmentTypes) throws JpegProcessingException, IOException
    {
        return readSegments(reader, segmentTypes, false);
    }
    
    /**
     * Processes the provided JPEG data, and extracts the specified JPEG segments into a {@link JpegSegmentData} object.
     * <p>
     * Will not return SOS (start of scan) or EOI (end of image) segments.
     *
     * @param reader a {@link ReaderInfo} from which the JPEG data will be read. It must be positioned at the
     *               beginning of the JPEG data stream.
     * @param segmentTypes the set of JPEG segments types that are to be returned. If this argument is <code>null</code>
     *                     then all found segment types are returned.
     */
    @NotNull
    public static JpegSegmentData readSegments(@NotNull final ReaderInfo reader, @Nullable Collection<JpegSegmentType> segmentTypes, boolean holdBytes) throws JpegProcessingException, IOException
    {
        // Must be big-endian
        assert (reader.isMotorolaByteOrder());

        // first two bytes should be JPEG magic number
        final int magicNumber = reader.getUInt16();
        if (magicNumber != 0xFFD8) {
            throw new JpegProcessingException("JPEG data is expected to begin with 0xFFD8 (ÿØ) not 0x" + Integer.toHexString(magicNumber));
        }

        Set<Byte> segmentTypeBytes = null;
        if (segmentTypes != null) {
            segmentTypeBytes = new HashSet<Byte>();
            for (JpegSegmentType segmentType : segmentTypes) {
                segmentTypeBytes.add(segmentType.byteValue);
            }
        }

        JpegSegmentData segments = new JpegSegmentData();

        boolean dobreak = false;
        
        while (true)
        {
            int padding = 0;
            dobreak = false;

            // Find the segment marker. Markers are zero or more 0xFF bytes, followed
            // by a 0xFF and then a byte not equal to 0x00 or 0xFF.
            if (reader.isCloserToEnd(2))
                break;
            byte segmentIdentifier = reader.getInt8();
            byte segmentTypeByte = reader.getInt8();

            // Read until we have a 0xFF byte followed by a byte that is not 0xFF or 0x00
            while (segmentIdentifier != SEGMENT_IDENTIFIER || segmentTypeByte == SEGMENT_IDENTIFIER || segmentTypeByte == 0)
            {
                padding++;
                if (reader.isCloserToEnd(2))
                {
                    dobreak = true;
                    break;
                }
                
                segmentIdentifier = segmentTypeByte;
                segmentTypeByte = reader.getInt8();
            }
            if(dobreak) break;

            JpegSegmentType segmentType = JpegSegmentType.fromByte(segmentTypeByte);

            // decide whether this JPEG segment type's marker is followed by a length indicator
            if (JpegSegmentType.containsPayload(segmentType))
            {
                // Need two more bytes for the segment length. If closer than two bytes to the end, yield
                if (reader.isCloserToEnd(2))
                    break;

                // Read the 2-byte big-endian segment length
                // The length includes the two bytes for the length, but not the two bytes for the marker
                int segmentLength = reader.getUInt16();

                // A length of less than two would be an error
                if (segmentLength < 2)
                    break;

                // get position after id and type bytes (beginning of payload)
                //offset += 2;

                // TODO: would you rather break here or throw an exception?
                /*if (segmentLength > (reader.Length - offset + 1))
                    yield break;        // throw new JpegProcessingException($"Segment {segmentType} is truncated. Processing cannot proceed.");
                else
                {*/
                // segment length includes size bytes, so subtract two
                segmentLength -= 2;
                //}

                // Check whether we are interested in this segment
                if (segmentTypes == null || segmentTypes.contains(segmentType))
                {
                    byte[] preambleBytes = new byte[Math.min(segmentLength, _appSegmentByPreambleBytes.getMaxDepth())];
                    long read = reader.read(preambleBytes, 0, preambleBytes.length);
                    if (read != preambleBytes.length)
                        break;
                    String preamble = _appSegmentByPreambleBytes.find(preambleBytes); //?? "";

                    // Preamble wasn't found in the list. Since preamble is used in string comparisons, set it to blank.
                    // (TODO: this might be a good place to record unknown preambles and report back somehow)
                    if (preamble == null)
                        preamble = "";

                    reader.skip(-read);

                    byte bytemarker = 0x00;
                    if(preamble.length() == 0)
                    {
                        bytemarker = reader.getInt8();
                        if (!_segmentMarkerBytes.contains(bytemarker))
                            bytemarker = 0x00;
                        reader.skip(-1);
                    }
                    JpegSegment segment = new JpegSegment(segmentType, reader.Clone(segmentLength), preamble, bytemarker);
                    if(holdBytes)
                        segment.holdAsBytes();
                    segments.addSegment(segment);
                }

                // seek to the end of the segment
                reader.skip(segmentLength);

                // Sos means entropy encoded data immediately follows, ending with Eoi or another indicator
                // We already did a seek to the end of the SOS segment. A byte-by-byte scan follows to find the next indicator
                if (segmentType == JpegSegmentType.SOS)
                {
                    // yielding here makes Sos processing work the old way (ending at the first one)
                    break;
                }
            }
            else
            {
                // Check whether we are interested in this non-payload segment
                if (segmentTypes == null || segmentTypes.contains(segmentType))
                {
                    JpegSegment segment = new JpegSegment(segmentType, reader.Clone(0));
                    if(holdBytes)
                        segment.holdAsBytes();
                    segments.addSegment(segment);
                }

                if (segmentType == JpegSegmentType.EOI)
                    break;
            }

        }
        
        return segments;
    }

    private JpegSegmentReader() throws Exception
    {
        throw new Exception("Not intended for instantiation.");
    }
}
