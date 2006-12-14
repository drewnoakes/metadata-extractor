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
 * Created by dnoakes on 26-Nov-2002 01:26:39 using IntelliJ IDEA.
 */
package com.drew.metadata.iptc;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags used by the International Press Telecommunications Council (IPTC) metadata format.
 */
public class IptcDirectory extends Directory
{
    public static final int TAG_RECORD_VERSION = 0x0200;
    public static final int TAG_CAPTION = 0x0278;
    public static final int TAG_WRITER = 0x027a;
    public static final int TAG_HEADLINE = 0x0269;
    public static final int TAG_SPECIAL_INSTRUCTIONS = 0x0228;
    public static final int TAG_BY_LINE = 0x0250;
    public static final int TAG_BY_LINE_TITLE = 0x0255;
    public static final int TAG_CREDIT = 0x026e;
    public static final int TAG_SOURCE = 0x0273;
    public static final int TAG_OBJECT_NAME = 0x0205;
    public static final int TAG_DATE_CREATED = 0x0237;
    public static final int TAG_CITY = 0x025a;
    public static final int TAG_PROVINCE_OR_STATE = 0x025f;
    public static final int TAG_COUNTRY_OR_PRIMARY_LOCATION = 0x0265;
    public static final int TAG_ORIGINAL_TRANSMISSION_REFERENCE = 0x0267;
    public static final int TAG_CATEGORY = 0x020f;
    public static final int TAG_SUPPLEMENTAL_CATEGORIES = 0x0214;
    public static final int TAG_URGENCY = 0x0200 | 10;
    public static final int TAG_KEYWORDS = 0x0200 | 25;
    public static final int TAG_COPYRIGHT_NOTICE = 0x0274;
    public static final int TAG_RELEASE_DATE = 0x0200 | 30;
    public static final int TAG_RELEASE_TIME = 0x0200 | 35;
    public static final int TAG_TIME_CREATED = 0x0200 | 60;
    public static final int TAG_ORIGINATING_PROGRAM = 0x0200 | 65;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_RECORD_VERSION), "Directory Version");
        tagNameMap.put(new Integer(TAG_CAPTION), "Caption/Abstract");
        tagNameMap.put(new Integer(TAG_WRITER), "Writer/Editor");
        tagNameMap.put(new Integer(TAG_HEADLINE), "Headline");
        tagNameMap.put(new Integer(TAG_SPECIAL_INSTRUCTIONS), "Special Instructions");
        tagNameMap.put(new Integer(TAG_BY_LINE), "By-line");
        tagNameMap.put(new Integer(TAG_BY_LINE_TITLE), "By-line Title");
        tagNameMap.put(new Integer(TAG_CREDIT), "Credit");
        tagNameMap.put(new Integer(TAG_SOURCE), "Source");
        tagNameMap.put(new Integer(TAG_OBJECT_NAME), "Object Name");
        tagNameMap.put(new Integer(TAG_DATE_CREATED), "Date Created");
        tagNameMap.put(new Integer(TAG_CITY), "City");
        tagNameMap.put(new Integer(TAG_PROVINCE_OR_STATE), "Province/State");
        tagNameMap.put(new Integer(TAG_COUNTRY_OR_PRIMARY_LOCATION), "Country/Primary Location");
        tagNameMap.put(new Integer(TAG_ORIGINAL_TRANSMISSION_REFERENCE), "Original Transmission Reference");
        tagNameMap.put(new Integer(TAG_CATEGORY), "Category");
        tagNameMap.put(new Integer(TAG_SUPPLEMENTAL_CATEGORIES), "Supplemental Category(s)");
        tagNameMap.put(new Integer(TAG_URGENCY), "Urgency");
        tagNameMap.put(new Integer(TAG_KEYWORDS), "Keywords");
        tagNameMap.put(new Integer(TAG_COPYRIGHT_NOTICE), "Copyright Notice");
        tagNameMap.put(new Integer(TAG_RELEASE_DATE), "Release Date");
        tagNameMap.put(new Integer(TAG_RELEASE_TIME), "Release Time");
        tagNameMap.put(new Integer(TAG_TIME_CREATED), "Time Created");
        tagNameMap.put(new Integer(TAG_ORIGINATING_PROGRAM), "Originating Program");
    }

    public IptcDirectory()
    {
        this.setDescriptor(new IptcDescriptor(this));
    }

    public String getName()
    {
        return "Iptc";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}
