/*
 * Created by dnoakes on 21-Nov-2002 17:58:19 using IntelliJ IDEA.
 */
package com.drew.metadata.iptc;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

/**
 *
 */
public class IptcDescriptor extends TagDescriptor
{
    public IptcDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }
}
