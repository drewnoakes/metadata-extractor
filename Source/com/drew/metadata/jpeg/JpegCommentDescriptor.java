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
 * Created by dnoakes on Oct 10, 2003 using IntelliJ IDEA.
 */
package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>JpegCommentDirectory</code>.
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

    public String getJpegCommentDescription()
    {
        return _directory.getString(JpegCommentDirectory.TAG_JPEG_COMMENT);
    }
}
