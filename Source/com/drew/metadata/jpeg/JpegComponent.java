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
package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;

import java.io.Serializable;

/**
 * Stores information about a JPEG image component such as the component id, horiz/vert sampling factor and
 * quantization table number.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegComponent implements Serializable
{
    private static final long serialVersionUID = 61121257899091914L;

    private final int _componentId;
    private final int _samplingFactorByte;
    private final int _quantizationTableNumber;

    public JpegComponent(int componentId, int samplingFactorByte, int quantizationTableNumber)
    {
        _componentId = componentId;
        _samplingFactorByte = samplingFactorByte;
        _quantizationTableNumber = quantizationTableNumber;
    }

    public int getComponentId()
    {
        return _componentId;
    }

    /**
     * Returns the component name (one of: Y, Cb, Cr, I, or Q)
     * @return the component name
     */
    @NotNull
    public String getComponentName()
    {
        switch (_componentId)
        {
            case 1:
                return "Y";
            case 2:
                return "Cb";
            case 3:
                return "Cr";
            case 4:
                return "I";
            case 5:
                return "Q";
            default:
                return String.format("Unknown (%s)", _componentId);
        }
    }

    public int getQuantizationTableNumber()
    {
        return _quantizationTableNumber;
    }

    public int getHorizontalSamplingFactor()
    {
        return (_samplingFactorByte>>4) & 0x0F;
    }

    public int getVerticalSamplingFactor()
    {
        return _samplingFactorByte & 0x0F;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format(
            "Quantization table %d, Sampling factors %d horiz/%d vert",
            _quantizationTableNumber,
            getHorizontalSamplingFactor(),
            getVerticalSamplingFactor()
        );
    }
}
