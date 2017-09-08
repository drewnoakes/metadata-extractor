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

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds basic metadata from Avi files
 *
 * @author Payton Garland
 */
public class AviDirectory extends Directory
{

    public static final int TAG_FRAMES_PER_SECOND = 1;
    public static final int TAG_SAMPLES_PER_SECOND = 2;
    public static final int TAG_DURATION = 3;
    public static final int TAG_VIDEO_CODEC = 4;
    public static final int TAG_AUDIO_CODEC = 5;
    public static final int TAG_WIDTH = 6;
    public static final int TAG_HEIGHT = 7;
    public static final int TAG_STREAMS = 8;

    public static final String CHUNK_STREAM_HEADER = "strh";
    public static final String CHUNK_MAIN_HEADER = "avih";

    public static final String LIST_HEADER = "hdrl";
    public static final String LIST_STREAM_HEADER = "strl";

    public static final String FORMAT = "AVI ";

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FRAMES_PER_SECOND, "Frames Per Second");
        _tagNameMap.put(TAG_SAMPLES_PER_SECOND, "Samples Per Second");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_VIDEO_CODEC, "Video Codec");
        _tagNameMap.put(TAG_AUDIO_CODEC, "Audio Codec");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_STREAMS, "Stream Count");
    }

    public AviDirectory()
    {
        this.setDescriptor(new AviDescriptor(this));
    }

    @NotNull
    @Override
    public String getName() {
        return "AVI";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
