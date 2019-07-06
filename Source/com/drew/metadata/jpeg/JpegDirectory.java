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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.util.HashMap;

/**
 * Directory of tags and values for the SOF0 JPEG segment.  This segment holds basic metadata about the image.
 *
 * @author Darrell Silver http://www.darrellsilver.com and Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class JpegDirectory extends Directory
{
    public static final int TAG_COMPRESSION_TYPE = -3;
    /** This is in bits/sample, usually 8 (12 and 16 not supported by most software). */
    public static final int TAG_DATA_PRECISION = 0;
    /** The image's height.  Necessary for decoding the image, so it should always be there. */
    public static final int TAG_IMAGE_HEIGHT = 1;
    /** The image's width.  Necessary for decoding the image, so it should always be there. */
    public static final int TAG_IMAGE_WIDTH = 3;
    /**
     * Usually 1 = grey scaled, 3 = color YcbCr or YIQ, 4 = color CMYK
     * Each component TAG_COMPONENT_DATA_[1-4], has the following meaning:
     * component Id(1byte)(1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q),
     * sampling factors (1byte) (bit 0-3 vertical., 4-7 horizontal.),
     * quantization table number (1 byte).
     * <p>
     * This info is from http://www.funducode.com/freec/Fileformats/format3/format3b.htm
     */
    public static final int TAG_NUMBER_OF_COMPONENTS = 5;

    // NOTE!  Component tag type int values must increment in steps of 1

    /** the first of a possible 4 color components.  Number of components specified in TAG_NUMBER_OF_COMPONENTS. */
    public static final int TAG_COMPONENT_DATA_1 = 6;
    /** the second of a possible 4 color components.  Number of components specified in TAG_NUMBER_OF_COMPONENTS. */
    public static final int TAG_COMPONENT_DATA_2 = 7;
    /** the third of a possible 4 color components.  Number of components specified in TAG_NUMBER_OF_COMPONENTS. */
    public static final int TAG_COMPONENT_DATA_3 = 8;
    /** the fourth of a possible 4 color components.  Number of components specified in TAG_NUMBER_OF_COMPONENTS. */
    public static final int TAG_COMPONENT_DATA_4 = 9;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_COMPRESSION_TYPE, "Compression Type");
        _tagNameMap.put(TAG_DATA_PRECISION, "Data Precision");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_NUMBER_OF_COMPONENTS, "Number of Components");
        _tagNameMap.put(TAG_COMPONENT_DATA_1, "Component 1");
        _tagNameMap.put(TAG_COMPONENT_DATA_2, "Component 2");
        _tagNameMap.put(TAG_COMPONENT_DATA_3, "Component 3");
        _tagNameMap.put(TAG_COMPONENT_DATA_4, "Component 4");
    }

    public JpegDirectory()
    {
        this.setDescriptor(new JpegDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "JPEG";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * @param componentNumber The zero-based index of the component.  This number is normally between 0 and 3.
     *                        Use getNumberOfComponents for bounds-checking.
     * @return the JpegComponent having the specified number.
     */
    @Nullable
    public JpegComponent getComponent(int componentNumber)
    {
        int tagType = JpegDirectory.TAG_COMPONENT_DATA_1 + componentNumber;
        return (JpegComponent)getObject(tagType);
    }

    public int getImageWidth() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_IMAGE_WIDTH);
    }

    public int getImageHeight() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_IMAGE_HEIGHT);
    }

    public int getNumberOfComponents() throws MetadataException
    {
        return getInt(JpegDirectory.TAG_NUMBER_OF_COMPONENTS);
    }
}
