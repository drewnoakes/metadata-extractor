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
package com.drew.imaging.png;

import com.drew.lang.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChunkType
{
    private static final Set<String> _identifiersAllowingMultiples
        = new HashSet<String>(Arrays.asList("IDAT", "sPLT", "iTXt", "tEXt", "zTXt"));

    //
    // Standard critical chunks
    //
    /**
     * Denotes a critical {@link PngChunk} that contains basic information about the PNG image.
     * This must be the first chunk in the data sequence, and may only occur once.
     * <p>
     * The format is:
     * <ul>
     *     <li><b>pixel width</b> 4 bytes, unsigned and greater than zero</li>
     *     <li><b>pixel height</b> 4 bytes, unsigned and greater than zero</li>
     *     <li><b>bit depth</b> 1 byte, number of bits per sample or per palette index (not per pixel)</li>
     *     <li><b>color type</b> 1 byte, maps to {@link PngColorType} enum</li>
     *     <li><b>compression method</b> 1 byte, currently only a value of zero (deflate/inflate) is in the standard</li>
     *     <li><b>filter method</b> 1 byte, currently only a value of zero (adaptive filtering with five basic filter types) is in the standard</li>
     *     <li><b>interlace method</b> 1 byte, indicates the transmission order of image data, currently only 0 (no interlace) and 1 (Adam7 interlace) are in the standard</li>
     * </ul>
     */
    public static final PngChunkType IHDR;

    /**
     * Denotes a critical {@link PngChunk} that contains palette entries.
     * This chunk should only appear for a {@link PngColorType} of <code>IndexedColor</code>,
     * and may only occur once in the PNG data sequence.
     * <p>
     * The chunk contains between one and 256 entries, each of three bytes:
     * <ul>
     *     <li><b>red</b> 1 byte</li>
     *     <li><b>green</b> 1 byte</li>
     *     <li><b>blue</b> 1 byte</li>
     * </ul>
     * The number of entries is determined by the chunk length. A chunk length indivisible by three is an error.
     */
    public static final PngChunkType PLTE;
    public static final PngChunkType IDAT;
    public static final PngChunkType IEND;

    //
    // Standard ancillary chunks
    //
    public static final PngChunkType cHRM;
    public static final PngChunkType gAMA;
    public static final PngChunkType iCCP;
    public static final PngChunkType sBIT;
    public static final PngChunkType sRGB;
    public static final PngChunkType bKGD;
    public static final PngChunkType hIST;
    public static final PngChunkType tRNS;
    public static final PngChunkType pHYs;
    public static final PngChunkType sPLT;
    public static final PngChunkType tIME;
    public static final PngChunkType iTXt;

    /**
     * Denotes an ancillary {@link PngChunk} that contains textual data, having first a keyword and then a value.
     * If multiple text data keywords are needed, then multiple chunks are included in the PNG data stream.
     * <p>
     * The format is:
     * <ul>
     *     <li><b>keyword</b> 1-79 bytes</li>
     *     <li><b>null separator</b> 1 byte (\0)</li>
     *     <li><b>text string</b> 0 or more bytes</li>
     * </ul>
     * Text is interpreted according to the Latin-1 character set [ISO-8859-1].
     * Newlines should be represented by a single linefeed character (0x9).
     */
    public static final PngChunkType tEXt;
    public static final PngChunkType zTXt;

    static {
        try {
            IHDR = new PngChunkType("IHDR");
            PLTE = new PngChunkType("PLTE");
            IDAT = new PngChunkType("IDAT", true);
            IEND = new PngChunkType("IEND");
            cHRM = new PngChunkType("cHRM");
            gAMA = new PngChunkType("gAMA");
            iCCP = new PngChunkType("iCCP");
            sBIT = new PngChunkType("sBIT");
            sRGB = new PngChunkType("sRGB");
            bKGD = new PngChunkType("bKGD");
            hIST = new PngChunkType("hIST");
            tRNS = new PngChunkType("tRNS");
            pHYs = new PngChunkType("pHYs");
            sPLT = new PngChunkType("sPLT", true);
            tIME = new PngChunkType("tIME");
            iTXt = new PngChunkType("iTXt", true);
            tEXt = new PngChunkType("tEXt", true);
            zTXt = new PngChunkType("zTXt", true);
        } catch (PngProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final byte[] _bytes;
    private final boolean _multipleAllowed;

    public PngChunkType(@NotNull String identifier) throws PngProcessingException
    {
        this(identifier, false);
    }

    public PngChunkType(@NotNull String identifier, boolean multipleAllowed) throws PngProcessingException
    {
        _multipleAllowed = multipleAllowed;

        try {
            byte[] bytes = identifier.getBytes("ASCII");
            validateBytes(bytes);
            _bytes = bytes;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to convert string code to bytes.");
        }
    }

    public PngChunkType(@NotNull byte[] bytes) throws PngProcessingException
    {
        validateBytes(bytes);
        _bytes = bytes;
        _multipleAllowed = _identifiersAllowingMultiples.contains(getIdentifier());
    }

    private static void validateBytes(byte[] bytes) throws PngProcessingException
    {
        if (bytes.length != 4) {
            throw new PngProcessingException("PNG chunk type identifier must be four bytes in length");
        }

        for (byte b : bytes) {
            if (!isValidByte(b)) {
                throw new PngProcessingException("PNG chunk type identifier may only contain alphabet characters");
            }
        }
    }

    public boolean isCritical()
    {
        return isUpperCase(_bytes[0]);
    }

    public boolean isAncillary()
    {
        return !isCritical();
    }

    public boolean isPrivate()
    {
        return isUpperCase(_bytes[1]);
    }

    public boolean isSafeToCopy()
    {
        return isLowerCase(_bytes[3]);
    }

    public boolean areMultipleAllowed()
    {
        return _multipleAllowed;
    }

    private static boolean isLowerCase(byte b)
    {
        return (b & (1 << 5)) != 0;
    }

    private static boolean isUpperCase(byte b)
    {
        return (b & (1 << 5)) == 0;
    }

    private static boolean isValidByte(byte b)
    {
        return (b >= 65 && b <= 90) || (b >= 97 && b <= 122);
    }

    public String getIdentifier()
    {
        try {
            return new String(_bytes, "ASCII");
        } catch (UnsupportedEncodingException e) {
            // The constructor should ensure that we're always able to encode the bytes in ASCII.
            // noinspection ConstantConditions
            assert(false);
            return "Invalid object instance";
        }
    }

    @Override
    public String toString()
    {
        return getIdentifier();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PngChunkType that = (PngChunkType)o;

        return Arrays.equals(_bytes, that._bytes);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(_bytes);
    }
}
