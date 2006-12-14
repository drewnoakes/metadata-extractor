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
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Kodak cameras.
 */
public class KodakMakernoteDirectory extends Directory
{
	protected static final HashMap _tagNameMap = new HashMap();
	
	public String getName()
    {
		return "Kodak Makernote";
	}

	protected HashMap getTagNameMap()
    {
		return _tagNameMap;
	}
}
