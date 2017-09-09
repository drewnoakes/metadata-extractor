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
package com.drew.metadata.photoshop;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.xmp.XmpReader;

import java.util.Arrays;
import java.util.Collections;

/**
 * Reads metadata created by Photoshop and stored in the APPD segment of JPEG files.
 * Note that IPTC data may be stored within this segment, in which case this reader will
 * create both a {@link PhotoshopDirectory} and a {@link com.drew.metadata.iptc.IptcDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Yuri Binev
 * @author Payton Garland
 */
public class PhotoshopReader implements JpegSegmentMetadataReader
{
    @NotNull
    private static final String JPEG_SEGMENT_PREAMBLE = "Photoshop 3.0";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APPD);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        final int preambleLength = JPEG_SEGMENT_PREAMBLE.length();

        for (byte[] segmentBytes : segments) {
            // Ensure data starts with the necessary preamble
            if (segmentBytes.length < preambleLength + 1 || !JPEG_SEGMENT_PREAMBLE.equals(new String(segmentBytes, 0, preambleLength)))
                continue;

            extract(
                new SequentialByteArrayReader(segmentBytes, preambleLength + 1),
                segmentBytes.length - preambleLength - 1,
                metadata);
        }
    }

    public void extract(@NotNull final SequentialReader reader, int length, @NotNull final Metadata metadata)
    {
        PhotoshopDirectory directory = new PhotoshopDirectory();
        metadata.addDirectory(directory);

        // Data contains a sequence of Image Resource Blocks (IRBs):
        //
        // 4 bytes - Signature; mostly "8BIM" but "PHUT", "AgHg" and "DCSR" are also found
        // 2 bytes - Resource identifier
        // String  - Pascal string, padded to make length even
        // 4 bytes - Size of resource data which follows
        // Data    - The resource data, padded to make size even
        //
        // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577409_pgfId-1037504

        int pos = 0;
        int clippingPathCount = 0;
        while (pos < length) {
            try {
                // 4 bytes for the signature ("8BIM", "PHUT", etc.)
                String signature = reader.getString(4);
                pos += 4;

                // 2 bytes for the resource identifier (tag type).
                int tagType = reader.getUInt16(); // segment type
                pos += 2;

                // A variable number of bytes holding a pascal string (two leading bytes for length).
                short descriptionLength = reader.getUInt8();
                pos += 1;
                // Some basic bounds checking
                if (descriptionLength < 0 || descriptionLength + pos > length)
                    throw new ImageProcessingException("Invalid string length");

                // Get name (important for paths)
                StringBuilder description = new StringBuilder();
                descriptionLength += pos;
                // Loop through each byte and append to string
                while (pos < descriptionLength) {
                    description.append((char)reader.getUInt8());
                    pos ++;
                }

                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 != 0) {
                    reader.skip(1);
                    pos++;
                }

                // 4 bytes for the size of the resource data that follows.
                int byteCount = reader.getInt32();
                pos += 4;
                // The resource data.
                byte[] tagBytes = reader.getBytes(byteCount);
                pos += byteCount;
                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 != 0) {
                    reader.skip(1);
                    pos++;
                }

                if (signature.equals("8BIM")) {
                    if (tagType == PhotoshopDirectory.TAG_IPTC)
                        new IptcReader().extract(new SequentialByteArrayReader(tagBytes), metadata, tagBytes.length, directory);
                    else if (tagType == PhotoshopDirectory.TAG_ICC_PROFILE_BYTES)
                        new IccReader().extract(new ByteArrayReader(tagBytes), metadata, directory);
                    else if (tagType == PhotoshopDirectory.TAG_EXIF_DATA_1 || tagType == PhotoshopDirectory.TAG_EXIF_DATA_3)
                        new ExifReader().extract(new ByteArrayReader(tagBytes), metadata, 0, directory);
                    else if (tagType == PhotoshopDirectory.TAG_XMP_DATA)
                        new XmpReader().extract(tagBytes, metadata, directory);
                    else if (tagType >= 0x07D0 && tagType <= 0x0BB6) {
                        clippingPathCount++;
                        tagBytes = Arrays.copyOf(tagBytes, tagBytes.length + description.length() + 1);
                        // Append description(name) to end of byte array with 1 byte before the description representing the length
                        for (int i = tagBytes.length - description.length() - 1; i < tagBytes.length; i++) {
                            if (i % (tagBytes.length - description.length() - 1 + description.length()) == 0)
                                tagBytes[i] = (byte)description.length();
                            else
                                tagBytes[i] = (byte)description.charAt(i - (tagBytes.length - description.length() - 1));
                        }
                        PhotoshopDirectory._tagNameMap.put(0x07CF + clippingPathCount, "Path Info " + clippingPathCount);
                        directory.setByteArray(0x07CF + clippingPathCount, tagBytes);
                    }
                    else
                        directory.setByteArray(tagType, tagBytes);

                    if (tagType >= 0x0fa0 && tagType <= 0x1387)
                        PhotoshopDirectory._tagNameMap.put(tagType, String.format("Plug-in %d Data", tagType - 0x0fa0 + 1));
                }
            } catch (Exception ex) {
                directory.addError(ex.getMessage());
                return;
            }
        }
    }
}
