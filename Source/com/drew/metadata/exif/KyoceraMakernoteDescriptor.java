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
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>KyoceraMakernoteDirectory</code>.
 *
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
 *
 * Most manufacturer's MakerNote counts the "offset to data" from the first byte
 * of TIFF header (same as the other IFD), but Kyocera (along with Fujifilm) counts
 * it from the first byte of MakerNote itself.
 */
public class KyoceraMakernoteDescriptor extends TagDescriptor
{
    public KyoceraMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case KyoceraMakernoteDirectory.TAG_KYOCERA_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            case KyoceraMakernoteDirectory.TAG_KYOCERA_PROPRIETARY_THUMBNAIL:
                return getProprietaryThumbnailDataDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getPrintImageMatchingInfoDescription() throws MetadataException
    {
        if (!_directory.containsTag(KyoceraMakernoteDirectory.TAG_KYOCERA_PRINT_IMAGE_MATCHING_INFO)) return null;
        byte[] bytes = _directory.getByteArray(KyoceraMakernoteDirectory.TAG_KYOCERA_PRINT_IMAGE_MATCHING_INFO);
        return "(" + bytes.length + " bytes)";
    }

    public String getProprietaryThumbnailDataDescription() throws MetadataException
    {
        if (!_directory.containsTag(KyoceraMakernoteDirectory.TAG_KYOCERA_PROPRIETARY_THUMBNAIL)) return null;
        byte[] bytes = _directory.getByteArray(KyoceraMakernoteDirectory.TAG_KYOCERA_PROPRIETARY_THUMBNAIL);
        return "(" + bytes.length + " bytes)";
    }
}
