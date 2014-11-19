package com.drew.metadata.gif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class GifHeaderDescriptor extends TagDescriptor<GifHeaderDirectory>
{
    public GifHeaderDescriptor(@NotNull GifHeaderDirectory directory)
    {
        super(directory);
    }

//    @Override
//    public String getDescription(int tagType)
//    {
//        switch (tagType) {
//            case GifHeaderDirectory.TAG_COMPRESSION:
//                return getCompressionDescription();
//            default:
//                return super.getDescription(tagType);
//        }
//    }
}
