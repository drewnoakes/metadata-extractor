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
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngHeader
{
    private int _imageWidth;
    private int _imageHeight;
    private byte _bitsPerSample;
    @NotNull
    private PngColorType _colorType;
    private byte _compressionType;
    private byte _filterMethod;
    private byte _interlaceMethod;

    public PngHeader(@NotNull byte[] bytes) throws PngProcessingException
    {
        if (bytes.length != 13) {
            throw new PngProcessingException("PNG header chunk must have 13 data bytes");
        }
        SequentialReader reader = new SequentialByteArrayReader(bytes);
        try {
            _imageWidth = reader.getInt32();
            _imageHeight = reader.getInt32();
            _bitsPerSample = reader.getInt8();
            byte colorTypeNumber = reader.getInt8();
            PngColorType colorType = PngColorType.fromNumericValue(colorTypeNumber);
            if (colorType == null) {
                throw new PngProcessingException("Unexpected PNG color type: " + colorTypeNumber);
            }
            _colorType = colorType;
            _compressionType = reader.getInt8();
            _filterMethod = reader.getInt8();
            _interlaceMethod = reader.getInt8();
        } catch (IOException e) {
            // Should never happen
            throw new PngProcessingException(e);
        }
    }

    public int getImageWidth()
    {
        return _imageWidth;
    }

    public int getImageHeight()
    {
        return _imageHeight;
    }

    public byte getBitsPerSample()
    {
        return _bitsPerSample;
    }

    @NotNull
    public PngColorType getColorType()
    {
        return _colorType;
    }

    public byte getCompressionType()
    {
        return _compressionType;
    }

    public byte getFilterMethod()
    {
        return _filterMethod;
    }

    public byte getInterlaceMethod()
    {
        return _interlaceMethod;
    }
}
