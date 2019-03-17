package com.drew.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.drew.lang.SequentialByteArrayReader;

/**
 * A limited-functionality binary property list (BPLIST) utility for Apple makernotes.
 * Parser functionality accounts for &quot;dict&quot; (with simple integer and string values) and &quot;data&quot;.
 * 
 * @author Bob Johnson
 * @see https://opensource.apple.com/source/CF/CF-550/ForFoundationOnly.h
 * @see https://opensource.apple.com/source/CF/CF-550/CFBinaryPList.c
 * @see https://synalysis.com/how-to-decode-apple-binary-property-list-files/
 */
public class BinaryPropertyListUtil 
{
    private static final String PLIST_DTD = "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";

    private static final byte[] BPLIST_HEADER = { 'b', 'p', 'l', 'i', 's', 't', '0', '0' };
    
    /**
     * Parse and return the binary property list in XML format.
     * 
     * @param bplist A byte array containing the binary property list.
     * @return
     * @throws IOException Thrown if the parse fails.
     */
	public static String parseAsXML(byte[] bplist) throws IOException
	{
		final PropertyListResults results = parse(bplist);
    	final Object topObject = results.getObjects().get((int) results.getTrailer().topObject);

        final StringBuilder xml = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append(PLIST_DTD)
                .append("<plist version=\"1.0\">");
        
    	if(topObject instanceof Map)
    	{
    		@SuppressWarnings("unchecked")
			Map<Byte, Byte> dict = (Map<Byte, Byte>) topObject;
    		xml.append("<dict>");
    		
    		for(Map.Entry<Byte, Byte> entry : dict.entrySet())
    		{
    		    xml.append("<key>").append((String) results.getObjects().get(entry.getKey())).append("</key>");

    		    xml.append("<integer>").append(results.getObjects().get(entry.getValue()).toString()).append("</integer>");
    		}

    		xml.append("</dict>");
    	}

        xml.append("</plist>");

    	return xml.toString();
	}
	
	/**
	 * A special case parser for situations where you can expect a CMTime value in the bplist.
	 * 
	 * @param bplist
	 * @return Returns a <tt>CMTime</tt> object with its values parsed from the bplist.
	 * @throws IOException Thrown if any error is encountered parsing the array.
	 * @throws ClassCastException Thrown if the &quot;top object&quot; is not a dictionary.
	 */
	public static CMTime parseAsCMTime(byte[] bplist) throws IOException
	{
		final PropertyListResults results = parse(bplist);
		
		@SuppressWarnings("unchecked")
		Map<Byte, Byte> dict = (Map<Byte, Byte>) results.getObjects().get((int) results.getTrailer().topObject);
		
		HashMap<String, Object> values = new HashMap<String, Object>(dict.size());
		for(Map.Entry<Byte, Byte> entry : dict.entrySet())
		{
			String key = (String) results.getObjects().get(entry.getKey());
			Object value = results.getObjects().get(entry.getValue());
			
			values.put(key, value);
		}
		
		final CMTime t = new CMTime();
		t.setEpoch((Byte) values.get("epoch"));
		t.setFlags((Byte) values.get("flags"));
		t.setTimeScale((Long) values.get("timescale"));
		t.setValue((Long) values.get("value"));

		return t;
	}
	
	/**
	 * Ensure that a BPLIST is valid.
	 * 
	 * @param bplist
	 * @return
	 */
	public static boolean isValid(byte[] bplist)
	{
        if(bplist.length < BPLIST_HEADER.length || !Arrays.equals(BPLIST_HEADER, 0, BPLIST_HEADER.length - 1, bplist, 0, BPLIST_HEADER.length - 1))
        {
            return false;
        }
        
        return true;
	}
    
    private static PropertyListResults parse(byte[] bplist) throws IOException
    {
    	if(!isValid(bplist))
    	{
    		throw new IllegalArgumentException("Input is not a bplist");
    	}

    	final ArrayList<Object> objects = new ArrayList<Object>();
    	final Trailer trailer = readTrailer(bplist);
    	
		// List out the pointers
		SequentialByteArrayReader reader = new SequentialByteArrayReader(bplist, (int) (trailer.offsetTableOffset + trailer.topObject));
		int[] offsets = new int[(int) trailer.numObjects];
		for(long i = 0; i < trailer.numObjects; i++)
		{
			if(trailer.offsetIntSize == 1)
			{
				offsets[(int) i] = reader.getByte();
			}
			else if(trailer.offsetIntSize == 2)
			{
				offsets[(int) i] = reader.getUInt16();
			}
		}

		for(int i = 0; i < offsets.length; i++)
		{
			reader = new SequentialByteArrayReader(bplist, offsets[i]);
			byte marker = reader.getByte();
			int objectFormat = marker>>4 & 0x0F;
			switch(objectFormat)
			{
			case 0x0D:	// dict
			    handleDict(i, marker, reader, objects);
			    break;
			case 0x05:	// string (ASCII)
				int charCount = marker & 0x0F;
				objects.add(i, reader.getString(charCount));
				break;
			case 0x04:	// data
			    handleData(i, marker, reader, objects);
				break;
			case 0x01:	// int
			    handleInt(i, marker, reader, objects);
				break;
			default:
			    throw new IOException("Un-handled objectFormat encountered");
			}
		}

		return new PropertyListResults(objects, trailer);
    }
    
