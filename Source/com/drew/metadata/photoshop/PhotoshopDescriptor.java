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
package com.drew.metadata.photoshop;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.drew.metadata.photoshop.PhotoshopDirectory.*;

/**
 * @author Yuri Binev
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PhotoshopDescriptor extends TagDescriptor<PhotoshopDirectory>
{
    public PhotoshopDescriptor(@NotNull PhotoshopDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_THUMBNAIL:
            case TAG_THUMBNAIL_OLD:
                return getThumbnailDescription(tagType);
            case TAG_URL:
            case TAG_XML:
                return getSimpleString(tagType);
            case TAG_IPTC:
                return getBinaryDataString(tagType);
            case TAG_SLICES:
                return getSlicesDescription();
            case TAG_VERSION:
                return getVersionDescription();
            case TAG_COPYRIGHT:
                return getBooleanString(tagType);
            case TAG_RESOLUTION_INFO:
                return getResolutionInfoDescription();
            case TAG_GLOBAL_ANGLE:
            case TAG_GLOBAL_ALTITUDE:
            case TAG_URL_LIST:
            case TAG_SEED_NUMBER:
                return get32BitNumberString(tagType);
            case TAG_JPEG_QUALITY:
                return getJpegQualityString();
            case TAG_PRINT_SCALE:
                return getPrintScaleDescription();
            case TAG_PIXEL_ASPECT_RATIO:
                return getPixelAspectRatioString();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getJpegQualityString()
    {
        try {
            byte[] b = _directory.getByteArray(TAG_JPEG_QUALITY);

            if (b == null)
                return _directory.getString(TAG_JPEG_QUALITY);

            RandomAccessReader reader = new ByteArrayReader(b);
            int q = reader.getUInt16(0); // & 0xFFFF;
            int f = reader.getUInt16(2); // & 0xFFFF;
            int s = reader.getUInt16(4);

            int q1 = q <= 0xFFFF && q >= 0xFFFD
                ? q - 0xFFFC
                : q <= 8
                    ? q + 4
                    : q;

            String quality;
            switch (q) {
                case 0xFFFD:
                case 0xFFFE:
                case 0xFFFF:
                case 0:
                    quality = "Low";
                    break;
                case 1:
                case 2:
                case 3:
                    quality = "Medium";
                    break;
                case 4:
                case 5:
                    quality = "High";
                    break;
                case 6:
                case 7:
                case 8:
                    quality = "Maximum";
                    break;
                default:
                    quality = "Unknown";
            }

            String format;
            switch (f) {
                case 0x0000:
                    format = "Standard";
                    break;
                case 0x0001:
                    format = "Optimised";
                    break;
                case 0x0101:
                    format = "Progressive";
                    break;
                default:
                    format = String.format("Unknown 0x%04X", f);
            }

            String scans = s >= 1 && s <= 3
                    ? String.format("%d", s + 2)
                    : String.format("Unknown 0x%04X", s);

            return String.format("%d (%s), %s format, %s scans", q1, quality, format, scans);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public String getPixelAspectRatioString()
    {
        try {
            byte[] bytes = _directory.getByteArray(TAG_PIXEL_ASPECT_RATIO);
            if (bytes == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(bytes);
            double d = reader.getDouble64(4);
            return Double.toString(d);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getPrintScaleDescription()
    {
        try {
            byte bytes[] = _directory.getByteArray(TAG_PRINT_SCALE);
            if (bytes == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(bytes);
            int style = reader.getInt32(0);
            float locX = reader.getFloat32(2);
            float locY = reader.getFloat32(6);
            float scale = reader.getFloat32(10);
            switch (style) {
                case 0:
                    return "Centered, Scale " + scale;
                case 1:
                    return "Size to fit";
                case 2:
                    return String.format("User defined, X:%s Y:%s, Scale:%s", locX, locY, scale);
                default:
                    return String.format("Unknown %04X, X:%s Y:%s, Scale:%s", style, locX, locY, scale);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getResolutionInfoDescription()
    {
        try {
            byte[] bytes = _directory.getByteArray(TAG_RESOLUTION_INFO);
            if (bytes == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(bytes);
            float resX = reader.getS15Fixed16(0);
            float resY = reader.getS15Fixed16(8); // is this the correct offset? it's only reading 4 bytes each time
            DecimalFormat format = new DecimalFormat("0.##");
            return format.format(resX) + "x" + format.format(resY) + " DPI";
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getVersionDescription()
    {
        try {
            final byte[] bytes = _directory.getByteArray(TAG_VERSION);
            if (bytes == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(bytes);
            int pos = 0;
            int ver = reader.getInt32(0);
            pos += 4;
            pos++;
            int readerLength = reader.getInt32(5);
            pos += 4;
            String readerStr = reader.getString(9, readerLength * 2, "UTF-16");
            pos += readerLength * 2;
            int writerLength = reader.getInt32(pos);
            pos += 4;
            String writerStr = reader.getString(pos, writerLength * 2, "UTF-16");
            pos += writerLength * 2;
            int fileVersion = reader.getInt32(pos);
            return String.format("%d (%s, %s) %d", ver, readerStr, writerStr, fileVersion);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public String getSlicesDescription()
    {
        try {
            final byte bytes[] = _directory.getByteArray(TAG_SLICES);
            if (bytes == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(bytes);
            int nameLength = reader.getInt32(20);
            String name = reader.getString(24, nameLength * 2, "UTF-16");
            int pos = 24 + nameLength * 2;
            int sliceCount = reader.getInt32(pos);
            return String.format("%s (%d,%d,%d,%d) %d Slices",
                    name, reader.getInt32(4), reader.getInt32(8), reader.getInt32(12), reader.getInt32(16), sliceCount);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public String getThumbnailDescription(int tagType)
    {
        try {
            byte[] v = _directory.getByteArray(tagType);
            if (v == null)
                return null;
            RandomAccessReader reader = new ByteArrayReader(v);
            int format = reader.getInt32(0);
            int width = reader.getInt32(4);
            int height = reader.getInt32(8);
            //skip WidthBytes
            int totalSize = reader.getInt32(16);
            int compSize = reader.getInt32(20);
            int bpp = reader.getInt32(24);
            //skip Number of planes
            return String.format("%s, %dx%d, Decomp %d bytes, %d bpp, %d bytes",
                    format == 1 ? "JpegRGB" : "RawRGB",
                    width, height, totalSize, bpp, compSize);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private String getBooleanString(int tag)
    {
        final byte[] bytes = _directory.getByteArray(tag);
        if (bytes == null || bytes.length == 0)
            return null;
        return bytes[0] == 0 ? "No" : "Yes";
    }

    @Nullable
    private String get32BitNumberString(int tag)
    {
        byte[] bytes = _directory.getByteArray(tag);
        if (bytes == null)
            return null;
        RandomAccessReader reader = new ByteArrayReader(bytes);
        try {
            return String.format("%d", reader.getInt32(0));
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private String getSimpleString(int tagType)
    {
        final byte[] bytes = _directory.getByteArray(tagType);
        if (bytes == null)
            return null;
        return new String(bytes);
    }

    @Nullable
    private String getBinaryDataString(int tagType)
    {
        final byte[] bytes = _directory.getByteArray(tagType);
        if (bytes == null)
            return null;
        return String.format("%d bytes binary data", bytes.length);
    }
}
