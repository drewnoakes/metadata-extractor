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
 * Created by dnoakes on 29-Nov-2002 09:07:43 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * A mock implementation of Directory used in unit testing.
 */
public class MockDirectory extends Directory
{
    private final HashMap _tagNameMap;

    public MockDirectory()
    {
        this._tagNameMap = new HashMap();
    }

    public String getName()
    {
        return "";
    }

    protected HashMap getTagNameMap()
    {
        return _tagNameMap;
    }
}
