/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.iptc;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>IptcDirectory</code>.
 * <p/>
 * As the IPTC directory already stores values as strings, this class simply returns the tag's value.
 *
 * @author Drew Noakes http://drewnoakes.com
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
