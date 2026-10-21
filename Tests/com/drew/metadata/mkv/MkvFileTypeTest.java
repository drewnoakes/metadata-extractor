/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ebml.EbmlTypeChecker;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileTypeDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.drew.metadata.mkv.EbmlTestUtil.*;
import static org.junit.Assert.*;

public class MkvFileTypeTest
{
    @Test
    public void recognizesEbmlSignatureThroughTypeChecker() throws Exception
    {
        EbmlTypeChecker checker = new EbmlTypeChecker();
        byte[] header = header("webm");
        assertEquals(4, checker.getByteCount());
        assertEquals(FileType.Ebml, checker.checkType(header));
        assertEquals(FileType.Unknown, checker.checkType(new byte[] {0x1A, 0x45, (byte)0xDF}));
        assertEquals(FileType.Unknown, checker.checkType(new byte[4]));
        ByteArrayInputStream stream = new ByteArrayInputStream(header);
        assertEquals(FileType.Webm, checker.checkType(header, stream));
        assertEquals(header.length, stream.available());
    }

    @Test
    public void doesNotProbeStreamWhenEbmlSignatureDoesNotMatch() throws Exception
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(header("webm")) {
            @Override
            public synchronized int read()
            {
                throw new AssertionError("A nonmatching type checker must not read the stream");
            }

            @Override
            public synchronized int read(byte[] bytes, int offset, int length)
            {
                throw new AssertionError("A nonmatching type checker must not read the stream");
            }
        };
        assertEquals(FileType.Unknown, new EbmlTypeChecker().checkType(new byte[4], stream));
    }

    @Test
    public void doesNotIncreasePrefixReadsForOtherFileTypes() throws Exception
    {
        byte[] jpeg = new byte[65536];
        jpeg[0] = (byte)0xFF;
        jpeg[1] = (byte)0xD8;
        final int[] bytesRead = new int[1];
        ByteArrayInputStream stream = new ByteArrayInputStream(jpeg) {
            @Override
            public synchronized int read(byte[] bytes, int offset, int length)
            {
                int count = super.read(bytes, offset, length);
                if (count > 0)
                    bytesRead[0] += count;
                return count;
            }
        };
        assertEquals(FileType.Jpeg, FileTypeDetector.detectFileType(stream));
        assertTrue(bytesRead[0] < 1024);
        assertEquals(jpeg.length, stream.available());
    }

    @Test
    public void detectsDocumentTypeWhenAConflictingFilenameIsProvided() throws Exception
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(
            concat(header("webm"), element(ElementIDs.SEGMENT)));
        assertEquals(FileType.Webm, FileTypeDetector.detectFileType(stream, "sample.mkv"));
        assertEquals(0x1A, stream.read());
    }

    @Test
    public void distinguishesWebmFromMatroskaAndRestoresStream() throws Exception
    {
        for (String docType : new String[] {"webm", "matroska", "other"}) {
            byte[] bytes = concat(header(docType), element(ElementIDs.SEGMENT));
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            assertEquals(docType.equals("webm") ? FileType.Webm
                : docType.equals("matroska") ? FileType.Mkv : FileType.Ebml,
                FileTypeDetector.detectFileType(stream));
            assertEquals(bytes.length, stream.available());
            assertEquals(0x1A, stream.read());
        }
    }

    @Test
    public void reportsWebmNameExtensionAndMime() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(
            concat(header("webm"), element(ElementIDs.SEGMENT, element(ElementIDs.TRACKS, video(1))))));
        FileTypeDirectory type = metadata.getFirstDirectoryOfType(FileTypeDirectory.class);
        assertNotNull(type);
        assertEquals("WebM", type.getString(FileTypeDirectory.TAG_DETECTED_FILE_TYPE_NAME));
        assertEquals("webm", type.getString(FileTypeDirectory.TAG_EXPECTED_FILE_NAME_EXTENSION));
        assertEquals("video/webm", type.getString(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE));
    }

    @Test
    public void reportsDeclaredContainerMimeForAudioOnlyWebm() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(
            concat(header("webm"), element(ElementIDs.SEGMENT, element(ElementIDs.TRACKS, audio(1))))));
        assertEquals(FileType.Webm.getMimeType(), metadata.getFirstDirectoryOfType(FileTypeDirectory.class)
            .getString(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE));
        assertEquals(1, metadata.getDirectoriesOfType(MkvAudioDirectory.class).size());
        assertTrue(metadata.getDirectoriesOfType(MkvVideoDirectory.class).isEmpty());
    }

    @Test
    public void boundsDetectionWithoutLosingSupportForLargeHeaders() throws Exception
    {
        byte[] bytes = concat(element(ElementIDs.EBML_HEADER_ELEMENT,
            element(ElementIDs.VOID_ELEMENT, new byte[131072]), string(ElementIDs.DOCTYPE, "webm")),
            element(ElementIDs.SEGMENT, element(ElementIDs.TRACKS, video(1))));
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        assertEquals(FileType.Ebml, FileTypeDetector.detectFileType(stream));
        assertEquals(bytes.length, stream.available());
        Metadata metadata = ImageMetadataReader.readMetadata(stream);
        assertFalse(metadata.hasErrors());
        assertEquals("EBML", metadata.getFirstDirectoryOfType(FileTypeDirectory.class)
            .getString(FileTypeDirectory.TAG_DETECTED_FILE_TYPE_NAME));
        assertEquals("webm", metadata.getFirstDirectoryOfType(EbmlDirectory.class)
            .getString(EbmlDirectory.TAG_DOC_TYPE));
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    @Test
    public void keepsUnsupportedEbmlAsGenericWithExplicitErrors() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(
            concat(header("other"), element(ElementIDs.SEGMENT))));
        assertTrue(metadata.hasErrors());
        assertEquals("EBML", metadata.getFirstDirectoryOfType(FileTypeDirectory.class)
            .getString(FileTypeDirectory.TAG_DETECTED_FILE_TYPE_NAME));
    }
}
