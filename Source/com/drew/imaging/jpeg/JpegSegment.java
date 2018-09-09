/*
 * Copyright 2018 kevinm.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.RandomAccessStream;
import com.drew.lang.ReaderInfo;

import java.io.IOException;
/**
 *
 * @author Kevin Mott https://github.com/kwhopper
 */
public class JpegSegment
{
    private JpegSegmentType _type;
    private String _preamble;
    private ReaderInfo _reader;
    private byte _byteMarker;
    
    @NotNull
    public JpegSegmentType getType()
    {
        return _type;
    }
    
    @NotNull
    public String getPreamble()
    {
        return _preamble;
    }
    
    @NotNull
    public ReaderInfo getReader()
    {
        return _reader;
    }
    
    @NotNull
    public byte getByteMarker()
    {
        return _byteMarker;
    }

    public JpegSegment(JpegSegmentType type, @NotNull ReaderInfo segmentReader)
    {
        this(type, segmentReader, "");
    }

    public JpegSegment(JpegSegmentType type, @NotNull ReaderInfo segmentReader, @NotNull String preamble)
    {
        this(type, segmentReader, preamble, (byte)0x00);
    }

    public JpegSegment(JpegSegmentType type, @NotNull ReaderInfo segmentReader, @NotNull String preamble, @NotNull byte byteMarker)
    {
        _type = type;
        _reader = segmentReader;
        _preamble = preamble;
        _byteMarker = byteMarker;
    }
    
    private byte[] asByteArray = null;
    public void holdAsBytes() throws IOException
    {
        if(asByteArray == null)
        {
            asByteArray = getReader().toArray();
            _reader = new ReaderInfo(new RandomAccessStream(asByteArray));
        }
    }
}
