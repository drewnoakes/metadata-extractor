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
package com.drew.metadata.xmp;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.impl.ByteBuffer;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.jpeg.JpegSegment;
import com.drew.lang.Charsets;
import com.drew.lang.ReaderInfo;
import com.drew.metadata.Directory;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import java.io.ByteArrayInputStream;

/**
 * Extracts XMP data from JPEG APP1 segments.
 * <p>
 * Note that XMP uses a namespace and path format for identifying values, which does not map to metadata-extractor's
 * integer based tag identifiers. Therefore, XMP data is extracted and exposed via {@link XmpDirectory#getXMPMeta()}
 * which returns an instance of Adobe's {@link XMPMeta} which exposes the full XMP data set.
 * <p>
 * The extraction is done with Adobe's XmpCore-Library (XMP-Toolkit)
 * Copyright (c) 1999 - 2007, Adobe Systems Incorporated All rights reserved.
 *
 * @author Torsten Skadell
 * @author Drew Noakes https://drewnoakes.com
 * @author https://github.com/bezineb5
 */
public class XmpReader implements JpegSegmentMetadataReader
{
    @NotNull
    public static final String JPEG_SEGMENT_ID = "XMP";
    @NotNull
    public static final String JPEG_SEGMENT_PREAMBLE = "http://ns.adobe.com/xap/1.0/\0";
    
    @NotNull
    public static final String JPEG_SEGMENT_EXTENSION_ID = "XMP (Extended)";
    @NotNull
    public static final String JPEG_SEGMENT_PREAMBLE_EXTENSION = "http://ns.adobe.com/xmp/extension/\0";
    
    @NotNull
    private static final String SCHEMA_XMP_NOTES = "http://ns.adobe.com/xmp/note/";
    @NotNull
    private static final String ATTRIBUTE_EXTENDED_XMP = "xmpNote:HasExtendedXMP";

    /**
     * Extended XMP constants
     */
    private static final int EXTENDED_XMP_GUID_LENGTH = 32;
    private static final int EXTENDED_XMP_INT_LENGTH = 4;
    
    private static final byte[] jpegSegmentPreambleBytes = JPEG_SEGMENT_PREAMBLE.getBytes();
    private static final byte[] jpegSegmentPreambleExtensionBytes = JPEG_SEGMENT_PREAMBLE_EXTENSION.getBytes();
    
    private static final byte[] xmpStringBytes = "XMP".getBytes();

