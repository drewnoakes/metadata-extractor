package com.drew.metadata.jpeg;

import com.drew.metadata.MetadataException;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: dnoakes
 * Date: 09-Oct-2003
 * Time: 17:04:07
 * To change this template use Options | File Templates.
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