    private static void handleInt(final int objectIndex, final byte marker, final SequentialByteArrayReader reader, final ArrayList<Object> objects) throws IOException
    {
        int objectSize = (int) Math.pow(2, (marker & 0x0F));
        if(objectSize == 1)
        {
            objects.add(objectIndex, reader.getByte());
        }
        else if(objectSize == 2)
        {
            objects.add(objectIndex, reader.getUInt16());
        }
        else if(objectSize == 4)
        {
            objects.add(objectIndex, reader.getUInt32());
        }
        else if(objectSize == 8)
        {
            objects.add(objectIndex, reader.getInt64());
        }
    }
    
    private static void handleDict(final int objectIndex, final byte marker, final SequentialByteArrayReader reader, final ArrayList<Object> objects) throws IOException
    {
        // Using linked map preserves the key order
        LinkedHashMap<Byte, Byte> map = new LinkedHashMap<Byte, Byte>();
        int dictEntries = marker & 0x0F;
        byte[] keyRefs = new byte[dictEntries];
    
        for(int j = 0; j < dictEntries; j++)
        {
            keyRefs[j] = reader.getByte();
        }
        for(int j = 0; j < dictEntries; j++)
        {
            map.put(keyRefs[j], reader.getByte());
        }
    
        objects.add(objectIndex, map);
    }
    
    private static void handleData(final int objectIndex, final byte marker, final SequentialByteArrayReader reader, final ArrayList<Object> objects) throws IOException
    {
        int byteCount = marker & 0x0F;
        if(byteCount == 0x0F)
        {
            byte sizeMarker = reader.getByte();
            if((sizeMarker >> 4 & 0x0F) != 1)
            {
                throw new IllegalArgumentException("Invalid size marker");
            }
    
            int objectSizeWidth = (int) Math.pow(2, sizeMarker & 0x0F);
            if(objectSizeWidth == 1)
            {
                byteCount = reader.getInt8();
            }
            else if(objectSizeWidth == 2)
            {
                byteCount = reader.getUInt16();
            }
        }

        objects.add(objectIndex, reader.getBytes(byteCount));

    }
    
	
	public static class PropertyListResults
	{
		private List<Object> objects;
    	private Trailer trailer;

    	public PropertyListResults(List<Object> objects, Trailer trailer)
    	{
    		this.objects = objects;
    		this.trailer = trailer;
    	}
    	
    	public List<Object> getObjects()
    	{
    		return objects;
    	}
    	
    	public Trailer getTrailer()
    	{
    		return trailer;
    	}
	}
	

	/**
	 * Given a full byte array containing the BPLIST, read the trailer object from the end 
	 * of the array. 5 unused bytes and 1 sort version are skipped.
	 * 
	 * @param bplist The BPLIST binary array.
	 * @return Returns the <tt>Trailer</tt> object with values parsed from the array.
	 * @throws IOException
	 */
    private static Trailer readTrailer(byte[] bplist) throws IOException
    {
    	SequentialByteArrayReader reader = new SequentialByteArrayReader(bplist, bplist.length - Trailer.STRUCT_SIZE);
    	reader.skip(5L);	// Skip the 5-byte _unused values
    	reader.skip(1L);	// Skip 1-byte sort version

    	final Trailer trailer = new Trailer();
    	trailer.offsetIntSize = reader.getByte();
    	trailer.objectRefSize = reader.getByte();
    	trailer.numObjects = reader.getInt64();
    	trailer.topObject = reader.getInt64();
    	trailer.offsetTableOffset = reader.getInt64();
    	
    	return trailer;
    }

	/**
	 * A data structure to hold the BPLIST trailer data. Only meaningful fields
	 * are represented - the reader is responsible for skipping unused arrays.
	 */
    private static class Trailer
    {
    	public static final int STRUCT_SIZE = 32;
    	
    	byte offsetIntSize;
    	byte objectRefSize;
    	long numObjects;
    	long topObject;
    	long offsetTableOffset;
    }

    /**
     * CMTime class, derived from referenced Apple documentation.
     * 
     * @see https://developer.apple.com/documentation/coremedia/cmtime-u58
     */
    public static class CMTime
    {
    	private int epoch;
    	private byte flags;
    	private long timeScale;
    	private long value;
    	
    	public boolean isValid()
    	{
    		return ((flags & 0x01) == 1);
    	}
    	
    	public int getEpoch()
    	{
    		return epoch;
    	}
    	
    	public void setEpoch(int epoch)
    	{
    		this.epoch = epoch;
    	}
    	
    	public byte getFlags()
    	{
    		return flags;
    	}
    	
    	public void setFlags(byte flags)
    	{
    		this.flags = flags;
    	}
    	
    	public long getTimeScale()
    	{
    		return timeScale;
    	}
    	
    	public void setTimeScale(long timeScale)
    	{
    		this.timeScale = timeScale;
    	}
    	
    	public long getValue()
    	{
    		return value;
    	}
    	
    	public void setValue(long value)
    	{
    		this.value = value;
    	}
    }
}
