/*
 * EXIFExtractor.java
 *
 * This class based upon code from Jhead, a C program for extracting and
 * manipulating the Exif data within files written by Matthias Wandel.
 *   http://www.sentex.net/~mwandel/jhead/
 *
 * Jhead is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.  Unlike
 * Jhead, this code (as it stands) only supports reading of Exif data - no
 * manipulation, and no thumbnail stuff.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 28 April 2002, 23:54
 * Modified 04 Aug 2002
 * - Renamed constants to be inline with changes to ExifTagValues interface
 * - Substituted usage of JDK 1.4 features (java.nio package)
 */
package com.drew.imaging.exif;

import java.io.*;
import java.util.Iterator;
import com.sun.image.codec.jpeg.*;

/**
 * Extracts Exif data from a JPEG header segment, providing information about the
 * camera/scanner/capture device (if available).  Information is encapsulated in 
 * an <code>ImageInfo</code> object.
 * @author  Drew Noakes drew.noakes@drewnoakes.com
 */
public class ExifExtractor implements ExifTagValues 
{
    /**
     * The JPEG segment as an array of bytes.
     */
    private byte[] data;

    /**
     * Represents the native byte ordering used in the JPEG segment.  If true,
     * then we're using Motorolla ordering (Big endian), else we're using Intel
     * ordering (Little endian).
     */
    private boolean motorollaByteOrder;

    /**
     * Bean instance to store information about the image and camera/scanner/capture
     * device.
     */
    private ImageInfo info;
    
    /**
     * Flag to dictate the printing of debug messages to <code>System.out</code>.
     */
    private static final boolean DEBUG = false;
    
    /**
     * Flag to dictate the printing of extracted image information to
     * <code>System.out</code>.
     */
    private static final boolean PRINT_TAGS = false;
    
    /**
     * The number of bytes used per format descriptor.
     */
    static int[] bytesPerFormat = {0,1,1,2,4,8,1,1,2,4,8,4,8};

    /**
     * The number of formats known.
     */
    private static final int NUM_FORMATS = 12;

    // the format enumeration
    private static final int FMT_BYTE = 1;
    private static final int FMT_STRING = 2;
    private static final int FMT_USHORT = 3;
    private static final int FMT_ULONG = 4;
    private static final int FMT_URATIONAL = 5;
    private static final int FMT_SBYTE = 6;
    private static final int FMT_UNDEFINED = 7;
    private static final int FMT_SSHORT = 8;
    private static final int FMT_SLONG = 9;
    private static final int FMT_SRATIONAL = 10;
    private static final int FMT_SINGLE = 11;
    private static final int FMT_DOUBLE = 12;

    /**
     * Marker variable, potentially used later for help finding start of thumbnail data.
     */
    private int lastExifRefd;
    
    /**
     * Creates an ExifExtractor for the given JPEG header segment.
     */
    public ExifExtractor(byte[] data)
    {
        this.data = data;
        this.info = new ImageInfo();
    }

    /**
     * Creates an ExifExtractor for the given <code>JPEGDecodeParam</code> object.
     */
    public ExifExtractor(JPEGDecodeParam param)
    {
        if (param==null) {
            throw new IllegalArgumentException("param cannot be null");
        }

        /* We should only really be seeing Exif in data[0]... the 2D array exists
         * because markers can theoretically appear multiple times in the file.
         */
        byte[][] data = param.getMarkerData(JPEGDecodeParam.APP1_MARKER);
        if (data!=null) {
            this.data = data[0];
        }
        this.info = new ImageInfo();
    }

    /**
     * Performs the Exif data extraction, returning an instance of
     * <code>ImageInfo</code>.
     * @throws ExifProcessingException for bad/unexpected Exif data
     */
    public ImageInfo extract() throws ExifProcessingException
    {
        //double FocalplaneXRes;
        //double FocalplaneUnits;
        //int ExifImageWidth;
        
        if (data==null) {
            throw new ExifProcessingException("Image doesn't contain any Exif data");
        }
        
        if (!"Exif\0\0".equals(new String(data, 0, 6))) {
            throw new ExifProcessingException("Exif data segment doesn't being with 'Exif'");
        }
        
        if (DEBUG) System.out.println("EXIF header is "+data.length+" bytes long");
        
        if ("MM".equals(new String(data, 6, 2))) {
            if (DEBUG) System.out.println("Motorola byte ordering (Big Endian)");
            //byteOrder = ByteOrder.BIG_ENDIAN;
            motorollaByteOrder = true;
        } else if ("II".equals(new String(data, 6, 2))) {
            if (DEBUG) System.out.println("Intel byte ordering (Little Endian)");
            //byteOrder = ByteOrder.LITTLE_ENDIAN;
            motorollaByteOrder = false;
        } else {
            throw new ExifProcessingException("Unclear distinction between Motorola/Intel byte ordering");
        }

        // Check the next two values for correctness.
        if (get16Bits(8)!=0x2a) {
            throw new ExifProcessingException("Invalid Exif start (1)");
        }
        if (get32Bits(10)!=0x08) {
            throw new ExifProcessingException("Invalid Exif start (2)");
        }
        
        // First directory starts 14 bytes in.  All offset are relative to 6 bytes in.
        processExifDir(14, 6);
        
        return info;
    }

