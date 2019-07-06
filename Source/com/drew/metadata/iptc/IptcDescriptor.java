/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.iptc;

import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.iptc.IptcDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link IptcDirectory}.
 * <p>
 * As the IPTC directory already stores values as strings, this class simply returns the tag's value.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class IptcDescriptor extends TagDescriptor<IptcDirectory>
{
    public IptcDescriptor(@NotNull IptcDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_DATE_CREATED:
                return getDateCreatedDescription();
            case TAG_DIGITAL_DATE_CREATED:
                return getDigitalDateCreatedDescription();
            case TAG_DATE_SENT:
                return getDateSentDescription();
            case TAG_EXPIRATION_DATE:
                return getExpirationDateDescription();
            case TAG_EXPIRATION_TIME:
                return getExpirationTimeDescription();
            case TAG_FILE_FORMAT:
                return getFileFormatDescription();
            case TAG_KEYWORDS:
                return getKeywordsDescription();
            case TAG_REFERENCE_DATE:
                return getReferenceDateDescription();
            case TAG_RELEASE_DATE:
                return getReleaseDateDescription();
            case TAG_RELEASE_TIME:
                return getReleaseTimeDescription();
            case TAG_TIME_CREATED:
                return getTimeCreatedDescription();
            case TAG_DIGITAL_TIME_CREATED:
                return getDigitalTimeCreatedDescription();
            case TAG_TIME_SENT:
                return getTimeSentDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getDateDescription(int tagType)
    {
        String s = _directory.getString(tagType);
        if (s == null)
            return null;
        if (s.length() == 8)
            return s.substring(0, 4) + ':' + s.substring(4, 6) + ':' + s.substring(6);
        return s;
    }

    @Nullable
    public String getTimeDescription(int tagType)
    {
        String s = _directory.getString(tagType);
        if (s == null)
            return null;
        if (s.length() == 6 || s.length() == 11)
            return s.substring(0, 2) + ':' + s.substring(2, 4) + ':' + s.substring(4);
        return s;
    }

    @Nullable
    public String getFileFormatDescription()
    {
        Integer value = _directory.getInteger(TAG_FILE_FORMAT);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "No ObjectData";
            case 1: return "IPTC-NAA Digital Newsphoto Parameter Record";
            case 2: return "IPTC7901 Recommended Message Format";
            case 3: return "Tagged Image File Format (Adobe/Aldus Image data)";
            case 4: return "Illustrator (Adobe Graphics data)";
            case 5: return "AppleSingle (Apple Computer Inc)";
            case 6: return "NAA 89-3 (ANPA 1312)";
            case 7: return "MacBinary II";
            case 8: return "IPTC Unstructured Character Oriented File Format (UCOFF)";
            case 9: return "United Press International ANPA 1312 variant";
            case 10: return "United Press International Down-Load Message";
            case 11: return "JPEG File Interchange (JFIF)";
            case 12: return "Photo-CD Image-Pac (Eastman Kodak)";
            case 13: return "Bit Mapped Graphics File [.BMP] (Microsoft)";
            case 14: return "Digital Audio File [.WAV] (Microsoft & Creative Labs)";
            case 15: return "Audio plus Moving Video [.AVI] (Microsoft)";
            case 16: return "PC DOS/Windows Executable Files [.COM][.EXE]";
            case 17: return "Compressed Binary File [.ZIP] (PKWare Inc)";
            case 18: return "Audio Interchange File Format AIFF (Apple Computer Inc)";
            case 19: return "RIFF Wave (Microsoft Corporation)";
            case 20: return "Freehand (Macromedia/Aldus)";
            case 21: return "Hypertext Markup Language [.HTML] (The Internet Society)";
            case 22: return "MPEG 2 Audio Layer 2 (Musicom), ISO/IEC";
            case 23: return "MPEG 2 Audio Layer 3, ISO/IEC";
            case 24: return "Portable Document File [.PDF] Adobe";
            case 25: return "News Industry Text Format (NITF)";
            case 26: return "Tape Archive [.TAR]";
            case 27: return "Tidningarnas Telegrambyra NITF version (TTNITF DTD)";
            case 28: return "Ritzaus Bureau NITF version (RBNITF DTD)";
            case 29: return "Corel Draw [.CDR]";
        }
        return String.format("Unknown (%d)", value);
    }

    @Nullable
    public String getByLineDescription()
    {
        return _directory.getString(TAG_BY_LINE);
    }

    @Nullable
    public String getByLineTitleDescription()
    {
        return _directory.getString(TAG_BY_LINE_TITLE);
    }

    @Nullable
    public String getCaptionDescription()
    {
        return _directory.getString(TAG_CAPTION);
    }

    @Nullable
    public String getCategoryDescription()
    {
        return _directory.getString(TAG_CATEGORY);
    }

    @Nullable
    public String getCityDescription()
    {
        return _directory.getString(TAG_CITY);
    }

    @Nullable
    public String getCopyrightNoticeDescription()
    {
        return _directory.getString(TAG_COPYRIGHT_NOTICE);
    }

    @Nullable
    public String getCountryOrPrimaryLocationDescription()
    {
        return _directory.getString(TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME);
    }

    @Nullable
    public String getCreditDescription()
    {
        return _directory.getString(TAG_CREDIT);
    }

    @Nullable
    public String getDateCreatedDescription()
    {
        return getDateDescription(TAG_DATE_CREATED);
    }

    @Nullable
    public String getDigitalDateCreatedDescription()
    {
        return getDateDescription(TAG_DIGITAL_DATE_CREATED);
    }

    @Nullable
    public String getDateSentDescription()
    {
        return getDateDescription(TAG_DATE_SENT);
    }

    @Nullable
    public String getExpirationDateDescription()
    {
        return getDateDescription(TAG_EXPIRATION_DATE);
    }

    @Nullable
    public String getExpirationTimeDescription()
    {
        return getTimeDescription(TAG_EXPIRATION_TIME);
    }

    @Nullable
    public String getHeadlineDescription()
    {
        return _directory.getString(TAG_HEADLINE);
    }

    @Nullable
    public String getKeywordsDescription()
    {
        final String[] keywords = _directory.getStringArray(TAG_KEYWORDS);
        if (keywords==null)
            return null;
        return StringUtil.join(keywords, ";");
    }

    @Nullable
    public String getObjectNameDescription()
    {
        return _directory.getString(TAG_OBJECT_NAME);
    }

    @Nullable
    public String getOriginalTransmissionReferenceDescription()
    {
        return _directory.getString(TAG_ORIGINAL_TRANSMISSION_REFERENCE);
    }

    @Nullable
    public String getOriginatingProgramDescription()
    {
        return _directory.getString(TAG_ORIGINATING_PROGRAM);
    }

    @Nullable
    public String getProvinceOrStateDescription()
    {
        return _directory.getString(TAG_PROVINCE_OR_STATE);
    }

    @Nullable
    public String getRecordVersionDescription()
    {
        return _directory.getString(TAG_APPLICATION_RECORD_VERSION);
    }

    @Nullable
    public String getReferenceDateDescription()
    {
        return getDateDescription(TAG_REFERENCE_DATE);
    }

    @Nullable
    public String getReleaseDateDescription()
    {
        return getDateDescription(TAG_RELEASE_DATE);
    }

    @Nullable
    public String getReleaseTimeDescription()
    {
        return getTimeDescription(TAG_RELEASE_TIME);
    }

    @Nullable
    public String getSourceDescription()
    {
        return _directory.getString(TAG_SOURCE);
    }

    @Nullable
    public String getSpecialInstructionsDescription()
    {
        return _directory.getString(TAG_SPECIAL_INSTRUCTIONS);
    }

    @Nullable
    public String getSupplementalCategoriesDescription()
    {
        return _directory.getString(TAG_SUPPLEMENTAL_CATEGORIES);
    }

    @Nullable
    public String getTimeCreatedDescription()
    {
        return getTimeDescription(TAG_TIME_CREATED);
    }

    @Nullable
    public String getDigitalTimeCreatedDescription()
    {
        return getTimeDescription(TAG_DIGITAL_TIME_CREATED);
    }

    @Nullable
    public String getTimeSentDescription()
    {
        return getTimeDescription(TAG_TIME_SENT);
    }

    @Nullable
    public String getUrgencyDescription()
    {
        return _directory.getString(TAG_URGENCY);
    }

    @Nullable
    public String getWriterDescription()
    {
        return _directory.getString(TAG_CAPTION_WRITER);
    }
}
