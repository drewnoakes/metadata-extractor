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
 * Created on Aug 2, 2003.
 */
package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.util.HashMap;

/**
 * Directory of tags and values for the SOF0 Jpeg segment.  This segment holds basic metadata about the image.
 * @author Darrell Silver http://www.darrellsilver.com and Drew Noakes
 */
public class JpegDirectory extends Directory {

	/** This is in bits/sample, usually 8 (12 and 16 not supported by most software). */
	public static final int TAG_JPEG_DATA_PRECISION = 0;
	/** The image's height.  Necessary for decoding the image, so it should always be there. */
	public static final int TAG_JPEG_IMAGE_HEIGHT = 1;
	/** The image's width.  Necessary for decoding the image, so it should always be there. */
	public static final int TAG_JPEG_IMAGE_WIDTH = 3;
	/** Usually 1 = grey scaled, 3 = color YcbCr or YIQ, 4 = color CMYK
	 * Each component TAG_COMPONENT_DATA_[1-4], has the following meaning:
	 * component Id(1byte)(1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q),
	 * sampling factors (1byte) (bit 0-3 vertical., 4-7 horizontal.),
	 * quantization table number (1 byte).
	 * <p>
	 * This info is from http://www.funducode.com/freec/Fileformats/format3/format3b.htm
	 */
	public static final int TAG_JPEG_NUMBER_OF_COMPONENTS = 5;

    // NOTE!  Component tag type int values must increment in steps of 1

	/** the first of a possible 4 color components.  Number of components specified in TAG_JPEG_NUMBER_OF_COMPONENTS.*/
	public static final int TAG_JPEG_COMPONENT_DATA_1 = 6;
	/** the second of a possible 4 color components.  Number of components specified in TAG_JPEG_NUMBER_OF_COMPONENTS.*/
	public static final int TAG_JPEG_COMPONENT_DATA_2 = 7;
	/** the third of a possible 4 color components.  Number of components specified in TAG_JPEG_NUMBER_OF_COMPONENTS.*/
	public static final int TAG_JPEG_COMPONENT_DATA_3 = 8;
	/** the fourth of a possible 4 color components.  Number of components specified in TAG_JPEG_NUMBER_OF_COMPONENTS.*/
	public static final int TAG_JPEG_COMPONENT_DATA_4 = 9;

	protected static final HashMap tagNameMap = new HashMap();

	static {
        tagNameMap.put(new Integer(TAG_JPEG_DATA_PRECISION), "Data Precision");
        tagNameMap.put(new Integer(TAG_JPEG_IMAGE_WIDTH), "Image Width");
        tagNameMap.put(new Integer(TAG_JPEG_IMAGE_HEIGHT), "Image Height");
		tagNameMap.put(new Integer(TAG_JPEG_NUMBER_OF_COMPONENTS), "Number of Components");
		tagNameMap.put(new Integer(TAG_JPEG_COMPONENT_DATA_1), "Component 1");
		tagNameMap.put(new Integer(TAG_JPEG_COMPONENT_DATA_2), "Component 2");
		tagNameMap.put(new Integer(TAG_JPEG_COMPONENT_DATA_3), "Component 3");
		tagNameMap.put(new Integer(TAG_JPEG_COMPONENT_DATA_4), "Component 4");
	}

    public JpegDirectory() {
		this.setDescriptor(new JpegDescriptor(this));
	}

	public String getName() {
		return "Jpeg";
	}

	protected HashMap getTagNameMap() {
		return tagNameMap;
	}

    /**
     * 
     * @param componentNumber The zero-based index of the component.  This number is normally between 0 and 3.
     *        Use getNumberOfComponents for bounds-checking.
     * @return the JpegComponent having the specified number.
     */
    public JpegComponent getComponent(int componentNumber)
    {
        int tagType = JpegDirectory.TAG_JPEG_COMPONENT_DATA_1 + componentNumber;

        JpegComponent component = (JpegComponent)getObject(tagType);

        return component;
    }

    public int getImageWidth() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH);
    }

    public int getImageHeight() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT);
    }

    public int getNumberOfComponents() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS);
    }
}