    /**
     * Process one of the nested EXIF directories.
     */
    private void processExifDir(int dirStartOffset, int offsetBase) throws ExifProcessingException
    {
        int dirEntryCount = get16Bits(dirStartOffset);

        if (DEBUG) System.out.println(dirEntryCount+" dir entries");
        
        int dirLength = getDirEntryAddress(dirStartOffset, dirEntryCount);

        if (dirLength+4 > (offsetBase+data.length)) {
            if (dirLength+2 == offsetBase+data.length || dirLength == offsetBase+data.length) {
                // Version 1.3 of jhead would truncate a bit too much.
                // This also caught later on as well.
            } else {
                // Note: Files that had thumbnails trimmed with jhead 1.3 or earlier
                // might trigger this. 
                throw new ExifProcessingException("Illegally sized directory");
            }
        }
        if (dirLength < lastExifRefd) {
            lastExifRefd = dirLength;
        }

        for (int dirEntry=0; dirEntry<dirEntryCount; dirEntry++) {
            int dirEntryOffset = getDirEntryAddress(dirStartOffset, dirEntry);
            int tagType = get16Bits(dirEntryOffset);
            int formatCode = get16Bits(dirEntryOffset+2);
            int components = get32Bits(dirEntryOffset+4);

            if ((formatCode-1) >= NUM_FORMATS) {
                // (-1) catches illegal zero case as unsigned underflows to positive large.
                throw new ExifProcessingException("Illegal format code in EXIF dir");
            }

            int byteCount = components * bytesPerFormat[formatCode];
            int valueOffset;
            
            if (byteCount > 4) {
                // If its bigger than 4 bytes, the dir entry contains an offset.
                int offsetVal = get32Bits(dirEntryOffset+8);
                if (offsetVal + byteCount > data.length) {
                    // Bogus pointer offset and / or bytecount value
                    throw new ExifProcessingException("Illegal pointer offset value in EXIF");
                }
                valueOffset = offsetBase + offsetVal;
            } else {
                // 4 bytes or less and value is in the dir entry itself
                valueOffset = dirEntryOffset + 8;
            }
            
            if (lastExifRefd < valueOffset+byteCount){
                // Keep track of last byte in the exif header that was actually referenced.
                // That way, we know where the discardable thumbnail data begins.
                lastExifRefd = valueOffset+byteCount;
            }
            
            // Print tag details to the console, if flagged to do so
            if (PRINT_TAGS) {
                switch(formatCode) {
                    case FMT_UNDEFINED:
                        // Undefined is typically an ASCII string
                    case FMT_STRING:
                        // String arrays printed without function call (different from int arrays)
                        boolean printing = true;
                        System.out.print(Integer.toHexString(tagType)+" "+ImageInfo.getTagName(tagType)+" ");
                        System.out.print("\"");
                        for (int i=0; i<byteCount; i++) {
                            char c = (char)data[valueOffset+i];
                            if (c>=32 && c<=127) {
                                System.out.print(c);
                                printing = true;
                            } else if (c=='\0') {
                                break;
                            } else if (printing) {
                                // Avoiding indicating too many unprintable characters of proprietary
                                // bits of binary information this program may know how to parse.
                                System.out.print('?');
                                printing = false;
                            }
                        }
                        System.out.println("\" ");
                        break;

                    default:
                        // Handle arrays of numbers later (will there ever be?)
                        String formatted = formatNumber(valueOffset, formatCode);
                        System.out.print(Integer.toHexString(tagType)+" "+ImageInfo.getTagName(tagType)+" ");
                        System.out.println(formatted);
                        break;
                }
            }
            
            // Extract useful components of tag
            // If the format is rational, give it priority and treat it uniquely...
            if (formatCode==FMT_SRATIONAL || formatCode==FMT_URATIONAL) {
                info.setRational(tagType, get32Bits(valueOffset), get32Bits(valueOffset+4));
            } else {
                switch (tagType) {
                    case TAG_MAKE:
                        info.setString(TAG_MAKE, readString(valueOffset, 31));
                        break;

                    case TAG_MODEL:
                        info.setString(TAG_MODEL, readString(valueOffset, 39));
                        break;

                    case TAG_DATETIME_ORIGINAL:
                        info.setString(TAG_DATETIME_ORIGINAL, readString(valueOffset, 19));
                        break;

                    case TAG_USER_COMMENT:
                        // Olympus has this padded with trailing spaces.  Remove these first.
                        for (int i=byteCount; i>0; i--) {
                            if (data[valueOffset+i] == ' ') {
                                data[valueOffset+i] = (byte)'\0';
                            } else {
                                break;
                            }
                        }

                        // Copy the comment
                        if ("ASCII".equals(new String(data, valueOffset, 5))) {
                            for (int i=5; i<10; i++) {
                                byte b = data[valueOffset+i];
                                if (b!='\0' && b!=' ') {
                                    info.setString(TAG_USER_COMMENT, readString(valueOffset+i, 199));
                                    break;
                                }
                            }
                        } else {
                            info.setString(TAG_USER_COMMENT, readString(valueOffset, 199));
                        }
                        break;

                    // This value is usually Rational, so is handled above... however for completeness
                    // this is included
                    case TAG_X_RESOLUTION:
                    case TAG_Y_RESOLUTION:
                        info.setDouble(tagType, convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_RESOLUTION_UNIT:
                    case TAG_ORIENTATION:
                    case TAG_YCBCR_POSITIONING:
                        info.setInt(tagType, (int)data[valueOffset]);
                        break;

                    case TAG_FNUMBER:
                        // Simplest way of expressing aperture, so I trust it the most.
                        // (overwrite previously computed value if there is one)
                        //info.setApertureFNumber((float)convertNumber(valueOffset, formatCode));
                        info.setFloat(TAG_FNUMBER, (float)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_APERTURE:
                    case TAG_MAX_APERTURE:
                        // More relevant info always comes earlier, so only use this field if we don't 
                        // have appropriate aperture information yet.
                        if (!info.containsTag(TAG_FNUMBER)) {
                            //info.setApertureFNumber((float)Math.exp(convertNumber(valueOffset, formatCode)*Math.log(2)*0.5));
                            info.setFloat(TAG_FNUMBER, (float)Math.exp(convertNumber(valueOffset, formatCode)*Math.log(2)*0.5));
                        }
                        break;

                    case TAG_FOCAL_LENGTH:
                        // Nice digital cameras actually save the focal length as a function
                        // of how far they are zoomed in.
                        info.setFloat(TAG_FOCAL_LENGTH, (float)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_SUBJECT_DISTANCE:
                        // Inidcates the distance the autofocus camera is focused to.
                        // Tends to be less accurate as distance increases.
                        info.setFloat(TAG_SUBJECT_DISTANCE, (float)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_EXPOSURE_TIME:
                        // Simplest way of expressing exposure time, so I trust it most.
                        // (overwrite previously computd value if there is one)
                        info.setFloat(TAG_EXPOSURE_TIME, (float)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_SHUTTER_SPEED:
                        // More complicated way of expressing exposure time, so only use
                        // this value if we don't already have it from somewhere else.
                        if (!info.containsTag(TAG_EXPOSURE_TIME)) {
                            info.setFloat(TAG_EXPOSURE_TIME, (float)(1 / Math.exp(convertNumber(valueOffset, formatCode)*Math.log(2))));
                        }
                        break;

                    case TAG_FLASH:
/*
                        if (convertNumber(valueOffset, formatCode)==0) {
                            //info.setFlashUsed(false);
                            info.setBoolean(TAG_FLASH, false);
                        } else {
                            info.setBoolean(TAG_FLASH, true);
                        }
*/
                        int val = (int)convertNumber(valueOffset, formatCode);
                        info.setInt(TAG_FLASH, val);
                        break;

                    case TAG_EXIF_IMAGE_HEIGHT:
                    case TAG_EXIF_IMAGE_WIDTH:
                        info.setInt(tagType, (int)convertNumber(valueOffset, formatCode));
                        break;
/*
                    case TAG_FOCALPLANEXRES:
                        FocalplaneXRes = convertNumber(valueOffset, formatCode);
                        break;

                    case TAG_FOCALPLANEUNITS:
                        switch((int)convertNumber(valueOffset, formatCode)){
                            case 1: FocalplaneUnits = 25.4; break; // inch
                            case 2:
                                // According to the information I was using, 2 means meters.
                                // But looking at the Cannon powershot's files, inches is the only
                                // sensible value.
                                FocalplaneUnits = 25.4;
                                break;

                            case 3: FocalplaneUnits = 10;   break;  // centimeter
                            case 4: FocalplaneUnits = 1;    break;  // milimeter
                            case 5: FocalplaneUnits = .001; break;  // micrometer
                        }
                        break;
*/
                    // Remaining cases contributed to jhead by: Volker C. Schoech (schoech@gmx.de)

                    case TAG_EXPOSURE_BIAS:
                        info.setFloat(tagType, (float)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_WHITE_BALANCE:
                    case TAG_METERING_MODE:
                    case TAG_EXPOSURE_PROGRAM:
                    case TAG_COMPRESSION_LEVEL:
                        info.setInt(tagType, (int)convertNumber(valueOffset, formatCode));
                        break;

                    case TAG_ISO_EQUIVALENT:
                        int isoEquiv = (int)convertNumber(valueOffset, formatCode);
                        if (isoEquiv < 50) {
                            isoEquiv *= 200;
                        }
                        info.setInt(tagType, isoEquiv);
                        break;

    /*
                    case TAG_THUMBNAIL_OFFSET:
                        ThumbnailOffset = (unsigned)convertNumber(valueOffset, formatCode);
                        DirWithThumbnailPtrs = DirStart;
                        break;

                    case TAG_THUMBNAIL_LENGTH:
                        ThumbnailSize = (unsigned)convertNumber(valueOffset, formatCode);
                        break;
    */
                    case TAG_EXIF_OFFSET:
                    case TAG_INTEROP_OFFSET:
                        int subdirOffset = offsetBase + get32Bits(valueOffset);
                        if (subdirOffset < offsetBase || subdirOffset > offsetBase+data.length) {
                            throw new ExifProcessingException("Illegal subdirectory link 1");
                        } else {
                            processExifDir(subdirOffset, offsetBase);
                        }
                        continue;

                    default:
                        String formattedString = formatNumber(valueOffset, formatCode);
                        if (formattedString!=null) {
                            info.setString(tagType, formattedString);
                        }
                        break;
                }
            }
            
            /* In addition to linking to subdirectories via exif tags, there's also a
             * potential link to another directory at the end of each directory.
             */
            if (getDirEntryAddress(dirStartOffset, dirEntryCount) + 4 <= offsetBase+data.length){
                int offset = get32Bits(dirStartOffset+2+12*dirEntryCount);
                if (offset!=0) {
                    int subdirOffset = offsetBase + offset;
                    if (subdirOffset > offsetBase+data.length){
                        if (subdirOffset < offsetBase+data.length+20){
                            /* Jhead 1.3 or earlier would crop the whole directory!
                             * As Jhead produces this form of format incorrectness, 
                             * I'll just let it pass silently
                             */
                            if (PRINT_TAGS) System.out.println("Thumbnail removed with Jhead 1.3 or earlier\n");
                        } else {
                            throw new ExifProcessingException("Illegal subdirectory link 2");
                        }
                    } else {
                        if (subdirOffset <= offsetBase+data.length) {
                            processExifDir(subdirOffset, offsetBase);
                        }
                    }
                }
            } else {
                // The exif header ends before the last next directory pointer.
            }        
        
        }
    }

    /**
     * Creates a String from the data buffer starting at the specified offset,
     * and ending where byte=='\0' or where length==maxLength.
     */
    private String readString(int offset, int maxLength)
    {
        int length = 0;
        while (data[offset+length]!='\0' && length<maxLength) {
            length++;
        }
        return new String(data, offset, length);
    }
    
    /**
     * Fashions the value at the given offset into String format, assuming the
     * provided format code.
     * <p>
     * Format codes are <code>FMT_BYTE</code>, <code>FMT_URATIONAL</code>, <code>FMT_DOUBLE</code>,
     * etc...
     */
    private String formatNumber(int offset, int formatCode)
    {
        StringBuffer sbuff = new StringBuffer();
        switch (formatCode) {
            case FMT_SBYTE:
            case FMT_BYTE:
//              sbuff.append("%02x ",*(uchar *)ValuePtr);             
                sbuff.append(data[offset]);             
                break;
            case FMT_USHORT:    
//              sbuff.append("%d\n",Get16u(ValuePtr));                
                sbuff.append(get16Bits(offset));                
                break;
            case FMT_ULONG:     
            case FMT_SLONG:     
//              sbuff.append("%d\n",Get32s(ValuePtr));                
                sbuff.append(get32Bits(offset));                
                break;
            case FMT_SSHORT:    
//              sbuff.append("%hd\n",(signed short)Get16u(ValuePtr)); 
                sbuff.append((short)get16Bits(offset)); 
                break;
            case FMT_URATIONAL:
            case FMT_SRATIONAL: 
//             sbuff.append("%d/%d\n",Get32s(ValuePtr), Get32s(4+(char *)ValuePtr)); 
               sbuff.append(get32Bits(offset)+"/"+get32Bits(offset+4)); 
               break;
            case FMT_SINGLE:    
//              sbuff.append("%f\n",(double)*(float *)ValuePtr);   
                sbuff.append((double)data[offset] );
                break;
            case FMT_DOUBLE:    
//              printf("%f\n",*(double *)ValuePtr);          
                sbuff.append((double)data[offset]);          
                break;
            case FMT_STRING:    
                sbuff.append(readString(offset, 255));
                break;
            default: 
//              sbuff.append("Unknown format "+formatCode);
//              break;
                // this format code is screwey... simply return null and we'll deal with presentation elsewhere
                return null;
        }
        return sbuff.toString();
    }
    
    /**
     * Evaluates the value at the given offset, assuming the provided format code.  Values are
     * returned as <code>double</code> primitives, which should be cast to the desired type.
     * <p>
     * Format codes are <code>FMT_BYTE</code>, <code>FMT_URATIONAL</code>, <code>FMT_DOUBLE</code>,
     * etc...
     */
    private double convertNumber(int offset, int formatCode)
    {
        double value = 0;

        switch (formatCode) {
            case FMT_SBYTE:     
                value = data[offset];  
                break;
            case FMT_BYTE:      
                value = (char)data[offset];        
                break;

            case FMT_USHORT:
                value = get16Bits(offset);          
                break;
            case FMT_ULONG:     
                value = get32Bits(offset);          
                break;

            case FMT_URATIONAL:
            case FMT_SRATIONAL: 
                int numerator = get32Bits(offset);
                int denominator = get32Bits(offset+4);
                if (denominator==0) {
                    value = 0;
                } else {
                    value = (double)numerator/(double)denominator;
                }
                break;

            case FMT_SSHORT:    
                value = get16Bits(offset);          
                break;
            case FMT_SLONG:     
                value = get32Bits(offset);          
                break;

            // Not sure if this is correct (never seen float used in Exif format)
            case FMT_SINGLE:    
                value = (double)data[offset];      
                break;
            case FMT_DOUBLE:    
                value = (double)data[offset];    
                break;
        }
        return value;
    }
    
    /**
     * Simple formula to get a given directory's entry address.
     */
    private int getDirEntryAddress(int start, int entry)
    {
        return (start+2+12*(entry));
    }
    
    /**
     * Get a 16 bit value from file's native byte order.  Between 0x0000 and 0xFFFF
     */
    private int get16Bits(int pos)
    {
        if (motorollaByteOrder) {
            // Motorola big first
            return (data[pos]<<8&0xFF00) | (data[pos+1]&0xFF);
        } else {
            // Intel ordering
            return (data[pos+1]<<8&0xFF00) | (data[pos]&0xFF);
        }
    }

    /**
     * Get a 32 bit value from file's native byte order.
     */
    private int get32Bits(int pos)
    {
        if (motorollaByteOrder) {
            // Motorola big first
            return (data[pos]<<24&0xFF000000) |
                    (data[pos+1]<<16&0xFF0000) |
                    (data[pos+2]<<8&0xFF00) |
                    (data[pos+3]&0xFF);
        } else {
            // Intel ordering
            return (data[pos+3]<<24&0xFF000000) |
                    (data[pos+2]<<16&0xFF0000) |
                    (data[pos+1]<<8&0xFF00) |
                    (data[pos]&0xFF);
        }
    }

    /**
     * Entry point for testing and comand line usage.
     */
    public static void main(String[] args) throws Exception
    {
        // expecting one argument exactly
        if (args.length!=1) {
            printUsage();
            System.exit(1);
        }
        // create a file from the argument, and make sure it's valid and readable
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            System.out.println("Cannot read from the specified file");
            printUsage();
            System.exit(1);
        }
        
        // load the data and extract exif info
        ImageInfo info = ExifLoader.getImageInfo(file);

        // iterate over the exif data and print to System.out
        Iterator i = info.getTagIterator();
        while (i.hasNext()) {
            int tagType = ((Integer)i.next()).intValue();
            System.out.println(ImageInfo.getTagName(tagType) +
                               ": " +
                               info.getDescription(tagType)
            );
        }
    }
    
    /**
     * Prints an explanatory message describing usage to the console.
     */
    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("\tjava com.drew.imaging.exif.ExifExtractor <filename>");
        System.out.println("\tjava -jar exifExtractor.jar <filename>");
        System.out.println();
    }
}
