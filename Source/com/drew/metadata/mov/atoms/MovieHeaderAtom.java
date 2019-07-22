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

import com.drew.lang.DateUtil;
import com.drew.lang.Rational;
import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QuickTimeDirectory;
import java.io.IOException;
import static com.drew.metadata.mov.QuickTimeDirectory.*;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFGJG
 *
 * @author Payton Garland
 */
public class MovieHeaderAtom extends FullAtom
{
    private long creationTime;
    private long modificationTime;
    private long timescale;
    private long duration;
    private int preferredRate;
    private int preferredVolume;
    // TODO this matrix data is not currently used anywhere
    private int[] matrixStructure;
    private long previewTime;
    private long previewDuration;
    private long posterTime;
    private long selectionTime;
    private long selectionDuration;
    private long currentTime;
    private long nextTrackID;

    public MovieHeaderAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        creationTime = reader.getUInt32();
        modificationTime = reader.getUInt32();
        timescale = reader.getUInt32();
        duration = reader.getUInt32();
        preferredRate = reader.getInt32();
        preferredVolume = reader.getInt16();
        reader.skip(10); // Reserved
        matrixStructure = new int[]{
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        previewTime = reader.getUInt32();
        previewDuration = reader.getUInt32();
        posterTime = reader.getUInt32();
        selectionTime = reader.getUInt32();
        selectionDuration = reader.getUInt32();
        currentTime = reader.getUInt32();
        nextTrackID = reader.getUInt32();
    }

    public void addMetadata(QuickTimeDirectory directory)
    {
        // Get creation/modification times
        directory.setDate(TAG_CREATION_TIME, DateUtil.get1Jan1904EpochDate(creationTime));
        directory.setDate(TAG_MODIFICATION_TIME, DateUtil.get1Jan1904EpochDate(modificationTime));

        // Get duration and time scale
        directory.setLong(TAG_DURATION, duration);
        directory.setLong(TAG_TIME_SCALE, timescale);
        directory.setRational(TAG_DURATION_SECONDS, new Rational(duration, timescale));

        // Calculate preferred rate fixed point
        double preferredRateInteger = (preferredRate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (preferredRate & 0x0000FFFF) / 16.0d;
        directory.setDouble(TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        double preferredVolumeInteger = (preferredVolume & 0xFF00) >> 8;
        double preferredVolumeFraction = (preferredVolume & 0x00FF) / 8.0d;
        directory.setDouble(TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        directory.setLong(TAG_PREVIEW_TIME, previewTime);
        directory.setLong(TAG_PREVIEW_DURATION, previewDuration);
        directory.setLong(TAG_POSTER_TIME, posterTime);
        directory.setLong(TAG_SELECTION_TIME, selectionTime);
        directory.setLong(TAG_SELECTION_DURATION, selectionDuration);
        directory.setLong(TAG_CURRENT_TIME, currentTime);
        directory.setLong(TAG_NEXT_TRACK_ID, nextTrackID);
    }
}
