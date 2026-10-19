/*
 * Copyright 2002-2025 Drew Noakes and contributors
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
package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The audio/video sample-entry format is a four-character code (e.g. {@code avc1}, {@code mp4a}).
 * It is passed through {@link com.drew.metadata.mp4.Mp4Dictionary#setLookup} into a human-readable
 * description (or "Unknown"), which loses the raw code. These tests cover exposing it unchanged via
 * the {@code *_RAW} tags.
 */
public class Mp4SampleEntryFormatTest
{
    @Test public void testVideoSampleEntryExposesRawFormat() throws Exception
    {
        // A minimal VisualSampleEntry (ISO/IEC 14496-12) carrying the 'avc1' format code.
        final byte[] bytes = new byte[] {
            0x00, 0x00, 0x00, 0x00,                         // version + flags (skipped)
            0x00, 0x00, 0x00, 0x01,                         // entry count = 1
            0x00, 0x00, 0x00, 0x56,                         // sample description size
            0x61, 0x76, 0x63, 0x31,                         // format = "avc1"
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,             // reserved
            0x00, 0x01,                                     // data reference index
            0x00, 0x00,                                     // version
            0x00, 0x00,                                     // revision level
            0x00, 0x00, 0x00, 0x00,                         // vendor
            0x00, 0x00, 0x00, 0x00,                         // temporal quality
            0x00, 0x00, 0x00, 0x00,                         // spatial quality
            0x07, (byte) 0x80,                              // width = 1920
            0x04, 0x38,                                     // height = 1080
            0x00, 0x48, 0x00, 0x00,                         // horizontal resolution
            0x00, 0x48, 0x00, 0x00,                         // vertical resolution
            0x00, 0x00, 0x00, 0x00,                         // reserved
            0x00, 0x01,                                     // frame count
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // compressor name (32 bytes)
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x18,                                     // depth = 24
            0x00, 0x00                                      // pre-defined
        };

        Metadata metadata = new Metadata();
        new Mp4VideoHandler(metadata, new Mp4Context())
            .processSampleDescription(new SequentialByteArrayReader(bytes));

        Mp4VideoDirectory directory = metadata.getFirstDirectoryOfType(Mp4VideoDirectory.class);
        assertNotNull(directory);
        assertEquals("avc1", directory.getString(Mp4VideoDirectory.TAG_COMPRESSION_TYPE_RAW));
    }

    @Test public void testSoundSampleEntryExposesRawFormat() throws Exception
    {
        // A minimal AudioSampleEntry (ISO/IEC 14496-12) carrying the 'mp4a' format code.
        final byte[] bytes = new byte[] {
            0x00, 0x00, 0x00, 0x00,                         // version + flags (skipped)
            0x00, 0x00, 0x00, 0x01,                         // entry count = 1
            0x00, 0x00, 0x00, 0x24,                         // sample description size
            0x6D, 0x70, 0x34, 0x61,                         // format = "mp4a"
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,             // reserved
            0x00, 0x01,                                     // data reference index
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // reserved
            0x00, 0x02,                                     // channel count = 2
            0x00, 0x10,                                     // sample size = 16
            0x00, 0x00,                                     // pre-defined
            0x00, 0x00,                                     // reserved
            0x00, 0x00, (byte) 0xAC, 0x44                   // sample rate = 44100
        };

        Metadata metadata = new Metadata();
        new Mp4SoundHandler(metadata, new Mp4Context())
            .processSampleDescription(new SequentialByteArrayReader(bytes));

        Mp4SoundDirectory directory = metadata.getFirstDirectoryOfType(Mp4SoundDirectory.class);
        assertNotNull(directory);
        assertEquals("mp4a", directory.getString(Mp4SoundDirectory.TAG_AUDIO_FORMAT_RAW));
    }
}
