package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Drew Noakes
 * Date: Oct 10, 2003
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegCommentDescriptor extends TagDescriptor
{
    public JpegCommentDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }
}
