package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Darrell Silver www.darrellsilver.com
 * Date: Aug 2, 2003
 * @author Darrell Silver http://www.darrellsilver.com
 */
public class JpegDescriptor extends TagDescriptor
{
    public JpegDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_1:
                return getComponentDataDescription(0);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_2:
                return getComponentDataDescription(1);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_3:
                return getComponentDataDescription(2);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_4:
                return getComponentDataDescription(3);
            case JpegDirectory.TAG_JPEG_DATA_PRECISION:
                return getDataPrecisionDescription();
            case JpegDirectory.TAG_JPEG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case JpegDirectory.TAG_JPEG_IMAGE_WIDTH:
                return getImageWidthDescription();
        }

        return _directory.getString(tagType);
    }

    public String getImageWidthDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_IMAGE_WIDTH) + " pixels";
    }

    public String getImageHeightDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT) + " pixels";
    }

    public String getDataPrecisionDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_DATA_PRECISION) + " bits";
    }

    public String getComponentDataDescription(int componentNumber) throws MetadataException
    {
        JpegComponent component = ((JpegDirectory)_directory).getComponent(componentNumber);
        if (component==null)
        {
            throw new MetadataException("No Jpeg component exists with number " + componentNumber);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(component.getComponentName());
        sb.append(" component: Quantization table ");
        sb.append(component.getQuantizationTableNumber());
        sb.append(", Sampling factors ");
        sb.append(component.getHorizontalSamplingFactor());
        sb.append(" horiz/");
        sb.append(component.getVerticalSamplingFactor());
        sb.append(" vert");
        return sb.toString();
    }
}
