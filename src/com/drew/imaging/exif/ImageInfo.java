/*
 * ImageInfo.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the 
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 * 
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 28 April 2002, 17:40
 */
package com.drew.imaging.exif;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 * @author  Drew Noakes drew.noakes@drewnoakes.com
 */
public final class ImageInfo implements ExifTagValues
{
    /**
     * static Map for lookup of Exif tag names (such as 'Model' or 'XResolution'
     * by Integer tag codes.
     */
    private static final HashMap tagMap = new HashMap();
    static {
        tagMap.put(new Integer(0x100), "ImageWidth");
        tagMap.put(new Integer(0x101), "ImageLength");
        tagMap.put(new Integer(0x102), "BitsPerSample");
        tagMap.put(new Integer(0x103), "Compression");
        tagMap.put(new Integer(0x106), "PhotometricInterpretation");
        tagMap.put(new Integer(0x10A), "FillOrder");
        tagMap.put(new Integer(0x10D), "DocumentName");
        tagMap.put(new Integer(0x10E), "ImageDescription");
        tagMap.put(new Integer(0x10F), "Make");
        tagMap.put(new Integer(0x110), "Model");
        tagMap.put(new Integer(0x111), "StripOffsets");
        tagMap.put(new Integer(0x112), "Orientation");
        tagMap.put(new Integer(0x115), "SamplesPerPixel");
        tagMap.put(new Integer(0x116), "RowsPerStrip");
        tagMap.put(new Integer(0x117), "StripByteCounts");
        tagMap.put(new Integer(0x11A), "XResolution");
        tagMap.put(new Integer(0x11B), "YResolution");
        tagMap.put(new Integer(0x11C), "PlanarConfiguration");
        tagMap.put(new Integer(0x128), "ResolutionUnit");
        tagMap.put(new Integer(0x12D), "TransferFunction");
        tagMap.put(new Integer(0x131), "Software");
        tagMap.put(new Integer(0x132), "DateTime");
        tagMap.put(new Integer(0x13B), "Artist");
        tagMap.put(new Integer(0x13E), "WhitePoint");
        tagMap.put(new Integer(0x13F), "PrimaryChromaticities");
        tagMap.put(new Integer(0x156), "TransferRange");
        tagMap.put(new Integer(0x200), "JPEGProc");
        tagMap.put(new Integer(0x201), "ThumbnailOffset");
        tagMap.put(new Integer(0x202), "ThumbnailLength");
        tagMap.put(new Integer(0x211), "YCbCrCoefficients");
        tagMap.put(new Integer(0x212), "YCbCrSubSampling");
        tagMap.put(new Integer(0x213), "YCbCrPositioning");
        tagMap.put(new Integer(0x214), "ReferenceBlackWhite");
        tagMap.put(new Integer(0x828D), "CFARepeatPatternDim");
        tagMap.put(new Integer(0x828E), "CFAPattern");
        tagMap.put(new Integer(0x828F), "BatteryLevel");
        tagMap.put(new Integer(0x8298), "Copyright");
        tagMap.put(new Integer(0x829A), "ExposureTime");
        tagMap.put(new Integer(0x829D), "FNumber");
        tagMap.put(new Integer(0x83BB), "IPTC/NAA");
        tagMap.put(new Integer(0x8769), "ExifOffset");
        tagMap.put(new Integer(0x8773), "InterColorProfile");
        tagMap.put(new Integer(0x8822), "ExposureProgram");
        tagMap.put(new Integer(0x8824), "SpectralSensitivity");
        tagMap.put(new Integer(0x8825), "GPSInfo");
        tagMap.put(new Integer(0x8827), "ISOSpeedRatings");
        tagMap.put(new Integer(0x8828), "OECF");
        tagMap.put(new Integer(0x9000), "ExifVersion");
        tagMap.put(new Integer(0x9003), "DateTimeOriginal");
        tagMap.put(new Integer(0x9004), "DateTimeDigitized");
        tagMap.put(new Integer(0x9101), "ComponentsConfiguration");
        tagMap.put(new Integer(0x9102), "CompressedBitsPerPixel");
        tagMap.put(new Integer(0x9201), "ShutterSpeedValue");
        tagMap.put(new Integer(0x9202), "ApertureValue");
        tagMap.put(new Integer(0x9203), "BrightnessValue");
        tagMap.put(new Integer(0x9204), "ExposureBiasValue");
        tagMap.put(new Integer(0x9205), "MaxApertureValue");
        tagMap.put(new Integer(0x9206), "SubjectDistance");
        tagMap.put(new Integer(0x9207), "MeteringMode");
        tagMap.put(new Integer(0x9208), "LightSource");
        tagMap.put(new Integer(0x9209), "Flash");
        tagMap.put(new Integer(0x920A), "FocalLength");
        tagMap.put(new Integer(0x927C), "MakerNote");
        tagMap.put(new Integer(0x9286), "UserComment");
        tagMap.put(new Integer(0x9290), "SubSecTime");
        tagMap.put(new Integer(0x9291), "SubSecTimeOriginal");
        tagMap.put(new Integer(0x9292), "SubSecTimeDigitized");
        tagMap.put(new Integer(0xA000), "FlashPixVersion");
        tagMap.put(new Integer(0xA001), "ColorSpace");
        tagMap.put(new Integer(0xA002), "ExifImageWidth");
        tagMap.put(new Integer(0xA003), "ExifImageLength");
        tagMap.put(new Integer(0xA005), "InteroperabilityOffset");
        // 0x920B in TIFF/EP
        tagMap.put(new Integer(0xA20B), "FlashEnergy");
        // 0x920C    -  -
        tagMap.put(new Integer(0xA20C), "SpatialFrequencyResponse");
        // 0x920E    -  -
        tagMap.put(new Integer(0xA20E), "FocalPlaneXResolution");
        // 0x920F    -  -
        tagMap.put(new Integer(0xA20F), "FocalPlaneYResolution");
        // 0x9210    -  -
        tagMap.put(new Integer(0xA210), "FocalPlaneResolutionUnit");
        // 0x9214    -  -
        tagMap.put(new Integer(0xA214), "SubjectLocation");
        // 0x9215    -  -
        tagMap.put(new Integer(0xA215), "ExposureIndex");
        // 0x9217    -  -
        tagMap.put(new Integer(0xA217), "SensingMethod");
        tagMap.put(new Integer(0xA300), "FileSource");
        tagMap.put(new Integer(0xA301), "SceneType");
    }

