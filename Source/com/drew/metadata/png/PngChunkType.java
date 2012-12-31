package com.drew.metadata.png;

import com.drew.lang.annotations.NotNull;

import java.io.UnsupportedEncodingException;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class PngChunkType
{
    //
    // Standard critical chunks
    //
    public static final PngChunkType IHDR = new PngChunkType("IHDR");
    public static final PngChunkType PLTE = new PngChunkType("PLTE");
    public static final PngChunkType IDAT = new PngChunkType("IDAT", true);
    public static final PngChunkType IEND = new PngChunkType("IEND");

    //
    // Standard ancillary chunks
    //
    public static final PngChunkType cHRM = new PngChunkType("cHRM");
    public static final PngChunkType gAMA = new PngChunkType("gAMA");
    public static final PngChunkType iCCP = new PngChunkType("iCCP");
    public static final PngChunkType sBIT = new PngChunkType("sBIT");
    public static final PngChunkType sRGB = new PngChunkType("sRGB");
    public static final PngChunkType bKGD = new PngChunkType("bKGD");
    public static final PngChunkType hIST = new PngChunkType("hIST");
    public static final PngChunkType tRNS = new PngChunkType("tRNS");
    public static final PngChunkType pHYs = new PngChunkType("pHYs");
    public static final PngChunkType sPLT = new PngChunkType("sPLT", true);
    public static final PngChunkType tIME = new PngChunkType("tIME");
    public static final PngChunkType iTXt = new PngChunkType("iTXt", true);
    public static final PngChunkType tEXt = new PngChunkType("tEXt", true);
    public static final PngChunkType zTXt = new PngChunkType("zTXt", true);

    private final byte[] _bytes;
    private final boolean _multipleAllowed;

    public PngChunkType(@NotNull String identifier)
    {
        this(identifier, false);
    }

    public PngChunkType(@NotNull String identifier, boolean multipleAllowed)
    {
        _multipleAllowed = multipleAllowed;

        try {
            byte[] bytes = identifier.getBytes("ASCII");

            if (bytes.length != 4) {
                throw new IllegalArgumentException("PNG chunk type identifier must be four bytes in length");
            }

            for (byte b : bytes) {
                if (!isValidByte(b)) {
                    throw new IllegalArgumentException("PNG chunk type identifier may only contain alphabet characters");
                }
            }

            _bytes = bytes;

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to convert string code to bytes.");
        }
    }

    @Override
    public String toString()
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
}
