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

/**
 * ISO/IED 14496-12:2005 pg.17-18
 */
public class TrackHeaderBox extends FullBox
{
    long creationTime;
    long modificationTime;
    long trackID;
    long duration;
    int layer;
    int alternateGroup;
    int volume;
    int[] matrix = new int[9];
    long width;
    long height;

    public TrackHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            trackID = reader.getInt32();
            reader.skip(4); // reserved
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            trackID = reader.getUInt32();
            reader.skip(4);
            duration = reader.getUInt32();
        }
        reader.skip(8); // reserved
        layer = reader.getInt16();
        alternateGroup = reader.getInt16();
        volume = reader.getInt16();
        reader.skip(2); // reserved
        for (int i = 0; i < 9; i++) {
            matrix[i] = reader.getInt32();
        }
        width = reader.getInt32();
        height = reader.getInt32();
    }

    public void addMetadata(Mp4Directory directory)
    {
        if (width != 0 && height != 0 && directory.getDoubleObject(Mp4Directory.TAG_ROTATION) == null) {
            int x = matrix[1] + matrix[4];
            int y = matrix[0] + matrix[3];
            double theta = Math.atan2(y, x);
            double degree = Math.toDegrees(theta);
            degree -= 45;
            directory.setDouble(Mp4Directory.TAG_ROTATION, Math.abs(degree));
        }
    }
}
