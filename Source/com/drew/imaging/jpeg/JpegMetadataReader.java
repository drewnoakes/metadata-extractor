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

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.adobe.AdobeJpegReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.jfif.JfifReader;
import com.drew.metadata.jfxx.JfxxReader;
import com.drew.metadata.jpeg.JpegCommentReader;
import com.drew.metadata.jpeg.JpegDhtReader;
import com.drew.metadata.jpeg.JpegDnlReader;
import com.drew.metadata.jpeg.JpegReader;
import com.drew.metadata.photoshop.DuckyReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Obtains all available metadata from JPEG formatted files.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegMetadataReader
{
    public static final Iterable<JpegSegmentMetadataReader> ALL_READERS = Arrays.asList(
            new JpegReader(),
            new JpegCommentReader(),
            new JfifReader(),
            new JfxxReader(),
            new ExifReader(),
            new XmpReader(),
            new IccReader(),
            new PhotoshopReader(),
            new DuckyReader(),
            new IptcReader(),
            new AdobeJpegReader(),
            new JpegDhtReader(),
            new JpegDnlReader()
    );

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, @Nullable Iterable<JpegSegmentMetadataReader> readers) throws JpegProcessingException, IOException
    {
        Metadata metadata = new Metadata();
        process(metadata, inputStream, readers);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws JpegProcessingException, IOException
    {
        return readMetadata(inputStream, null);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file, @Nullable Iterable<JpegSegmentMetadataReader> readers) throws JpegProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream, readers);
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws JpegProcessingException, IOException
    {
        return readMetadata(file, null);
    }

    public static void process(@NotNull Metadata metadata, @NotNull InputStream inputStream) throws JpegProcessingException, IOException
    {
        process(metadata, inputStream, null);
    }

    public static void process(@NotNull Metadata metadata, @NotNull InputStream inputStream, @Nullable Iterable<JpegSegmentMetadataReader> readers) throws JpegProcessingException, IOException
    {
        if (readers == null)
            readers = ALL_READERS;

        Set<JpegSegmentType> segmentTypes = new HashSet<JpegSegmentType>();
        for (JpegSegmentMetadataReader reader : readers) {
            for (JpegSegmentType type : reader.getSegmentTypes()) {
                segmentTypes.add(type);
            }
        }

        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new StreamReader(inputStream), segmentTypes);

        processJpegSegmentData(metadata, readers, segmentData);
    }

    public static void processJpegSegmentData(Metadata metadata, Iterable<JpegSegmentMetadataReader> readers, JpegSegmentData segmentData)
    {
        // Pass the appropriate byte arrays to each reader.
        for (JpegSegmentMetadataReader reader : readers) {
            for (JpegSegmentType segmentType : reader.getSegmentTypes()) {
                reader.readJpegSegments(segmentData.getSegments(segmentType), metadata, segmentType);
            }
        }
    }

    private JpegMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }
}
