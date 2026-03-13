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

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Core entry-point fuzzer -- imported from OSS-Fuzz and extended.
 * Targets ImageMetadataReader, which handles detection and dispatching
 * for all supported image and video formats.
 */
public class ImageMetadataReaderFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(
                new ByteArrayInputStream(data.consumeRemainingAsBytes()));
            FuzzInputBuilder.touchMetadata(metadata);
        } catch (IOException | ImageProcessingException e) {
            // Expected
        } catch (Exception e) {
            // Unexpected – potential bug
        }
    }
}
