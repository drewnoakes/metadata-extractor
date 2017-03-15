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
package com.drew.metadata.bmp;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 * @author Nadahar
 */
@SuppressWarnings("WeakerAccess")
public class BmpHeaderDirectory extends Directory
{
    public static final int TAG_BITMAP_TYPE = -2;
    public static final int TAG_HEADER_SIZE = -1;

    public static final int TAG_IMAGE_HEIGHT = 1;
    public static final int TAG_IMAGE_WIDTH = 2;
    public static final int TAG_COLOUR_PLANES = 3;
    public static final int TAG_BITS_PER_PIXEL = 4;
    public static final int TAG_COMPRESSION = 5;
    public static final int TAG_X_PIXELS_PER_METER = 6;
    public static final int TAG_Y_PIXELS_PER_METER = 7;
    public static final int TAG_PALETTE_COLOUR_COUNT = 8;
    public static final int TAG_IMPORTANT_COLOUR_COUNT = 9;
    public static final int TAG_RENDERING = 10;
    public static final int TAG_COLOR_ENCODING = 11;
    public static final int TAG_RED_MASK = 12;
    public static final int TAG_GREEN_MASK = 13;
    public static final int TAG_BLUE_MASK = 14;
    public static final int TAG_ALPHA_MASK = 15;
    public static final int TAG_COLOR_SPACE_TYPE = 16;
    public static final int TAG_GAMMA_RED = 17;
    public static final int TAG_GAMMA_GREEN = 18;
    public static final int TAG_GAMMA_BLUE = 19;
    public static final int TAG_INTENT = 20;
    public static final int TAG_LINKED_PROFILE = 21;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_BITMAP_TYPE, "Bitmap type");
        _tagNameMap.put(TAG_HEADER_SIZE, "Header Size");

        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_COLOUR_PLANES, "Planes");
        _tagNameMap.put(TAG_BITS_PER_PIXEL, "Bits Per Pixel");
        _tagNameMap.put(TAG_COMPRESSION, "Compression");
        _tagNameMap.put(TAG_X_PIXELS_PER_METER, "X Pixels per Meter");
        _tagNameMap.put(TAG_Y_PIXELS_PER_METER, "Y Pixels per Meter");
        _tagNameMap.put(TAG_PALETTE_COLOUR_COUNT, "Palette Colour Count");
        _tagNameMap.put(TAG_IMPORTANT_COLOUR_COUNT, "Important Colour Count");
        _tagNameMap.put(TAG_RENDERING, "Rendering");
        _tagNameMap.put(TAG_COLOR_ENCODING, "Color Encoding");
        _tagNameMap.put(TAG_RED_MASK, "Red Mask");
        _tagNameMap.put(TAG_GREEN_MASK, "Green Mask");
        _tagNameMap.put(TAG_BLUE_MASK, "Blue Mask");
        _tagNameMap.put(TAG_ALPHA_MASK, "Alpha Mask");
        _tagNameMap.put(TAG_COLOR_SPACE_TYPE, "Color Space Type");
        _tagNameMap.put(TAG_GAMMA_RED, "Red Gamma Curve");
        _tagNameMap.put(TAG_GAMMA_GREEN, "Green Gamma Curve");
        _tagNameMap.put(TAG_GAMMA_BLUE, "Blue Gamma Curve");
        _tagNameMap.put(TAG_INTENT, "Rendering Intent");
        _tagNameMap.put(TAG_LINKED_PROFILE, "Linked Profile File Name");
    }

    public BmpHeaderDirectory()
    {
        this.setDescriptor(new BmpHeaderDescriptor(this));
    }

    @Nullable
    public BitmapType getBitmapType() {
        Integer value = getInteger(TAG_BITMAP_TYPE);
        return value == null ? null : BitmapType.typeOf(value.intValue());
    }

    @Nullable
    public Compression getCompression() {
        return Compression.typeOf(this);
    }

    @Nullable
    public RenderingHalftoningAlgorithm getRendering() {
        Integer value = getInteger(TAG_RENDERING);
        return value == null ? null : RenderingHalftoningAlgorithm.typeOf(value.intValue());
    }

    @Nullable
    public ColorEncoding getColorEncoding() {
        Integer value = getInteger(TAG_COLOR_ENCODING);
        return value == null ? null : ColorEncoding.typeOf(value.intValue());
    }

    @Nullable
    public ColorSpaceType getColorSpaceType() {
        Long value = getLongObject(TAG_COLOR_SPACE_TYPE);
        return value == null ? null : ColorSpaceType.typeOf(value.longValue());
    }

    @Nullable
    public RenderingIntent getRenderingIntent() {
        Integer value = getInteger(TAG_INTENT);
        return value == null ? null : RenderingIntent.typeOf(value.intValue());
    }

    @Override
    @NotNull
    public String getName()
    {
        return "BMP Header";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    public enum BitmapType {

           /** "BM" - Windows or OS/2 bitmap */
        BITMAP(0x4D42),

        /** "BA" - OS/2 Bitmap array (multiple bitmaps) */
        OS2_BITMAP_ARRAY(0x4142),

            /** "IC" - OS/2 Icon */
        OS2_ICON(0x4349),

            /** "CI" - OS/2 Color icon */
        OS2_COLOR_ICON(0x4943),

        /** "CP" - OS/2 Color pointer */
        OS2_COLOR_POINTER(0x5043),

            /** "PT" - OS/2 Pointer */
        OS2_POINTER(0x5450);

        private final int value;

        private BitmapType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Nullable
        public static BitmapType typeOf(int value) {
            for (BitmapType bitmapType : BitmapType.values())
            {
                if (bitmapType.value == value)
                {
                    return bitmapType;
                }
            }
            return null;
        }

        @Override
        @NotNull
        public String toString() {
            switch (this) {
                case BITMAP: return "Standard";
                case OS2_BITMAP_ARRAY: return "Bitmap Array";
                case OS2_COLOR_ICON: return "Color Icon";
                case OS2_COLOR_POINTER: return "Color Pointer";
                case OS2_ICON: return "Monochrome Icon";
                case OS2_POINTER: return "Monochrome Pointer";
                default:
                    throw new IllegalStateException("Unimplemented bitmap type " + super.toString());
            }
        }
    }

    public enum Compression {

        /** 0 = None */
        BI_RGB(0),

        /** 1 = RLE 8-bit/pixel */
        BI_RLE8(1),

        /** 2 = RLE 4-bit/pixel */
        BI_RLE4(2),

        /** 3 = Bit fields (not OS22XBITMAPHEADER (size 64)) */
        BI_BITFIELDS(3),

        /** 3 = Huffman 1D (if OS22XBITMAPHEADER (size 64)) */
        BI_HUFFMAN_1D(3),

        /** 4 = JPEG (not OS22XBITMAPHEADER (size 64)) */
        BI_JPEG(4),

        /** 4 = RLE 24-bit/pixel (if OS22XBITMAPHEADER (size 64)) */
        BI_RLE24(4),

        /** 5 = PNG */
        BI_PNG(5),

        /** 6 = RGBA bit fields */
        BI_ALPHABITFIELDS(6),

        /** 11 = CMYK */
        BI_CMYK(11),

        /** 12 = CMYK RLE-8 */
        BI_CMYKRLE8(12),

        /** 13 = CMYK RLE-4 */
        BI_CMYKRLE4(13);

        private final int value;

        private Compression(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Nullable
        public static Compression typeOf(@NotNull BmpHeaderDirectory directory) {
            Integer value = directory.getInteger(TAG_COMPRESSION);
            if (value == null)
                return null;
            Integer headerSize = directory.getInteger(TAG_HEADER_SIZE);
            if (headerSize == null)
                return null;

            return typeOf(value, headerSize);
        }

        @Nullable
        public static Compression typeOf(int value, int headerSize) {
            switch (value) {
                case 0:  return BI_RGB;
                case 1:  return BI_RLE8;
                case 2:  return BI_RLE4;
                case 3:  return headerSize == 64 ? BI_HUFFMAN_1D : BI_BITFIELDS;
                case 4:  return headerSize == 64 ? BI_RLE24 : BI_JPEG;
                case 5:  return BI_PNG;
                case 6:  return BI_ALPHABITFIELDS;
                case 11: return BI_CMYK;
                case 12: return BI_CMYKRLE8;
                case 13: return BI_CMYKRLE4;
                default: return null;
            }
        }

        @Override
        @NotNull
        public String toString() {
            switch (this) {
                case BI_RGB:            return "None";
                case BI_RLE8:           return "RLE 8-bit/pixel";
                case BI_RLE4:           return "RLE 4-bit/pixel";
                case BI_BITFIELDS:      return "Bit Fields";
                case BI_HUFFMAN_1D:     return "Huffman 1D";
                case BI_JPEG:           return "JPEG";
                case BI_RLE24:          return "RLE 24-bit/pixel";
                case BI_PNG:            return "PNG";
                case BI_ALPHABITFIELDS: return "RGBA Bit Fields";
                case BI_CMYK:           return "CMYK Uncompressed";
                case BI_CMYKRLE8:       return "CMYK RLE-8";
                case BI_CMYKRLE4:       return "CMYK RLE-4";
                default:
                    throw new IllegalStateException("Unimplemented compression type " + super.toString());
            }
        }
    }

    public enum RenderingHalftoningAlgorithm {

        /** No halftoning algorithm */
        NONE(0),

        /** Error Diffusion Halftoning */
        ERROR_DIFFUSION(1),

        /** Processing Algorithm for Noncoded Document Acquisition */
        PANDA(2),

        /** Super-circle Halftoning */
        SUPER_CIRCLE(3);

        private final int value;

        private RenderingHalftoningAlgorithm(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Nullable
        public static RenderingHalftoningAlgorithm typeOf(int value) {
            for (RenderingHalftoningAlgorithm renderingHalftoningAlgorithm : RenderingHalftoningAlgorithm.values())
            {
                if (renderingHalftoningAlgorithm.value == value)
                {
                    return renderingHalftoningAlgorithm;
                }
            }
            return null;
        }

        @Override
        @NotNull
        public String toString() {
            switch (this) {
                case NONE:
                    return "No Halftoning Algorithm";
                case ERROR_DIFFUSION:
                    return "Error Diffusion Halftoning";
                case PANDA:
                    return "Processing Algorithm for Noncoded Document Acquisition";
                case SUPER_CIRCLE:
                    return "Super-circle Halftoning";
                default:
                    throw new IllegalStateException("Unimplemented rendering halftoning algorithm type " + super.toString());
            }
        }
    }

    public enum ColorEncoding {
        RGB(0);

        private final int value;

        private ColorEncoding(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Nullable
        public static ColorEncoding typeOf(int value) {
            return value == 0 ? RGB : null;
        }
    }

    public enum ColorSpaceType {

        /** 0 = Calibrated RGB */
        LCS_CALIBRATED_RGB(0L),

        /** "sRGB" = sRGB Color Space */
        LCS_sRGB(0x73524742L),

        /** "Win " = System Default Color Space, sRGB */
        LCS_WINDOWS_COLOR_SPACE(0x57696E20L),

        /** "LINK" = Linked Profile */
        PROFILE_LINKED(0x4C494E4BL),

        /** "MBED" = Embedded Profile */
        PROFILE_EMBEDDED(0x4D424544L);

        private final long value;

        private ColorSpaceType(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        @Nullable
        public static ColorSpaceType typeOf(long value) {
            for (ColorSpaceType colorSpaceType : ColorSpaceType.values())
            {
                if (colorSpaceType.value == value)
                {
                    return colorSpaceType;
                }
            }
            return null;
        }

        @Override
        @NotNull
        public String toString() {
            switch (this) {
                case LCS_CALIBRATED_RGB:
                    return "Calibrated RGB";
                case LCS_sRGB:
                    return "sRGB Color Space";
                case LCS_WINDOWS_COLOR_SPACE:
                    return "System Default Color Space, sRGB";
                case PROFILE_LINKED:
                    return "Linked Profile";
                case PROFILE_EMBEDDED:
                    return "Embedded Profile";
                default:
                    throw new IllegalStateException("Unimplemented color space type " + super.toString());
            }
        }
    }

    public enum RenderingIntent {

        /** Graphic, Saturation */
        LCS_GM_BUSINESS(1),

        /** Proof, Relative Colorimetric */
        LCS_GM_GRAPHICS(2),

        /** Picture, Perceptual */
        LCS_GM_IMAGES(4),

        /** Match, Absolute Colorimetric */
        LCS_GM_ABS_COLORIMETRIC(8);

        private final int value;

        private RenderingIntent(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Nullable
        public static RenderingIntent typeOf(long value) {
            for (RenderingIntent renderingIntent : RenderingIntent.values())
            {
                if (renderingIntent.value == value)
                {
                    return renderingIntent;
                }
            }
            return null;
        }

        @Override
        @NotNull
        public String toString() {
            switch (this) {
                case LCS_GM_BUSINESS:
                    return "Graphic, Saturation";
                case LCS_GM_GRAPHICS:
                    return "Proof, Relative Colorimetric";
                case LCS_GM_IMAGES:
                    return "Picture, Perceptual";
                case LCS_GM_ABS_COLORIMETRIC:
                    return "Match, Absolute Colorimetric";
                default:
                    throw new IllegalStateException("Unimplemented rendering intent " + super.toString());
            }
        }
    }
}
