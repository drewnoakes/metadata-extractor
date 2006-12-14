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
package com.drew.imaging.jpeg;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Holds a collection of Jpeg data segments.  This need not necessarily be all segments
 * within the Jpeg.  For example, it may be convenient to store only the non-image
 * segments when analysing (or serializing) metadata.
 *
 * Segments are keyed via their segment marker (a byte).  Where multiple segments use the
 * same segment marker, they will all be stored and available.
 */
public class JpegSegmentData implements Serializable
{
    static final long serialVersionUID = 7110175216435025451L;
    
    /** A map of byte[], keyed by the segment marker */
    private final HashMap _segmentDataMap;

    /**
     * Creates a new JpegSegmentData collection object.
     */
    public JpegSegmentData()
    {
        _segmentDataMap = new HashMap(10);
    }

    /**
     * Adds segment bytes to the collection.
     * @param segmentMarker
     * @param segmentBytes
     */
    public void addSegment(byte segmentMarker, byte[] segmentBytes)
    {
        final List segmentList = getOrCreateSegmentList(segmentMarker);
        segmentList.add(segmentBytes);
    }

    /**
     * Gets the first Jpeg segment data for the specified marker.
     * @param segmentMarker the byte identifier for the desired segment
     * @return a byte[] containing segment data or null if no data exists for that segment
     */
    public byte[] getSegment(byte segmentMarker)
    {
        return getSegment(segmentMarker, 0);
    }

    /**
     * Gets segment data for a specific occurrence and marker.  Use this method when more than one occurrence
     * of segment data for a given marker exists.
     * @param segmentMarker identifies the required segment
     * @param occurrence the zero-based index of the occurrence
     * @return the segment data as a byte[], or null if no segment exists for the marker & occurrence
     */
    public byte[] getSegment(byte segmentMarker, int occurrence)
    {
        final List segmentList = getSegmentList(segmentMarker);

        if (segmentList==null || segmentList.size()<=occurrence)
            return null;
        else
            return (byte[]) segmentList.get(occurrence);
    }

    /**
     * Returns the count of segment data byte arrays stored for a given segment marker.
     * @param segmentMarker identifies the required segment
     * @return the segment count (zero if no segments exist).
     */
    public int getSegmentCount(byte segmentMarker)
    {
        final List segmentList = getSegmentList(segmentMarker);
        if (segmentList==null)
            return 0;
        else
            return segmentList.size();
    }

    /**
     * Removes a specified instance of a segment's data from the collection.  Use this method when more than one
     * occurrence of segment data for a given marker exists.
     * @param segmentMarker identifies the required segment
     * @param occurrence the zero-based index of the segment occurrence to remove.
     */
    public void removeSegmentOccurrence(byte segmentMarker, int occurrence)
    {
        final List segmentList = (List)_segmentDataMap.get(new Byte(segmentMarker));
        segmentList.remove(occurrence);
    }

    /**
     * Removes all segments from the collection having the specified marker.
     * @param segmentMarker identifies the required segment
     */
    public void removeSegment(byte segmentMarker)
    {
        _segmentDataMap.remove(new Byte(segmentMarker));
    }

    /**
     * Determines whether data is present for a given segment marker.
     * @param segmentMarker identifies the required segment
     * @return true if data exists, otherwise false
     */
    public boolean containsSegment(byte segmentMarker)
    {
        return _segmentDataMap.containsKey(new Byte(segmentMarker));
    }

    /**
     * Serialises the contents of a JpegSegmentData to a file.
     * @param file to file to write from
     * @param segmentData the data to write
     * @throws IOException if problems occur while writing
     */
    public static void ToFile(File file, JpegSegmentData segmentData) throws IOException
    {
        ObjectOutputStream outputStream = null;
        try
        {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(segmentData);
        }
        finally
        {
            if (outputStream!=null)
                outputStream.close();
        }
    }

    /**
     * Deserialises the contents of a JpegSegmentData from a file.
     * @param file the file to read from
     * @return the JpegSegmentData as read
     * @throws IOException if problems occur while reading
     * @throws ClassNotFoundException if problems occur while deserialising
     */
    public static JpegSegmentData FromFile(File file) throws IOException, ClassNotFoundException
    {
        ObjectInputStream inputStream = null;
        try
        {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            return (JpegSegmentData)inputStream.readObject();
        }
        finally
        {
            if (inputStream!=null)
                inputStream.close();
        }
    }

    private List getSegmentList(byte segmentMarker)
    {
        return (List)_segmentDataMap.get(new Byte(segmentMarker));
    }

    private List getOrCreateSegmentList(byte segmentMarker)
    {
        List segmentList;
        Byte key = new Byte(segmentMarker);
        if (_segmentDataMap.containsKey(key)) {
            segmentList = (List)_segmentDataMap.get(key);
        } else {
            segmentList = new ArrayList();
            _segmentDataMap.put(key, segmentList);
        }
        return segmentList;
    }
}
