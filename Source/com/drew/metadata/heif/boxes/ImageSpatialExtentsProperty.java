/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.MetadataException;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * ISO/IEC 23008-12:2017 pg.11
 */
public class ImageSpatialExtentsProperty extends FullBox
{
    long width;
    long height;

    public ImageSpatialExtentsProperty(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        width = reader.getUInt32();
        height = reader.getUInt32();
    }

    public void addMetadata(HeifDirectory directory)
    {
        if (directory.containsTag(HeifDirectory.TAG_IMAGE_WIDTH) && directory.containsTag(HeifDirectory.TAG_IMAGE_HEIGHT)) {
            // There may be several images in the HEIF file, what we'd like to return is the width and height
            // of the "main" image.
            // We assume that the main image is the biggest image, so we want to take the max width & height.
            // While it is not guaranteed to be correct, this is a pretty safe bet and seems to be validated
            // by actual images found in the wild (additional images in practice are often thumbnails or overlays,
            // that are going to be smaller that the main image).
            // Note that here we also assume that if width is bigger than the preexisting value, so is height.
            try {
                directory.setLong(
                    HeifDirectory.TAG_IMAGE_WIDTH,
                    Math.max(directory.getInt(HeifDirectory.TAG_IMAGE_WIDTH), width)
                );
                directory.setLong(
                    HeifDirectory.TAG_IMAGE_HEIGHT,
                    Math.max(directory.getInt(HeifDirectory.TAG_IMAGE_HEIGHT), height)
                );
            } catch(MetadataException ex) {
                // We could not read width and height tags as int values (unexpected type ?).
                // We just ignore this. It should never ever happen given that we always set those to int values.
            }
        } else {
            directory.setLong(HeifDirectory.TAG_IMAGE_WIDTH, width);
            directory.setLong(HeifDirectory.TAG_IMAGE_HEIGHT, height);
        }
    }
}