    public static String getTagName(int tagType)
    {
        String tagName = (String)tagMap.get(new Integer(tagType));
        if (tagName==null) {
            tagName = "Unknown tag (0x"+Integer.toHexString(tagType)+")";
        }
        return tagName;
    }

    /**
     * Image attributes are stored here, hashed by the Integer representation of the 'tagType'
     */
    private HashMap valueMap = new HashMap();

    /**
     * Lists tag values set against this object, assisting creation of an Iterator.
     */
    private final ArrayList definedTagList = new ArrayList();

    /**
     * Creates a new instance of ImageInfo.  Package private.
     */
    ImageInfo()
    {
        
    }
    
    /**
     * Sets an int value for the specified Exif tag.
     */
    public void setInt(int tagType, int value)
    {
        addObject(tagType, new Integer(value));
    }

    /**
     * Sets a double value for the specified Exif tag.
     */
    public void setDouble(int tagType, double value)
    {
        addObject(tagType, new Double(value));
    }

    /**
     * Sets a float value for the specified Exif tag.
     */
    public void setFloat(int tagType, float value)
    {
        addObject(tagType, new Float(value));
    }

    /**
     * Sets an int value for the specified Exif tag.
     */
    public void setString(int tagType, String value)
    {
        addObject(tagType, value);
    }

    /**
     * Sets an int value for the specified Exif tag.
     */
    public void setBoolean(int tagType, boolean value)
    {
        addObject(tagType, new Boolean(value));
    }
    
    /**
     * Sets a long value for the specified Exif tag.
     */
    public void setLong(int tagType, long value)
    {
        addObject(tagType, new Long(value));
    }

    /**
     * Sets a java.util.Date value for the specified Exif tag.
     */
    public void setDate(int tagType, java.util.Date value)
    {
        addObject(tagType, value);
    }

    /**
     * Sets a Rational value for the specified Exif tag.
     */
    public void setRational(int tagType, int numerator, int denominator)
    {
        addObject(tagType, new Rational(numerator, denominator));
    }
    
    /**
     * Contains common functionality for all 'add' methods.
     */
    private void addObject(int tagType, Object value)
    {
        Integer tag = new Integer(tagType);
        if (!valueMap.containsKey(tag)) {
            definedTagList.add(tag);
        }
        valueMap.put(tag, value);
    }
    
    /**
     * Creates an Iterator over the tag values set against this object, preserving the order
     * in which they were set.  Should the same tag have been set more than once, it's first
     * position is maintained, even though the final value is used.
     */
    public Iterator getTagIterator()
    {
        return definedTagList.iterator();
    }
    
    /**
     * Returns a count of unique tag types which have been set.
     */
    public int countTags()
    {
        return definedTagList.size();
    }
    
    /**
     * Returns whether the specified tag type has been set.
     */
    public boolean containsTag(int tagType)
    {
        return valueMap.containsKey(new Integer(tagType));
    }

    /**
     * Returns the specified tag's value as an int, if possible.
     */
    public int getInt(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Integer.parseInt((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).intValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to int");
        }
    }

    /**
     * Returns the specified tag's value as a double, if possible.
     */
    public double getDouble(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Double.parseDouble((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).doubleValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to double");
        }
    }

    /**
     * Returns the specified tag's value as a float, if possible.
     */
    public float getFloat(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Float.parseFloat((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).floatValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to float");
        }
    }

    /**
     * Returns the specified tag's value as a long, if possible.
     */
    public long getLong(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Long.parseLong((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).longValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to long");
        }
    }
    
    /**
     * Returns the specified tag's value as a String.  This is the only 'get' method
     * that shouldn't throw an exception.
     * @returns the String reprensentation of the tag's value, or <code>null</code> if
     *          the tag hasn't been defined.
     */
    public String getString(int tagType)
    {
        Object val = valueMap.get(new Integer(tagType));
        if (val==null) {
            return null;
        } else {
            return val.toString();
        }
    }
    
    /**
     * Returns the specified tag's value as a boolean, if possible.
     */
    public boolean getBoolean(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            return Boolean.getBoolean((String)o);
        } else if (o instanceof Number) {
            return (((Number)o).doubleValue()!=0);
        } else {
            throw new RuntimeException("Requested tag cannot be cast to boolean");
        }
    }
    
    /**
     * Returns the specified tag's value as a java.util.Date, if possible.
     */
    public java.util.Date getDate(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof java.util.Date) {
            return (java.util.Date)o;
        } else {
            throw new RuntimeException("Requested tag cannot be cast to java.util.Date");
        }
    }
    
    /**
     * Returns the specified tag's value as a Rational, if possible.
     */
    public Rational getRational(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof Rational) {
            return (Rational)o;
        } else {
            throw new RuntimeException("Requested tag cannot be cast to Rational");
        }
    }
}
