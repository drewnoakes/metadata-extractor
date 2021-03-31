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
package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.drew.metadata.mp4.Mp4Directory.TAG_LATITUDE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_LONGITUDE;

public class UserDataBox extends Box {

    private static final int LOCATION_CODE = 0xA978797A; // "©xyz"

    private static final Pattern COORDINATE_PATTERN = Pattern.compile("([+-]\\d+\\.\\d+)([+-]\\d+\\.\\d+)");

    @Nullable
    private String coordinateString;

    public UserDataBox(@NotNull final SequentialReader reader, @NotNull final Box box, int length) throws IOException {
        super(box);

        while (reader.getPosition() < length) {
            long size = reader.getUInt32();
            if (size <= 4)
                break;
            int kind = reader.getInt32();
            if (kind == LOCATION_CODE) {
                int xyzLength = reader.getUInt16();
                reader.skip(2);
                coordinateString = reader.getString(xyzLength, "UTF-8");
            } else if (size >= 8) {
                reader.skip(size - 8);
            } else {
                return;
            }
        }
    }

    public void addMetadata(final Mp4Directory directory) {
        if (coordinateString != null) {
            final Matcher matcher = COORDINATE_PATTERN.matcher(coordinateString);
            if (matcher.find()) {
                final double latitude = Double.parseDouble(matcher.group(1));
                final double longitude = Double.parseDouble(matcher.group(2));

                directory.setDouble(TAG_LATITUDE, latitude);
                directory.setDouble(TAG_LONGITUDE, longitude);
            }
        }
    }
}
