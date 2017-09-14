package com.drew.metadata.photoshop;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.IOException;
import java.util.Set;

/**
 * @author Payton Garland
 */
public class PhotoshopTiffHandler extends ExifTiffHandler
{
    // Photoshop-specific Tiff Tags
    // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577413_pgfId-1039502
    private static final int TAG_PAGE_MAKER_EXTENSION = 0x014A;
    private static final int TAG_JPEG_TABLES = 0X01B5;
    private static final int TAG_XMP = 0x02BC;
    private static final int TAG_FILE_INFORMATION = 0x83BB;
    private static final int TAG_PHOTOSHOP_IMAGE_RESOURCES = 0x8649;
    private static final int TAG_EXIF_IFD_POINTER = 0x8769;
    private static final int TAG_ICC_PROFILES = 0x8773;
    private static final int TAG_EXIF_GPS = 0x8825;
    private static final int TAG_T_IMAGE_SOURCE_DATA = 0x935C;
    private static final int TAG_T_ANNOTATIONS = 0xC44F;

    public PhotoshopTiffHandler(Metadata metadata, Directory parentDirectory)
    {
        super(metadata, parentDirectory);
    }

    public boolean customProcessTag(final int tagOffset,
                                    final @NotNull Set<Integer> processedIfdOffsets,
                                    final int tiffHeaderOffset,
                                    final @NotNull RandomAccessReader reader,
                                    final int tagId,
                                    final int byteCount) throws IOException
    {
        switch (tagId) {
            case TAG_XMP:
                new XmpReader().extract(reader.getBytes(tagOffset, byteCount), _metadata);
                return true;
            case TAG_PHOTOSHOP_IMAGE_RESOURCES:
                new PhotoshopReader().extract(new SequentialByteArrayReader(reader.getBytes(tagOffset, byteCount)), byteCount, _metadata);
                return true;
            case TAG_ICC_PROFILES:
                new IccReader().extract(new ByteArrayReader(reader.getBytes(tagOffset, byteCount)), _metadata);
                return true;
        }


        return super.customProcessTag(tagOffset, processedIfdOffsets, tiffHeaderOffset, reader, tagId, byteCount);
    }
}
