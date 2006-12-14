/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on Oct 9, 17:04:07 using IntelliJ IDEA.
 */
package com.drew.metadata.jpeg;

import com.drew.metadata.MetadataException;

import java.io.Serializable;

/**
 * Stores information about a Jpeg image component such as the component id, horiz/vert sampling factor and
 * quantization table number.
 */
public class JpegComponent implements Serializable
{
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
     * @throws MetadataException if the component id is not known
     */
    public String getComponentName() throws MetadataException
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
        }

        throw new MetadataException("Unsupported component id: " + _componentId);
    }

    public int getQuantizationTableNumber()
    {
        return _quantizationTableNumber;
    }

    public int getHorizontalSamplingFactor()
    {
        return _samplingFactorByte & 0x0F;
    }

    public int getVerticalSamplingFactor()
    {
        return (_samplingFactorByte>>4) & 0x0F;
    }
}
