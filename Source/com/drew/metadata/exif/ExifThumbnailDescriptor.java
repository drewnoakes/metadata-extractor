/*
 * Copyright 2002-2015 Drew Noakes
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

package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import static com.drew.metadata.exif.ExifThumbnailDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link ExifThumbnailDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifThumbnailDescriptor extends ExifDescriptorBase<ExifThumbnailDirectory>
{
    public ExifThumbnailDescriptor(@NotNull ExifThumbnailDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_THUMBNAIL_OFFSET:
                return getThumbnailOffsetDescription();
            case TAG_THUMBNAIL_LENGTH:
                return getThumbnailLengthDescription();
            case TAG_THUMBNAIL_COMPRESSION:
                return getCompressionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCompressionDescription()
    {
        Integer value = _directory.getInteger(TAG_THUMBNAIL_COMPRESSION);
        if (value == null)
            return null;
        switch (value) {
            case 1: return "Uncompressed";
            case 2: return "CCITT 1D";
            case 3: return "T4/Group 3 Fax";
            case 4: return "T6/Group 4 Fax";
            case 5: return "LZW";
            case 6: return "JPEG (old-style)";
            case 7: return "JPEG";
            case 8: return "Adobe Deflate";
            case 9: return "JBIG B&W";
            case 10: return "JBIG Color";
            case 32766: return "Next";
            case 32771: return "CCIRLEW";
            case 32773: return "PackBits";
            case 32809: return "Thunderscan";
            case 32895: return "IT8CTPAD";
            case 32896: return "IT8LW";
            case 32897: return "IT8MP";
            case 32898: return "IT8BL";
            case 32908: return "PixarFilm";
            case 32909: return "PixarLog";
            case 32946: return "Deflate";
            case 32947: return "DCS";
            case 32661: return "JBIG";
            case 32676: return "SGILog";
            case 32677: return "SGILog24";
            case 32712: return "JPEG 2000";
            case 32713: return "Nikon NEF Compressed";
            default:
                return "Unknown compression";
        }
    }

    @Nullable
    public String getThumbnailLengthDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_LENGTH);
        return value == null ? null : value + " bytes";
    }

    @Nullable
    public String getThumbnailOffsetDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_OFFSET);
        return value == null ? null : value + " bytes";
    }
}
