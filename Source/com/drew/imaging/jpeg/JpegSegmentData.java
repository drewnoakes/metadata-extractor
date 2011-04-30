/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Holds a collection of Jpeg data segments.  This need not necessarily be all segments
 * within the Jpeg.  For example, it may be convenient to store only the non-image
 * segments when analysing (or serializing) metadata.
 * <p/>
 * Segments are keyed via their segment marker (a byte).  Where multiple segments use the
 * same segment marker, they will all be stored and available.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentData implements Serializable
{
    static final long serialVersionUID = 7110175216435025451L;
    
    /** A map of byte[], keyed by the segment marker */
    @NotNull
    private final HashMap<Byte, List<byte[]>> _segmentDataMap = new HashMap<Byte, List<byte[]>>(10);

    /**
     * Adds segment bytes to the collection.
     * @param segmentMarker
     * @param segmentBytes
     */
    public void addSegment(byte segmentMarker, @NotNull byte[] segmentBytes)
    {
        final List<byte[]> segmentList = getOrCreateSegmentList(segmentMarker);
        segmentList.add(segmentBytes);
    }

    /**
     * Gets the first Jpeg segment data for the specified marker.
     * @param segmentMarker the byte identifier for the desired segment
     * @return a byte[] containing segment data or null if no data exists for that segment
     */
    @Nullable
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
    @Nullable
    public byte[] getSegment(byte segmentMarker, int occurrence)
    {
        final List<byte[]> segmentList = getSegmentList(segmentMarker);

        if (segmentList==null || segmentList.size()<=occurrence)
            return null;
        else
            return segmentList.get(occurrence);
    }

    /**
     * Returns all instances of a given Jpeg segment.  If no instances exist, an empty sequence is returned.
     *
     * @param segmentMarker
     * @return
     */
    @NotNull
    public Iterable<byte[]> getSegments(byte segmentMarker)
    {
        final List<byte[]> segmentList = getSegmentList(segmentMarker);
        return segmentList==null ? new ArrayList<byte[]>() : segmentList;
    }

    @Nullable
    public List<byte[]> getSegmentList(byte segmentMarker)
    {
        return _segmentDataMap.get(new Byte(segmentMarker));
    }

    /**
     * Returns the count of segment data byte arrays stored for a given segment marker.
     * @param segmentMarker identifies the required segment
     * @return the segment count (zero if no segments exist).
     */
    public int getSegmentCount(byte segmentMarker)
    {
        final List<byte[]> segmentList = getSegmentList(segmentMarker);
        return segmentList == null ? 0 : segmentList.size();
    }

    /**
     * Removes a specified instance of a segment's data from the collection.  Use this method when more than one
     * occurrence of segment data for a given marker exists.
     * @param segmentMarker identifies the required segment
     * @param occurrence the zero-based index of the segment occurrence to remove.
     */
    public void removeSegmentOccurrence(byte segmentMarker, int occurrence)
    {
        final List<byte[]> segmentList = _segmentDataMap.get(new Byte(segmentMarker));
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
    public static void toFile(@NotNull File file, @NotNull JpegSegmentData segmentData) throws IOException
    {
        ObjectOutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(segmentData);
        }
        finally
        {
            if (fileOutputStream!=null)
                fileOutputStream.close();
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
    @NotNull
    public static JpegSegmentData fromFile(@NotNull File file) throws IOException, ClassNotFoundException
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

    @NotNull
    private List<byte[]> getOrCreateSegmentList(byte segmentMarker)
    {
        List<byte[]> segmentList;
        if (_segmentDataMap.containsKey(segmentMarker)) {
            segmentList = _segmentDataMap.get(segmentMarker);
        } else {
            segmentList = new ArrayList<byte[]>();
            _segmentDataMap.put(segmentMarker, segmentList);
        }
        return segmentList;
    }
}
