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
import org.junit.Test;

import static com.drew.metadata.mkv.EbmlTestUtil.*;
import static org.junit.Assert.*;

public class MkvDescriptorTest
{
    @Test
    public void derivesDurationUsingNonDefaultScaleRegardlessOfFieldOrder() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.SEGMENT_INFO,
            floating(ElementIDs.DURATION, 5250),
            unsigned(ElementIDs.TIMESTAMP_SCALE, 2000000))));
        MkvSegmentDirectory segment = metadata.getFirstDirectoryOfType(MkvSegmentDirectory.class);
        assertNotNull(segment);
        assertEquals(10.5, segment.getDouble(MkvSegmentDirectory.TAG_DURATION_SECONDS), 0);
        assertEquals(5250, segment.getDouble(MkvSegmentDirectory.TAG_DURATION), 0);
        assertEquals("10.5 seconds", segment.getDescription(MkvSegmentDirectory.TAG_DURATION_SECONDS));
        assertEquals("2000000 ns", segment.getDescription(MkvSegmentDirectory.TAG_TIMESTAMP_SCALE));
    }

    @Test
    public void derivesNominalFrameRateWithoutChangingNanosecondDuration() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS,
            element(ElementIDs.TRACK_ENTRY, unsigned(ElementIDs.TRACK_TYPE, 1),
                unsigned(ElementIDs.DEFAULT_DURATION, 40000000), element(ElementIDs.VIDEO)))));
        MkvVideoDirectory video = metadata.getFirstDirectoryOfType(MkvVideoDirectory.class);
        assertNotNull(video);
        assertEquals(25, video.getDouble(MkvVideoDirectory.TAG_FRAME_RATE), 0);
        assertEquals("25 fps", video.getDescription(MkvVideoDirectory.TAG_FRAME_RATE));
        assertEquals("40000000 ns", video.getDescription(MkvTrackDirectory.TAG_DEFAULT_DURATION));
    }

    @Test
    public void describesEnumsAndUnitsAndUnknownValues()
    {
        MkvVideoDirectory video = new MkvVideoDirectory();
        video.setInt(MkvVideoDirectory.TAG_FLAG_INTERLACED, 2);
        video.setInt(MkvVideoDirectory.TAG_MATRIX_COEFFICIENTS, 1);
        video.setInt(MkvVideoDirectory.TAG_TRANSFER_CHARACTERISTICS, 16);
        video.setInt(MkvVideoDirectory.TAG_PRIMARIES, 22);
        video.setInt(MkvVideoDirectory.TAG_RANGE, 2);
        video.setInt(MkvTrackDirectory.TAG_TRACK_TYPE, 1);
        assertEquals("Progressive", video.getDescription(MkvVideoDirectory.TAG_FLAG_INTERLACED));
        assertEquals("BT.709", video.getDescription(MkvVideoDirectory.TAG_MATRIX_COEFFICIENTS));
        assertEquals("SMPTE 2084 (PQ)", video.getDescription(MkvVideoDirectory.TAG_TRANSFER_CHARACTERISTICS));
        assertEquals("EBU Tech. 3213-E", video.getDescription(MkvVideoDirectory.TAG_PRIMARIES));
        assertEquals("Full Range", video.getDescription(MkvVideoDirectory.TAG_RANGE));
        assertEquals("Video", video.getDescription(MkvTrackDirectory.TAG_TRACK_TYPE));
        video.setInt(MkvVideoDirectory.TAG_PRIMARIES, 255);
        assertEquals("Unknown (255)", video.getDescription(MkvVideoDirectory.TAG_PRIMARIES));
        MkvAudioDirectory audio = new MkvAudioDirectory();
        audio.setDouble(MkvAudioDirectory.TAG_SAMPLING_FREQUENCY, 48000);
        assertEquals("48000 Hz", audio.getDescription(MkvAudioDirectory.TAG_SAMPLING_FREQUENCY));
    }

    @Test
    public void describesSegmentUuidWithoutRewritingItsBits()
    {
        MkvSegmentDirectory segment = new MkvSegmentDirectory();
        segment.setByteArray(MkvSegmentDirectory.TAG_SEGMENT_UUID,
            new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
        assertEquals("00010203-0405-0607-0809-0a0b0c0d0e0f",
            segment.getDescription(MkvSegmentDirectory.TAG_SEGMENT_UUID));
    }
}
