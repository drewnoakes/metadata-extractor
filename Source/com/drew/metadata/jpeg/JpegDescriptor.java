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
 */
package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JpegDirectory.
 * Thanks to Darrell Silver (www.darrellsilver.com) for the initial version of this class.
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
            throw new MetadataException("No Jpeg component exists with number " + componentNumber);

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
