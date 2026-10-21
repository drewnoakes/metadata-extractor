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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.drew.metadata.mkv.EbmlTestUtil.*;
import static org.junit.Assert.*;

public class MkvReaderTest
{
    @Test
    public void extractsSingleVideoTrack() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS, video(1))));
        VideoDirectory video = metadata.getFirstDirectoryOfType(VideoDirectory.class);
        assertNotNull(video);
        assertEquals(32, video.getInt(ElementIDs.PIXEL_WIDTH));
        assertEquals("V_VP8", video.getString(ElementIDs.CODEC_ID));
        assertEquals(1, metadata.getDirectoriesOfType(VideoDirectory.class).size());
    }

    @Test
    public void extractsSingleAudioTrack() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TRACKS, audio(1))));
        AudioDirectory audio = metadata.getFirstDirectoryOfType(AudioDirectory.class);
        assertNotNull(audio);
        assertEquals(8000, audio.getDouble(ElementIDs.SAMPLING_FREQUENCY), 0);
        assertEquals(1, metadata.getDirectoriesOfType(AudioDirectory.class).size());
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
            assertEquals(1, metadata.getDirectoriesOfType(VideoDirectory.class).size());
            assertEquals(count - 1, metadata.getDirectoriesOfType(AudioDirectory.class).size());
            List<Long> numbers = new ArrayList<Long>();
            for (AudioDirectory audio : metadata.getDirectoriesOfType(AudioDirectory.class))
                numbers.add(audio.getLong(ElementIDs.TRACK_NUMBER));
            assertEquals(Arrays.asList(2L, 3L, 4L).subList(0, count - 1), numbers);
        }
    }
}