    @NotNull
    @Override
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP1);
    }

    /**
     * Version specifically for dealing with XMP found in JPEG segments. This form of XMP has a peculiar preamble, which
     * must be removed before parsing the XML.
     *
     * @param segments The byte array from which the metadata should be extracted.
     * @param metadata The {@link Metadata} object into which extracted values should be merged.
     * @param segmentType The {@link JpegSegmentType} being read.
     */
    @Override
    public void readJpegSegments(@NotNull Iterable<JpegSegment> segments, @NotNull Metadata metadata) throws IOException
    {
        final int preambleLength = JPEG_SEGMENT_PREAMBLE.length();
        final int extensionPreambleLength = JPEG_SEGMENT_PREAMBLE_EXTENSION.length();
        String extendedXMPGUID = null;
        byte[] extendedXMPBuffer = null;

        for (JpegSegment segment : segments) {
            // XMP in a JPEG file has an identifying preamble which is not valid XML
            if (segment.getReader().getLength() >= preambleLength) {
                // NOTE we expect the full preamble here, but some images (such as that reported on GitHub #102)
                // start with "XMP\0://ns.adobe.com/xap/1.0/" which appears to be an error but is easily recovered
                // from. In such cases, the actual XMP data begins at the same offset.
                if (isXmpSegment(segment)) {
                    extract(segment.getReader().Clone(preambleLength, segment.getReader().getLength() - preambleLength), metadata);

                    // Check in the Standard XMP if there should be a Extended XMP part in other chunks.
                    extendedXMPGUID = getExtendedXMPGUID(metadata);
                    continue;
                }
            }

            // If we know that there's Extended XMP chunks, look for them.
            if (extendedXMPGUID != null &&
                segment.getReader().getLength() >= extensionPreambleLength &&
                isExtendedXmpSegment(segment)) {

                extendedXMPBuffer = processExtendedXMPChunk(metadata, segment.getReader().toArray(), extendedXMPGUID, extendedXMPBuffer);
            }
        }

        // Now that the Extended XMP chunks have been concatenated, let's parse and merge with the Standard XMP.
        if (extendedXMPBuffer != null) {
            extract(extendedXMPBuffer, metadata);
        }
    }

    private static boolean isXmpSegment(JpegSegment segment) throws IOException
    {
        // NOTE we expect the full preamble here, but some images (such as that reported on GitHub #102)
        // start with "XMP\0://ns.adobe.com/xap/1.0/" which appears to be an error but is easily recovered
        // from. In such cases, the actual XMP data begins at the same offset.
        return segment.getReader().startsWith(jpegSegmentPreambleBytes) ||
               segment.getReader().startsWith(xmpStringBytes);
    }
    
    private static boolean isExtendedXmpSegment(JpegSegment segment) throws IOException
    {
        return segment.getReader().startsWith(jpegSegmentPreambleExtensionBytes);
    }
    
    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull ReaderInfo reader, @NotNull Metadata metadata) throws IOException
    {
        extract(reader.Clone().toArray(), metadata);
    }
    
    public void extract(@NotNull ReaderInfo reader, @NotNull Metadata metadata, @Nullable Directory parentDirectory) throws IOException
    {
        extract(reader.Clone().toArray(), metadata, parentDirectory);
    }
    
    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final byte[] xmpBytes, @NotNull Metadata metadata)
    {
        extract(xmpBytes, metadata, null);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final byte[] xmpBytes, @NotNull Metadata metadata, @Nullable Directory parentDirectory)
    {
        extract(xmpBytes, 0, xmpBytes.length, metadata, parentDirectory);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final byte[] xmpBytes, int offset, int length, @NotNull Metadata metadata, @Nullable Directory parentDirectory)
    {
        XmpDirectory directory = new XmpDirectory();

        if (parentDirectory != null)
            directory.setParent(parentDirectory);

        try {
            XMPMeta xmpMeta;

            // If all xmpBytes are requested, no need to make a new ByteBuffer
            if (offset == 0 && length == xmpBytes.length) {
                xmpMeta = XMPMetaFactory.parseFromBuffer(xmpBytes);
            } else {
                ByteBuffer buffer = new ByteBuffer(xmpBytes, offset, length);
                xmpMeta = XMPMetaFactory.parse(buffer.getByteStream());
            }

            directory.setXMPMeta(xmpMeta);                
        } catch (XMPException e) {
            directory.addError("Error processing XMP data: " + e.getMessage());
        }

        if (!directory.isEmpty())
                metadata.addDirectory(directory);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final String xmpString, @NotNull Metadata metadata)
    {
        extract(xmpString, metadata, null);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final StringValue xmpString, @NotNull Metadata metadata)
    {
        extract(xmpString.getBytes(), metadata, null);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final String xmpString, @NotNull Metadata metadata, @Nullable Directory parentDirectory)
    {
        XmpDirectory directory = new XmpDirectory();

        if (parentDirectory != null)
            directory.setParent(parentDirectory);

        try {
            XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmpString);
            directory.setXMPMeta(xmpMeta);
        } catch (XMPException e) {
            directory.addError("Error processing XMP data: " + e.getMessage());
        }

        if (!directory.isEmpty())
            metadata.addDirectory(directory);
    }

    /**
     * Determine if there is an extended XMP section based on the standard XMP part.
     * The xmpNote:HasExtendedXMP attribute contains the GUID of the Extended XMP chunks.
     */
    @Nullable
    private static String getExtendedXMPGUID(@NotNull Metadata metadata)
    {
        final Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);

        for (XmpDirectory directory : xmpDirectories) {
            final XMPMeta xmpMeta = directory.getXMPMeta();

            try {
                final XMPIterator itr = xmpMeta.iterator(SCHEMA_XMP_NOTES, null, null);
                if (itr == null)
                    continue;

                while (itr.hasNext()) {
                    final XMPPropertyInfo pi = (XMPPropertyInfo) itr.next();
                    if (ATTRIBUTE_EXTENDED_XMP.equals(pi.getPath())) {
                        return pi.getValue();
                    }
                }
            } catch (XMPException e) {
                // Fail silently here: we had a reading issue, not a decoding issue.
            }
        }

        return null;
    }

    /**
     * Process an Extended XMP chunk. It will read the bytes from segmentBytes and validates that the GUID the requested one.
     * It will progressively fill the buffer with each chunk.
     * The format is specified in this document:
     * http://www.adobe.com/content/dam/Adobe/en/devnet/xmp/pdfs/XMPSpecificationPart3.pdf
     * at page 19
     */
    @Nullable
    private static byte[] processExtendedXMPChunk(@NotNull Metadata metadata, @NotNull byte[] segmentBytes, @NotNull String extendedXMPGUID, @Nullable byte[] extendedXMPBuffer)
    {
        final int extensionPreambleLength = JPEG_SEGMENT_PREAMBLE_EXTENSION.length();
        final int segmentLength = segmentBytes.length;
        final int totalOffset = extensionPreambleLength + EXTENDED_XMP_GUID_LENGTH + EXTENDED_XMP_INT_LENGTH + EXTENDED_XMP_INT_LENGTH;

        if (segmentLength >= totalOffset) {
            try {
                /*
                 * The chunk contains:
                 * - A null-terminated signature string of "http://ns.adobe.com/xmp/extension/".
                 * - A 128-bit GUID stored as a 32-byte ASCII hex string, capital A-F, no null termination.
                 *   The GUID is a 128-bit MD5 digest of the full ExtendedXMP serialization.
                 * - The full length of the ExtendedXMP serialization as a 32-bit unsigned integer
                 * - The offset of this portion as a 32-bit unsigned integer
                 * - The portion of the ExtendedXMP
                 */
                ReaderInfo reader = ReaderInfo.createFromArray(segmentBytes);
                reader.skip(extensionPreambleLength);
                final String segmentGUID = reader.getString(EXTENDED_XMP_GUID_LENGTH, Charsets.UTF_8);

                if (extendedXMPGUID.equals(segmentGUID)) {
                    final int fullLength = (int)reader.getUInt32();
                    final int chunkOffset = (int)reader.getUInt32();

                    if (extendedXMPBuffer == null)
                        extendedXMPBuffer = new byte[fullLength];

                    if (extendedXMPBuffer.length == fullLength) {
                        System.arraycopy(segmentBytes, totalOffset, extendedXMPBuffer, chunkOffset, segmentLength - totalOffset);
                    } else {
                        XmpDirectory directory = new XmpDirectory();
                        directory.addError(String.format("Inconsistent length for the Extended XMP buffer: %d instead of %d", fullLength, extendedXMPBuffer.length));
                        metadata.addDirectory(directory);
                    }
                }
            } catch (IOException ex) {
                XmpDirectory directory = new XmpDirectory();
                directory.addError(ex.getMessage());
                metadata.addDirectory(directory);
            }
        }

        return extendedXMPBuffer;
    }
}
