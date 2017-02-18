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
package com.drew.imaging.png;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChromaticities
{
    private final int _whitePointX;
    private final int _whitePointY;
    private final int _redX;
    private final int _redY;
    private final int _greenX;
    private final int _greenY;
    private final int _blueX;
    private final int _blueY;

    public PngChromaticities(@NotNull byte[] bytes) throws PngProcessingException
    {
        if (bytes.length != 8 * 4) {
            throw new PngProcessingException("Invalid number of bytes");
        }

        SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
        try {
            _whitePointX = reader.getInt32();
            _whitePointY = reader.getInt32();
            _redX = reader.getInt32();
            _redY = reader.getInt32();
            _greenX = reader.getInt32();
            _greenY = reader.getInt32();
            _blueX = reader.getInt32();
            _blueY = reader.getInt32();
        } catch (IOException ex) {
            throw new PngProcessingException(ex);
        }
    }

    public int getWhitePointX()
    {
        return _whitePointX;
    }

    public int getWhitePointY()
    {
        return _whitePointY;
    }

    public int getRedX()
    {
        return _redX;
    }

    public int getRedY()
    {
        return _redY;
    }

    public int getGreenX()
    {
        return _greenX;
    }

    public int getGreenY()
    {
        return _greenY;
    }

    public int getBlueX()
    {
        return _blueX;
    }

    public int getBlueY()
    {
        return _blueY;
    }
}
