package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Drew Noakes
 * Date: Oct 10, 2003
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegCommentDirectory extends Directory {

	/** This is in bits/sample, usually 8 (12 and 16 not supported by most software). */
	public static final int TAG_JPEG_COMMENT = 0;

	protected static final HashMap tagNameMap = new HashMap();

	static {
        tagNameMap.put(new Integer(TAG_JPEG_COMMENT), "Jpeg Comment");
	}

    public JpegCommentDirectory() {
		this.setDescriptor(new JpegCommentDescriptor(this));
	}

	public String getName() {
		return "JpegComment";
	}

	protected HashMap getTagNameMap() {
		return tagNameMap;
	}
}
