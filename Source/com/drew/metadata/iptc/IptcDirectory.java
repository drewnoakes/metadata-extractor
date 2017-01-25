/*
 * Copyright 2002-2017 Drew Noakes
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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Describes tags used by the International Press Telecommunications Council (IPTC) metadata format.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class IptcDirectory extends Directory
{
    // IPTC EnvelopeRecord Tags
    public static final int TAG_ENVELOPE_RECORD_VERSION          = 0x0100; // 0 + 0x0100
    public static final int TAG_DESTINATION                      = 0x0105; // 5
    public static final int TAG_FILE_FORMAT                      = 0x0114; // 20
    public static final int TAG_FILE_VERSION                     = 0x0116; // 22
    public static final int TAG_SERVICE_ID                       = 0x011E; // 30
    public static final int TAG_ENVELOPE_NUMBER                  = 0x0128; // 40
    public static final int TAG_PRODUCT_ID                       = 0x0132; // 50
    public static final int TAG_ENVELOPE_PRIORITY                = 0x013C; // 60
    public static final int TAG_DATE_SENT                        = 0x0146; // 70
    public static final int TAG_TIME_SENT                        = 0x0150; // 80
    public static final int TAG_CODED_CHARACTER_SET              = 0x015A; // 90
    public static final int TAG_UNIQUE_OBJECT_NAME               = 0x0164; // 100
    public static final int TAG_ARM_IDENTIFIER                   = 0x0178; // 120
    public static final int TAG_ARM_VERSION                      = 0x017a; // 122

    // IPTC ApplicationRecord Tags
    public static final int TAG_APPLICATION_RECORD_VERSION       = 0x0200; // 0 + 0x0200
    public static final int TAG_OBJECT_TYPE_REFERENCE            = 0x0203; // 3
    public static final int TAG_OBJECT_ATTRIBUTE_REFERENCE       = 0x0204; // 4
    public static final int TAG_OBJECT_NAME                      = 0x0205; // 5
    public static final int TAG_EDIT_STATUS                      = 0x0207; // 7
    public static final int TAG_EDITORIAL_UPDATE                 = 0x0208; // 8
    public static final int TAG_URGENCY                          = 0X020A; // 10
    public static final int TAG_SUBJECT_REFERENCE                = 0X020C; // 12
    public static final int TAG_CATEGORY                         = 0x020F; // 15
    public static final int TAG_SUPPLEMENTAL_CATEGORIES          = 0x0214; // 20
    public static final int TAG_FIXTURE_ID                       = 0x0216; // 22
    public static final int TAG_KEYWORDS                         = 0x0219; // 25
    public static final int TAG_CONTENT_LOCATION_CODE            = 0x021A; // 26
    public static final int TAG_CONTENT_LOCATION_NAME            = 0x021B; // 27
    public static final int TAG_RELEASE_DATE                     = 0X021E; // 30
    public static final int TAG_RELEASE_TIME                     = 0x0223; // 35
    public static final int TAG_EXPIRATION_DATE                  = 0x0225; // 37
    public static final int TAG_EXPIRATION_TIME                  = 0x0226; // 38
    public static final int TAG_SPECIAL_INSTRUCTIONS             = 0x0228; // 40
    public static final int TAG_ACTION_ADVISED                   = 0x022A; // 42
    public static final int TAG_REFERENCE_SERVICE                = 0x022D; // 45
    public static final int TAG_REFERENCE_DATE                   = 0x022F; // 47
    public static final int TAG_REFERENCE_NUMBER                 = 0x0232; // 50
    public static final int TAG_DATE_CREATED                     = 0x0237; // 55
    public static final int TAG_TIME_CREATED                     = 0X023C; // 60
    public static final int TAG_DIGITAL_DATE_CREATED             = 0x023E; // 62
    public static final int TAG_DIGITAL_TIME_CREATED             = 0x023F; // 63
    public static final int TAG_ORIGINATING_PROGRAM              = 0x0241; // 65
    public static final int TAG_PROGRAM_VERSION                  = 0x0246; // 70
    public static final int TAG_OBJECT_CYCLE                     = 0x024B; // 75
    public static final int TAG_BY_LINE                          = 0x0250; // 80
    public static final int TAG_BY_LINE_TITLE                    = 0x0255; // 85
    public static final int TAG_CITY                             = 0x025A; // 90
    public static final int TAG_SUB_LOCATION                     = 0x025C; // 92
    public static final int TAG_PROVINCE_OR_STATE                = 0x025F; // 95
    public static final int TAG_COUNTRY_OR_PRIMARY_LOCATION_CODE = 0x0264; // 100
    public static final int TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME = 0x0265; // 101
    public static final int TAG_ORIGINAL_TRANSMISSION_REFERENCE  = 0x0267; // 103
    public static final int TAG_HEADLINE                         = 0x0269; // 105
    public static final int TAG_CREDIT                           = 0x026E; // 110
    public static final int TAG_SOURCE                           = 0x0273; // 115
    public static final int TAG_COPYRIGHT_NOTICE                 = 0x0274; // 116
    public static final int TAG_CONTACT                          = 0x0276; // 118
    public static final int TAG_CAPTION                          = 0x0278; // 120
    public static final int TAG_LOCAL_CAPTION                    = 0x0279; // 121
    public static final int TAG_CAPTION_WRITER                   = 0x027A; // 122
    public static final int TAG_RASTERIZED_CAPTION               = 0x027D; // 125
    public static final int TAG_IMAGE_TYPE                       = 0x0282; // 130
    public static final int TAG_IMAGE_ORIENTATION                = 0x0283; // 131
    public static final int TAG_LANGUAGE_IDENTIFIER              = 0x0287; // 135
    public static final int TAG_AUDIO_TYPE                       = 0x0296; // 150
    public static final int TAG_AUDIO_SAMPLING_RATE              = 0x0297; // 151
    public static final int TAG_AUDIO_SAMPLING_RESOLUTION        = 0x0298; // 152
    public static final int TAG_AUDIO_DURATION                   = 0x0299; // 153
    public static final int TAG_AUDIO_OUTCUE                     = 0x029A; // 154

    public static final int TAG_JOB_ID                           = 0x02B8; // 184
    public static final int TAG_MASTER_DOCUMENT_ID               = 0x02B9; // 185
    public static final int TAG_SHORT_DOCUMENT_ID                = 0x02BA; // 186
    public static final int TAG_UNIQUE_DOCUMENT_ID               = 0x02BB; // 187
    public static final int TAG_OWNER_ID                         = 0x02BC; // 188

    public static final int TAG_OBJECT_PREVIEW_FILE_FORMAT       = 0x02C8; // 200
    public static final int TAG_OBJECT_PREVIEW_FILE_FORMAT_VERSION  = 0x02C9; // 201
    public static final int TAG_OBJECT_PREVIEW_DATA              = 0x02CA; // 202

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_ENVELOPE_RECORD_VERSION, "Enveloped Record Version");
        _tagNameMap.put(TAG_DESTINATION, "Destination");
        _tagNameMap.put(TAG_FILE_FORMAT, "File Format");
        _tagNameMap.put(TAG_FILE_VERSION, "File Version");
        _tagNameMap.put(TAG_SERVICE_ID, "Service Identifier");
        _tagNameMap.put(TAG_ENVELOPE_NUMBER, "Envelope Number");
        _tagNameMap.put(TAG_PRODUCT_ID, "Product Identifier");
        _tagNameMap.put(TAG_ENVELOPE_PRIORITY, "Envelope Priority");
        _tagNameMap.put(TAG_DATE_SENT, "Date Sent");
        _tagNameMap.put(TAG_TIME_SENT, "Time Sent");
        _tagNameMap.put(TAG_CODED_CHARACTER_SET, "Coded Character Set");
        _tagNameMap.put(TAG_UNIQUE_OBJECT_NAME, "Unique Object Name");
        _tagNameMap.put(TAG_ARM_IDENTIFIER, "ARM Identifier");
        _tagNameMap.put(TAG_ARM_VERSION, "ARM Version");

        _tagNameMap.put(TAG_APPLICATION_RECORD_VERSION, "Application Record Version");
        _tagNameMap.put(TAG_OBJECT_TYPE_REFERENCE, "Object Type Reference");
        _tagNameMap.put(TAG_OBJECT_ATTRIBUTE_REFERENCE, "Object Attribute Reference");
        _tagNameMap.put(TAG_OBJECT_NAME, "Object Name");
        _tagNameMap.put(TAG_EDIT_STATUS, "Edit Status");
        _tagNameMap.put(TAG_EDITORIAL_UPDATE, "Editorial Update");
        _tagNameMap.put(TAG_URGENCY, "Urgency");
        _tagNameMap.put(TAG_SUBJECT_REFERENCE, "Subject Reference");
        _tagNameMap.put(TAG_CATEGORY, "Category");
        _tagNameMap.put(TAG_SUPPLEMENTAL_CATEGORIES, "Supplemental Category(s)");
        _tagNameMap.put(TAG_FIXTURE_ID, "Fixture Identifier");
        _tagNameMap.put(TAG_KEYWORDS, "Keywords");
        _tagNameMap.put(TAG_CONTENT_LOCATION_CODE, "Content Location Code");
        _tagNameMap.put(TAG_CONTENT_LOCATION_NAME, "Content Location Name");
        _tagNameMap.put(TAG_RELEASE_DATE, "Release Date");
        _tagNameMap.put(TAG_RELEASE_TIME, "Release Time");
        _tagNameMap.put(TAG_EXPIRATION_DATE, "Expiration Date");
        _tagNameMap.put(TAG_EXPIRATION_TIME, "Expiration Time");
        _tagNameMap.put(TAG_SPECIAL_INSTRUCTIONS, "Special Instructions");
        _tagNameMap.put(TAG_ACTION_ADVISED, "Action Advised");
        _tagNameMap.put(TAG_REFERENCE_SERVICE, "Reference Service");
        _tagNameMap.put(TAG_REFERENCE_DATE, "Reference Date");
        _tagNameMap.put(TAG_REFERENCE_NUMBER, "Reference Number");
        _tagNameMap.put(TAG_DATE_CREATED, "Date Created");
        _tagNameMap.put(TAG_TIME_CREATED, "Time Created");
        _tagNameMap.put(TAG_DIGITAL_DATE_CREATED, "Digital Date Created");
        _tagNameMap.put(TAG_DIGITAL_TIME_CREATED, "Digital Time Created");
        _tagNameMap.put(TAG_ORIGINATING_PROGRAM, "Originating Program");
        _tagNameMap.put(TAG_PROGRAM_VERSION, "Program Version");
        _tagNameMap.put(TAG_OBJECT_CYCLE, "Object Cycle");
        _tagNameMap.put(TAG_BY_LINE, "By-line");
        _tagNameMap.put(TAG_BY_LINE_TITLE, "By-line Title");
        _tagNameMap.put(TAG_CITY, "City");
        _tagNameMap.put(TAG_SUB_LOCATION, "Sub-location");
        _tagNameMap.put(TAG_PROVINCE_OR_STATE, "Province/State");
        _tagNameMap.put(TAG_COUNTRY_OR_PRIMARY_LOCATION_CODE, "Country/Primary Location Code");
        _tagNameMap.put(TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME, "Country/Primary Location Name");
        _tagNameMap.put(TAG_ORIGINAL_TRANSMISSION_REFERENCE, "Original Transmission Reference");
        _tagNameMap.put(TAG_HEADLINE, "Headline");
        _tagNameMap.put(TAG_CREDIT, "Credit");
        _tagNameMap.put(TAG_SOURCE, "Source");
        _tagNameMap.put(TAG_COPYRIGHT_NOTICE, "Copyright Notice");
        _tagNameMap.put(TAG_CONTACT, "Contact");
        _tagNameMap.put(TAG_CAPTION, "Caption/Abstract");
        _tagNameMap.put(TAG_LOCAL_CAPTION, "Local Caption");
        _tagNameMap.put(TAG_CAPTION_WRITER, "Caption Writer/Editor");
        _tagNameMap.put(TAG_RASTERIZED_CAPTION, "Rasterized Caption");
        _tagNameMap.put(TAG_IMAGE_TYPE, "Image Type");
        _tagNameMap.put(TAG_IMAGE_ORIENTATION, "Image Orientation");
        _tagNameMap.put(TAG_LANGUAGE_IDENTIFIER, "Language Identifier");
        _tagNameMap.put(TAG_AUDIO_TYPE, "Audio Type");
        _tagNameMap.put(TAG_AUDIO_SAMPLING_RATE, "Audio Sampling Rate");
        _tagNameMap.put(TAG_AUDIO_SAMPLING_RESOLUTION, "Audio Sampling Resolution");
        _tagNameMap.put(TAG_AUDIO_DURATION, "Audio Duration");
        _tagNameMap.put(TAG_AUDIO_OUTCUE, "Audio Outcue");

        _tagNameMap.put(TAG_JOB_ID, "Job Identifier");
        _tagNameMap.put(TAG_MASTER_DOCUMENT_ID, "Master Document Identifier");
        _tagNameMap.put(TAG_SHORT_DOCUMENT_ID, "Short Document Identifier");
        _tagNameMap.put(TAG_UNIQUE_DOCUMENT_ID, "Unique Document Identifier");
        _tagNameMap.put(TAG_OWNER_ID, "Owner Identifier");

        _tagNameMap.put(TAG_OBJECT_PREVIEW_FILE_FORMAT, "Object Data Preview File Format");
        _tagNameMap.put(TAG_OBJECT_PREVIEW_FILE_FORMAT_VERSION, "Object Data Preview File Format Version");
        _tagNameMap.put(TAG_OBJECT_PREVIEW_DATA, "Object Data Preview Data");
    }

    public IptcDirectory()
    {
        this.setDescriptor(new IptcDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "IPTC";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * Returns any keywords contained in the IPTC data.  This value may be <code>null</code>.
     */
    @Nullable
    public List<String> getKeywords()
    {
        final String[] array = getStringArray(TAG_KEYWORDS);
        if (array==null)
            return null;
        return Arrays.asList(array);
    }

    /**
     * Parses the Date Sent tag and the Time Sent tag to obtain a single Date object representing the
     * date and time when the service sent this image.
     * @return A Date object representing when the service sent this image, if possible, otherwise null
     */
    @Nullable
    public Date getDateSent()
    {
        return getDate(TAG_DATE_SENT, TAG_TIME_SENT);
    }

    /**
     * Parses the Release Date tag and the Release Time tag to obtain a single Date object representing the
     * date and time when this image was released.
     * @return A Date object representing when this image was released, if possible, otherwise null
     */
    @Nullable
    public Date getReleaseDate()
    {
        return getDate(TAG_RELEASE_DATE, TAG_RELEASE_TIME);
    }

    /**
     * Parses the Expiration Date tag and the Expiration Time tag to obtain a single Date object representing
     * that this image should not used after this date and time.
     * @return A Date object representing when this image was released, if possible, otherwise null
     */
    @Nullable
    public Date getExpirationDate()
    {
        return getDate(TAG_EXPIRATION_DATE, TAG_EXPIRATION_TIME);
    }

    /**
     * Parses the Date Created tag and the Time Created tag to obtain a single Date object representing the
     * date and time when this image was captured.
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getDateCreated()
    {
        return getDate(TAG_DATE_CREATED, TAG_TIME_CREATED);
    }

    /**
     * Parses the Digital Date Created tag and the Digital Time Created tag to obtain a single Date object
     * representing the date and time when the digital representation of this image was created.
     * @return A Date object representing when the digital representation of this image was created,
     * if possible, otherwise null
     */
    @Nullable
    public Date getDigitalDateCreated()
    {
        return getDate(TAG_DIGITAL_DATE_CREATED, TAG_DIGITAL_TIME_CREATED);
    }

    @Nullable
    private Date getDate(int dateTagType, int timeTagType)
    {
        String date = getString(dateTagType);
        String time = getString(timeTagType);

        if (date == null)
            return null;
        if (time == null)
            return null;

        try {
            DateFormat parser = new SimpleDateFormat("yyyyMMddHHmmssZ");
            return parser.parse(date + time);
        } catch (ParseException e) {
            return null;
        }
    }
}
