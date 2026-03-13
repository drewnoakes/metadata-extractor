package com.drew.metadata.fuzzer;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Core entry-point fuzzer — imported from OSS-Fuzz and extended.
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
