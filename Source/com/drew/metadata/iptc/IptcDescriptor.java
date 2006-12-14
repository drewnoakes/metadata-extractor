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
 * Created by dnoakes on 21-Nov-2002 17:58:19 using IntelliJ IDEA.
 */
package com.drew.metadata.iptc;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>IptcDirectory</code>.
 *
 * As the IPTC directory already stores values as strings, this class simply returns the tag's value.
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

    public String getByLineDescription()
    {
        return _directory.getString(IptcDirectory.TAG_BY_LINE);
    }

    public String getByLineTitleDescription()
    {
        return _directory.getString(IptcDirectory.TAG_BY_LINE_TITLE);
    }

    public String getCaptionDescription()
    {
        return _directory.getString(IptcDirectory.TAG_CAPTION);
    }

    public String getCategoryDescription()
    {
        return _directory.getString(IptcDirectory.TAG_CATEGORY);
    }

    public String getCityDescription()
    {
        return _directory.getString(IptcDirectory.TAG_CITY);
    }

    public String getCopyrightNoticeDescription()
    {
        return _directory.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE);
    }

    public String getCountryOrPrimaryLocationDescription()
    {
        return _directory.getString(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION);
    }

    public String getCreditDescription()
    {
        return _directory.getString(IptcDirectory.TAG_CREDIT);
    }

    public String getDateCreatedDescription()
    {
        return _directory.getString(IptcDirectory.TAG_DATE_CREATED);
    }

    public String getHeadlineDescription()
    {
        return _directory.getString(IptcDirectory.TAG_HEADLINE);
    }

    public String getKeywordsDescription()
    {
        return _directory.getString(IptcDirectory.TAG_KEYWORDS);
    }

    public String getObjectNameDescription()
    {
        return _directory.getString(IptcDirectory.TAG_OBJECT_NAME);
    }

    public String getOriginalTransmissionReferenceDescription()
    {
        return _directory.getString(IptcDirectory.TAG_ORIGINAL_TRANSMISSION_REFERENCE);
    }

    public String getOriginatingProgramDescription()
    {
        return _directory.getString(IptcDirectory.TAG_ORIGINATING_PROGRAM);
    }

    public String getProvinceOrStateDescription()
    {
        return _directory.getString(IptcDirectory.TAG_PROVINCE_OR_STATE);
    }

    public String getRecordVersionDescription()
    {
        return _directory.getString(IptcDirectory.TAG_RECORD_VERSION);
    }

    public String getReleaseDateDescription()
    {
        return _directory.getString(IptcDirectory.TAG_RELEASE_DATE);
    }

    public String getReleaseTimeDescription()
    {
        return _directory.getString(IptcDirectory.TAG_RELEASE_TIME);
    }

    public String getSourceDescription()
    {
        return _directory.getString(IptcDirectory.TAG_SOURCE);
    }

    public String getSpecialInstructionsDescription()
    {
        return _directory.getString(IptcDirectory.TAG_SPECIAL_INSTRUCTIONS);
    }

    public String getSupplementalCategoriesDescription()
    {
        return _directory.getString(IptcDirectory.TAG_SUPPLEMENTAL_CATEGORIES);
    }

    public String getTimeCreatedDescription()
    {
        return _directory.getString(IptcDirectory.TAG_TIME_CREATED);
    }

    public String getUrgencyDescription()
    {
        return _directory.getString(IptcDirectory.TAG_URGENCY);
    }

    public String getWriterDescription()
    {
        return _directory.getString(IptcDirectory.TAG_WRITER);
    }
}
