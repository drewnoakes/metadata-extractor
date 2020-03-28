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
package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import java.awt.*;
import java.io.IOException;

/**
 * Extracts rotation information only
 *
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-SW34
 *
 * @author Ferenc Balogh
 */
public class TrackHeaderAtom extends FullAtom {
    int[] matrix = new int[9];
    long width;
    long height;

    public TrackHeaderAtom(SequentialReader reader, Atom atom) throws IOException {
        super(reader, atom);

        if (version == 1) {
            reader.skip(48);
        } else {
            reader.skip(36);
        }

        for (int i = 0; i < 9; i++) {
            matrix[i] = reader.getInt32();
        }
        width = reader.getInt32();
        height = reader.getInt32();
    }

    public void addMetadata(QuickTimeDirectory directory) {
        if (width != 0 && height != 0 && directory.getDoubleObject(Mp4Directory.TAG_ROTATION) == null) {
            Point p = new Point(matrix[1] + matrix[4], matrix[0] + matrix[3]);
            double theta = Math.atan2(p.y, p.x);
            double degree = Math.toDegrees(theta);
            degree -= 45;
            directory.setDouble(QuickTimeDirectory.TAG_ROTATION, Math.abs(degree));
        }
    }
}
