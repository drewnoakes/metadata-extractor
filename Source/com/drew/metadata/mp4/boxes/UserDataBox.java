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
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.drew.metadata.mp4.Mp4Directory.TAG_LATITUDE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_LONGITUDE;

public class UserDataBox extends FullBox {

    private static final String LOCATION_CODE = "©xyz";

    private static final Pattern COORDINATE_PATTERN = Pattern.compile("([+-]\\d+\\.\\d+)([+-]\\d+\\.\\d+)");

    private String coordinateString;

    public UserDataBox(final SequentialReader reader, final Box box) throws IOException {
        super(reader, box);

        final String fourCC = getFourCc(reader);

        if (LOCATION_CODE.equals(fourCC)) {
            extractLocation(reader);
        }
    }

    private String getFourCc(final SequentialReader reader) throws IOException {
        final byte[] codeBytes = reader.getBytes(4);
        return new String(codeBytes, "ISO-8859-1");
    }

    private void extractLocation(final SequentialReader reader) throws IOException {
        final int length = reader.getUInt16();
        reader.skip(2);
        final byte[] bytes = reader.getBytes(length);
        coordinateString = new String(bytes, "UTF-8");
    }

    public void addMetadata(final Mp4Directory directory) {
        final Matcher matcher = COORDINATE_PATTERN.matcher(coordinateString);
        if (matcher.find()) {
            final double latitude = Double.parseDouble(matcher.group(1));
            final double longitude = Double.parseDouble(matcher.group(2));

            directory.setDouble(TAG_LATITUDE, latitude);
            directory.setDouble(TAG_LONGITUDE, longitude);
        }
    }
}
