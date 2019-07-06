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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.KyoceraMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link KyoceraMakernoteDirectory}.
 * <p>
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
 * <p>
 * Most manufacturer's Makernote counts the "offset to data" from the first byte
 * of TIFF header (same as the other IFD), but Kyocera (along with Fujifilm) counts
 * it from the first byte of Makernote itself.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class KyoceraMakernoteDescriptor extends TagDescriptor<KyoceraMakernoteDirectory>
{
    public KyoceraMakernoteDescriptor(@NotNull KyoceraMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_PROPRIETARY_THUMBNAIL:
                return getProprietaryThumbnailDataDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getProprietaryThumbnailDataDescription()
    {
        return getByteLengthDescription(TAG_PROPRIETARY_THUMBNAIL);
    }
}
