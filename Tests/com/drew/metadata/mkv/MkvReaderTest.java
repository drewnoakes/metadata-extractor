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

import com.drew.metadata.Metadata;
import com.drew.imaging.mkv.MkvMetadataReader;
import com.drew.lang.StreamReader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static com.drew.metadata.mkv.EbmlTestUtil.*;
import static org.junit.Assert.*;

public class MkvReaderTest
{
    @Test
    public void enforcesDeclaredMaximumSizeWidthInDocumentBody() throws Exception
    {
        byte[] header = element(ElementIDs.EBML_HEADER_ELEMENT,
            string(ElementIDs.DOCTYPE, "matroska"), unsigned(ElementIDs.EBML_MAX_SIZE_LENGTH, 1));
        Metadata metadata = extract(concat(header, bytes(ElementIDs.SEGMENT), new byte[] {0x40, 0}));
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void endsUnknownMastersWhenAnotherRootElementStarts() throws Exception
    {
        byte[] first = unknownElement(ElementIDs.SEGMENT, 8,
            element(ElementIDs.TRACKS, video(1)),
            unknownElement(ElementIDs.CLUSTER, 1, unsigned(0xE7, 0)));
        byte[] second = element(ElementIDs.SEGMENT, element(ElementIDs.TRACKS, audio(2)));
        for (byte[] separator : new byte[][] {new byte[0], header("webm")}) {
            Metadata metadata = extract(concat(header("matroska"), first, separator, second));
            assertFalse(metadata.hasErrors());
            assertEquals(2, metadata.getDirectoriesOfType(MkvSegmentDirectory.class).size());
            assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
            assertEquals(1, metadata.getDirectoriesOfType(MkvAudioDirectory.class).size());
            assertNotSame(metadata.getFirstDirectoryOfType(MkvVideoDirectory.class).getParent(),
                metadata.getFirstDirectoryOfType(MkvAudioDirectory.class).getParent());
        }
    }

    @Test
    public void rejectsNonMatroskaDocumentTypesAndMissingHeaders() throws Exception
    {
        Metadata unsupported = extract(concat(header("other"), element(ElementIDs.SEGMENT,
            element(ElementIDs.TRACKS, video(1)))));
        assertTrue(unsupported.hasErrors());
        assertTrue(unsupported.getDirectoriesOfType(MkvVideoDirectory.class).isEmpty());
        Metadata missing = extract(element(ElementIDs.SEGMENT, element(ElementIDs.TRACKS, video(1))));
        assertTrue(missing.hasErrors());
        assertTrue(missing.getDirectoriesOfType(MkvSegmentDirectory.class).isEmpty());
    }

    @Test
    public void acceptsNewerDocumentVersionWithSupportedReadVersion() throws Exception
    {
        Metadata metadata = extract(concat(element(ElementIDs.EBML_HEADER_ELEMENT,
            string(ElementIDs.DOCTYPE, "matroska"),
            unsigned(ElementIDs.DOCTYPE_VERSION, 100),
            unsigned(ElementIDs.DOCTYPE_READ_VERSION, 2)), element(ElementIDs.SEGMENT)));
        assertFalse(metadata.hasErrors());
        assertEquals(100, metadata.getFirstDirectoryOfType(EbmlDirectory.class)
            .getInt(EbmlDirectory.TAG_DOC_TYPE_VERSION));
    }

    @Test
    public void rejectsUnsupportedReadVersionsAndIdLimits() throws Exception
    {
        for (byte[] invalid : new byte[][] {
            unsigned(ElementIDs.DOCTYPE_READ_VERSION, 5),
            unsigned(ElementIDs.EBML_READ_VERSION, 2),
            unsigned(ElementIDs.EBML_MAX_ID_LENGTH, 8),
            unsigned(ElementIDs.EBML_MAX_SIZE_LENGTH, 9)
        }) {
            Metadata metadata = extract(concat(element(ElementIDs.EBML_HEADER_ELEMENT,
                string(ElementIDs.DOCTYPE, "matroska"), invalid), element(ElementIDs.SEGMENT)));
            assertTrue(metadata.hasErrors());
            assertTrue(metadata.getDirectoriesOfType(MkvSegmentDirectory.class).isEmpty());
        }
    }

    @Test
    public void appliesDeclaredDefaultsToAbsentAndEmptyElements() throws Exception
    {
        Metadata metadata = extract(document(
            element(ElementIDs.SEGMENT_INFO, element(ElementIDs.TIMESTAMP_SCALE)),
            element(ElementIDs.TRACKS,
                element(ElementIDs.TRACK_ENTRY,
                    unsigned(ElementIDs.TRACK_TYPE, 2),
                    element(ElementIDs.TAG_LACING),
                    element(ElementIDs.LANGUAGE),
                    element(ElementIDs.AUDIO, element(ElementIDs.CHANNELS))))));
        MkvAudioDirectory audio = metadata.getFirstDirectoryOfType(MkvAudioDirectory.class);
        assertNotNull(audio);
        assertEquals(8000, audio.getDouble(MkvAudioDirectory.TAG_SAMPLING_FREQUENCY), 0);
        assertEquals(1, audio.getInt(MkvAudioDirectory.TAG_CHANNELS));
        assertEquals(1, audio.getInt(MkvTrackDirectory.TAG_FLAG_LACING));
        assertEquals("eng", audio.getString(MkvTrackDirectory.TAG_LANGUAGE));
        assertEquals(1000000, metadata.getFirstDirectoryOfType(MkvSegmentDirectory.class)
            .getLong(MkvSegmentDirectory.TAG_TIMESTAMP_SCALE));
    }

    @Test
    public void preservesExplicitNonDefaultValues() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS,
            element(ElementIDs.TRACK_ENTRY,
                unsigned(ElementIDs.TRACK_TYPE, 1),
                unsigned(ElementIDs.TAG_LACING, 0),
                string(ElementIDs.LANGUAGE, "fra"),
                element(ElementIDs.VIDEO, unsigned(ElementIDs.FLAG_INTERLACED, 2))))));
        MkvVideoDirectory video = metadata.getFirstDirectoryOfType(MkvVideoDirectory.class);
        assertNotNull(video);
        assertEquals(0, video.getInt(MkvTrackDirectory.TAG_FLAG_LACING));
        assertEquals("fra", video.getString(MkvTrackDirectory.TAG_LANGUAGE));
        assertEquals(2, video.getInt(MkvVideoDirectory.TAG_FLAG_INTERLACED));
    }

    @Test
    public void exposesNestedColourFields() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS,
            element(ElementIDs.TRACK_ENTRY,
                unsigned(ElementIDs.TRACK_TYPE, 1),
                element(ElementIDs.VIDEO,
                    unsigned(ElementIDs.PIXEL_WIDTH, 32),
                    element(ElementIDs.COLOR,
                        unsigned(ElementIDs.MATRIX_COEFFICIENTS, 1),
                        unsigned(ElementIDs.TRANSFER_CHARACTERISTICS, 16),
                        unsigned(ElementIDs.PRIMARIES, 9),
                        unsigned(ElementIDs.RANGE, 1),
                        unsigned(ElementIDs.CHROMA_SITING_HORZ, 1),
                        unsigned(ElementIDs.CHROMA_SITING_VERT, 2)))))));
        MkvVideoDirectory video = metadata.getFirstDirectoryOfType(MkvVideoDirectory.class);
        assertNotNull(video);
        assertEquals(1, video.getInt(MkvVideoDirectory.TAG_MATRIX_COEFFICIENTS));
        assertEquals(16, video.getInt(MkvVideoDirectory.TAG_TRANSFER_CHARACTERISTICS));
        assertEquals(9, video.getInt(MkvVideoDirectory.TAG_PRIMARIES));
        assertEquals(1, video.getInt(MkvVideoDirectory.TAG_RANGE));
        assertEquals(1, video.getInt(MkvVideoDirectory.TAG_CHROMA_SITING_HORZ));
        assertEquals(2, video.getInt(MkvVideoDirectory.TAG_CHROMA_SITING_VERT));
    }

    @Test
    public void readsUnknownSizedSegmentsAndClusters() throws Exception
    {
        for (int width : new int[] {1, 8}) {
            byte[] tracks = element(ElementIDs.TRACKS, video(1), audio(2));
            byte[] cluster = unknownElement(ElementIDs.CLUSTER, width,
                unsigned(0xE7, 0), element(ElementIDs.VOID_ELEMENT, new byte[4]));
            Metadata metadata = extract(concat(header("webm"),
                unknownElement(ElementIDs.SEGMENT, width, cluster, tracks)));
            assertFalse(metadata.hasErrors());
            assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
            assertEquals(1, metadata.getDirectoriesOfType(MkvAudioDirectory.class).size());
        }
    }

    @Test
    public void endsUnknownClusterAtKnownParentBoundary() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS, video(1)),
            unknownElement(ElementIDs.CLUSTER, 1, unsigned(0xE7, 0))));
        assertFalse(metadata.hasErrors());
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    @Test
    public void rejectsElementThatExceedsItsParent() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.SEGMENT_INFO,
            concat(bytes(ElementIDs.MUXING_APP), size(100), new byte[1]))));
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void rejectsUnknownSizedScalar() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.SEGMENT_INFO,
            unknownElement(ElementIDs.MUXING_APP, 1))));
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void streamsPastThreeGiBClusterWithoutBufferingIt() throws Exception
    {
        final long padding = 3L * 1024 * 1024 * 1024;
        byte[] tracks = element(ElementIDs.TRACKS, video(1));
        byte[] clusterHeader = concat(bytes(ElementIDs.CLUSTER), size(padding));
        byte[] prefix = concat(header("matroska"), bytes(ElementIDs.SEGMENT),
            size(clusterHeader.length + padding + tracks.length), clusterHeader);
        final InputStream stream = new PaddedStream(prefix, padding, tracks);
        StreamReader reader = new StreamReader(stream) {
            @Override
            public byte[] getBytes(int count) throws IOException
            {
                assertTrue("Must not buffer master elements", count <= 16);
                return super.getBytes(count);
            }
        };
        Metadata metadata = new Metadata();
        new MkvReader().extract(reader, metadata);
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
        assertEquals(prefix.length + padding + tracks.length, reader.getPosition());
    }

    @Test
    public void skipsReadablePayloadWhenInputStreamSkipReturnsZero() throws Exception
    {
        Metadata metadata = MkvMetadataReader.readMetadata(new ByteArrayInputStream(
            document(element(ElementIDs.CLUSTER, new byte[32]),
                element(ElementIDs.TRACKS, video(1)))) {
            @Override
            public long skip(long count)
            {
                return 0;
            }
        });
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    private static final class PaddedStream extends InputStream
    {
        private final byte[] _prefix;
        private final long _padding;
        private final byte[] _suffix;
        private long _position;

        PaddedStream(byte[] prefix, long padding, byte[] suffix)
        {
            _prefix = prefix;
            _padding = padding;
            _suffix = suffix;
        }

        @Override
        public int read()
        {
            long position = _position++;
            if (position < _prefix.length)
                return _prefix[(int)position] & 0xFF;
            if (position < _prefix.length + _padding)
                return 0;
            position -= _prefix.length + _padding;
            return position < _suffix.length ? _suffix[(int)position] & 0xFF : -1;
        }

        @Override
        public long skip(long count)
        {
            long skipped = Math.min(count, _prefix.length + _padding + _suffix.length - _position);
            _position += skipped;
            return skipped;
        }
    }

    @Test
    public void exposesUnsignedTrackUidWithoutLosingPrecision() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS,
            element(ElementIDs.TRACK_ENTRY,
                unsigned(ElementIDs.TRACK_TYPE, 1),
                unsigned(ElementIDs.TRACK_UID, -1L),
                element(ElementIDs.VIDEO)))));
        MkvVideoDirectory video = metadata.getFirstDirectoryOfType(MkvVideoDirectory.class);
        assertNotNull(video);
        assertEquals("18446744073709551615", video.getString(ElementIDs.TRACK_UID));
        assertEquals("18446744073709551615", video.getDescription(ElementIDs.TRACK_UID));
    }

    @Test
    public void usesDefaultForEmptySamplingFrequency() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS,
            element(ElementIDs.TRACK_ENTRY,
                unsigned(ElementIDs.TRACK_NUMBER, 1),
                unsigned(ElementIDs.TRACK_TYPE, 2),
                element(ElementIDs.AUDIO, element(ElementIDs.SAMPLING_FREQUENCY))))));
        MkvAudioDirectory audio = metadata.getFirstDirectoryOfType(MkvAudioDirectory.class);
        assertNotNull(audio);
        assertEquals(8000, audio.getDouble(ElementIDs.SAMPLING_FREQUENCY), 0);
    }

    @Test
    public void readsStreamWhoseAvailableIsZero() throws Exception
    {
        Metadata metadata = MkvMetadataReader.readMetadata(new ByteArrayInputStream(
            document(element(ElementIDs.TRACKS, video(1)))) {
            @Override
            public int available()
            {
                return 0;
            }
        });
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    @Test
    public void doesNotTreatTruncatedElementHeaderAsCleanEof() throws Exception
    {
        Metadata metadata = extract(concat(document(element(ElementIDs.TRACKS, video(1))),
            new byte[] {0x1A}));
        assertTrue(metadata.hasErrors());
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    @Test
    public void preservesInfoAndTrackValuesWhenLaterPayloadIsTruncated() throws Exception
    {
        byte[] payload = concat(element(ElementIDs.SEGMENT_INFO, string(ElementIDs.MUXING_APP, "mux")),
            element(ElementIDs.TRACKS, video(1)),
            concat(bytes(ElementIDs.CLUSTER), size(100), new byte[2]));
        Metadata metadata = extract(concat(header("matroska"), bytes(ElementIDs.SEGMENT),
            size(payload.length + 98), payload));
        assertTrue(metadata.hasErrors());
        assertEquals("mux", metadata.getFirstDirectoryOfType(MkvSegmentDirectory.class)
            .getString(MkvSegmentDirectory.TAG_MUXING_APP));
        assertEquals(32, metadata.getFirstDirectoryOfType(MkvVideoDirectory.class)
            .getInt(MkvVideoDirectory.TAG_PIXEL_WIDTH));
    }

    @Test
    public void reportsDuplicateScalarValues() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.SEGMENT_INFO,
            string(ElementIDs.MUXING_APP, "first"), string(ElementIDs.MUXING_APP, "second"))));
        assertTrue(metadata.hasErrors());
        assertEquals("second", metadata.getFirstDirectoryOfType(MkvSegmentDirectory.class)
            .getString(MkvSegmentDirectory.TAG_MUXING_APP));
    }

    @Test
    public void extractsSingleVideoTrack() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS, video(1))));
        MkvVideoDirectory video = metadata.getFirstDirectoryOfType(MkvVideoDirectory.class);
        assertNotNull(video);
        assertEquals(32, video.getInt(ElementIDs.PIXEL_WIDTH));
        assertEquals("V_VP8", video.getString(ElementIDs.CODEC_ID));
        assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
    }

    @Test
    public void extractsSingleAudioTrack() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS, audio(1))));
        MkvAudioDirectory audio = metadata.getFirstDirectoryOfType(MkvAudioDirectory.class);
        assertNotNull(audio);
        assertEquals(8000, audio.getDouble(ElementIDs.SAMPLING_FREQUENCY), 0);
        assertEquals(1, metadata.getDirectoriesOfType(MkvAudioDirectory.class).size());
    }

    @Test
    public void preservesAllRepeatedTracks() throws Exception
    {
        for (int count = 2; count <= 4; count++) {
            byte[][] tracks = new byte[count][];
            tracks[0] = video(1);
            for (int i = 1; i < count; i++)
                tracks[i] = audio(i + 1);
            Metadata metadata = extract(document(element(ElementIDs.TRACKS, tracks)));
            assertEquals(1, metadata.getDirectoriesOfType(MkvVideoDirectory.class).size());
            assertEquals(count - 1, metadata.getDirectoriesOfType(MkvAudioDirectory.class).size());
            List<Long> numbers = new ArrayList<Long>();
            for (MkvAudioDirectory audio : metadata.getDirectoriesOfType(MkvAudioDirectory.class))
                numbers.add(audio.getLong(ElementIDs.TRACK_NUMBER));
            assertEquals(Arrays.asList(2L, 3L, 4L).subList(0, count - 1), numbers);
        }
    }
}
