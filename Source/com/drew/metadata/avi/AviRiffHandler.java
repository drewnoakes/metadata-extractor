/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.metadata.avi;

import com.drew.imaging.riff.RiffHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Implementation of {@link RiffHandler} specialising in AVI support.
 *
 * Extracts data from chunk/list types:
 *
 * <ul>
 *     <li><code>"avih"</code>: width, height, streams</li>
 *     <li><code>"strh"</code>: frames/second, samples/second, duration, video codec</li>
 * </ul>
 *
 * Sources: http://www.alexander-noe.com/video/documentation/avi.pdf
 *          https://msdn.microsoft.com/en-us/library/ms899422.aspx
 *          https://www.loc.gov/preservation/digital/formats/fdd/fdd000025.shtml
 *
 * @author Payton Garland
 */
public class AviRiffHandler implements RiffHandler
{
    @NotNull
    private final AviDirectory _directory;

//    @NotNull
//    private String _currentList = "";

    public AviRiffHandler(@NotNull Metadata metadata)
    {
        _directory = new AviDirectory();
        metadata.addDirectory(_directory);
    }

    public boolean shouldAcceptRiffIdentifier(@NotNull String identifier)
    {
        return identifier.equals(AviDirectory.FORMAT);
    }

    public boolean shouldAcceptChunk(@NotNull String fourCC)
    {
        return fourCC.equals(AviDirectory.CHUNK_STREAM_HEADER)
            || fourCC.equals(AviDirectory.CHUNK_MAIN_HEADER);
    }

    public boolean shouldAcceptList(@NotNull String fourCC)
    {
//        if (fourCC.equals(AviDirectory.LIST_HEADER) || fourCC.equals(AviDirectory.LIST_STREAM_HEADER) || fourCC.equals(AviDirectory.FORMAT)) {
//            _currentList = fourCC;
//        } else {
//            _currentList = "";
//        }
        return fourCC.equals(AviDirectory.LIST_HEADER)
            || fourCC.equals(AviDirectory.LIST_STREAM_HEADER)
            || fourCC.equals(AviDirectory.FORMAT);
    }

    public void processChunk(@NotNull String fourCC, @NotNull byte[] payload)
    {
        try {
            if (fourCC.equals(AviDirectory.CHUNK_STREAM_HEADER)) {
                ByteArrayReader reader = new ByteArrayReader(payload);
                reader.setMotorolaByteOrder(false);

                String fccType = new String(reader.getBytes(0, 4));
                String fccHandler = new String(reader.getBytes(4, 4));
//                int dwFlags = reader.getInt32(8);
//                int wPriority = reader.getInt16(12);
//                int wLanguage = reader.getInt16(14);
//                int dwInitialFrames = reader.getInt32(16);
                float dwScale = reader.getFloat32(20);
                float dwRate = reader.getFloat32(24);
//                int dwStart = reader.getInt32(28);
                int dwLength = reader.getInt32(32);
//                int dwSuggestedBufferSize = reader.getInt32(36);
//                int dwQuality = reader.getInt32(40);
//                int dwSampleSize = reader.getInt32(44);
//                byte[] rcFrame = reader.getBytes(48, 2);

                if (fccType.equals("vids")) {
                    if (!_directory.containsTag(AviDirectory.TAG_FRAMES_PER_SECOND)) {
                        _directory.setDouble(AviDirectory.TAG_FRAMES_PER_SECOND, (dwRate / dwScale));

                        double duration = dwLength / (dwRate / dwScale);
                        Integer hours = (int) duration / (int) (Math.pow(60, 2));
                        Integer minutes = ((int) duration / (int) (Math.pow(60, 1))) - (hours * 60);
                        Integer seconds = (int) Math.round((duration / (Math.pow(60, 0))) - (minutes * 60));
                        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

                        _directory.setString(AviDirectory.TAG_DURATION, time);
                        _directory.setString(AviDirectory.TAG_VIDEO_CODEC, fccHandler);
                    }
                } else if (fccType.equals("auds")) {
                    if (!_directory.containsTag(AviDirectory.TAG_SAMPLES_PER_SECOND)) {
                        _directory.setDouble(AviDirectory.TAG_SAMPLES_PER_SECOND, (dwRate / dwScale));
                    }
                }
            } else if (fourCC.equals(AviDirectory.CHUNK_MAIN_HEADER)) {
                ByteArrayReader reader = new ByteArrayReader(payload);
                reader.setMotorolaByteOrder(false);

//                int dwMicroSecPerFrame = reader.getInt32(0);
//                int dwMaxBytesPerSec = reader.getInt32(4);
//                int dwPaddingGranularity = reader.getInt32(8);
//                int dwFlags = reader.getInt32(12);
//                int dwTotalFrames = reader.getInt32(16);
//                int dwInitialFrames = reader.getInt32(20);
                int dwStreams = reader.getInt32(24);
//                int dwSuggestedBufferSize = reader.getInt32(28);
                int dwWidth = reader.getInt32(32);
                int dwHeight = reader.getInt32(36);
//                byte[] dwReserved = reader.getBytes(40, 4);

                _directory.setInt(AviDirectory.TAG_WIDTH, dwWidth);
                _directory.setInt(AviDirectory.TAG_HEIGHT, dwHeight);
                _directory.setInt(AviDirectory.TAG_STREAMS, dwStreams);
            }
        } catch (IOException ex) {
            _directory.addError(ex.getMessage());
        }
    }
}
