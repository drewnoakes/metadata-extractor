// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////

package com.drew.metadata.fuzzer;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.imaging.quicktime.QuickTimeMetadataReader;
import com.drew.imaging.webp.WebpMetadataReader;
import com.drew.imaging.heif.HeifMetadataReader;
import com.drew.metadata.Metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * High-coverage fuzzer that steers inputs by prepending common format
 * magic/signature bytes to help bypass file-type detection and reach
 * deep format-specific readers.
 */
public class HighCoverageFuzzer {
    private static final Map<String, byte[]> SIGS = new HashMap<>();

    static {
        SIGS.put("mp4", new byte[]{'f', 't', 'y', 'p'});
        SIGS.put("webp", new byte[]{'R', 'I', 'F', 'F'});
        SIGS.put("heif", new byte[]{'f', 't', 'y', 'p'}); // heif sits in ftyp/brand space
        SIGS.put("qt", new byte[]{0x00, 0x00, 0x00, 0x18, 'f', 't', 'y', 'p'});
        SIGS.put("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        byte[] tail = data.consumeRemainingAsBytes();
        if (tail.length < 4) {
            return;
        }

        try {
            int choice = data.consumeInt(0, 9);

            byte[] input;
            Metadata metadata = null;
            if (choice < 6) {
                // Generic path: sometimes prepend JPEG signature to get past detectors
                if (data.consumeBoolean()) {
                    input = FuzzInputBuilder.prepend(SIGS.get("jpg"), tail);
                } else {
                    input = tail;
                }
                metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(input));
            } else {
                switch (choice) {
                    case 6:
                        input = FuzzInputBuilder.prepend(SIGS.get("webp"), tail);
                        metadata = WebpMetadataReader.readMetadata(new ByteArrayInputStream(input));
                        break;
                    case 7:
                        input = FuzzInputBuilder.prepend(SIGS.get("heif"), tail);
                        metadata = HeifMetadataReader.readMetadata(new ByteArrayInputStream(input));
                        break;
                    case 8:
                        input = FuzzInputBuilder.prepend(SIGS.get("qt"), tail);
                        metadata = QuickTimeMetadataReader.readMetadata(new ByteArrayInputStream(input));
                        break;
                    case 9:
                        input = FuzzInputBuilder.prepend(SIGS.get("mp4"), tail);
                        metadata = Mp4MetadataReader.readMetadata(new ByteArrayInputStream(input));
                        break;
                    default:
                        // Defensive fallback
                        metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(tail));
                }
            }

            if (metadata != null) {
                FuzzInputBuilder.touchMetadata(metadata);
            }
        } catch (IOException | ImageProcessingException e) {
            // Expected parsing errors
        } catch (Exception e) {
            // Unexpected – Jazzer will record the input in this case
        }
    }
}
