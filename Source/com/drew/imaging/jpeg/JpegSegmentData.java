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
package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.util.*;

/**
 * Holds a collection of JPEG data segments.  This need not necessarily be all segments
 * within the JPEG. For example, it may be convenient to store only the non-image
 * segments when analysing metadata.
 * <p>
 * Segments are keyed via their {@link JpegSegmentType}. Where multiple segments use the
 * same segment type, they will all be stored and available.
 * <p>
 * Each segment type may contain multiple entries. Conceptually the model is:
 * <code>Map&lt;JpegSegmentType, Collection&lt;byte[]&gt;&gt;</code>. This class provides
 * convenience methods around that structure.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegSegmentData
{
    @NotNull
    private final HashMap<JpegSegmentType, List<JpegSegment>> _segmentDataMap = new HashMap<JpegSegmentType, List<JpegSegment>>(10);

    /**
     * Adds segment bytes to the collection.
     *
     * @param segment  the specific segment being added
     */
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    public void addSegment(@NotNull JpegSegment segment)
    {
        getOrCreateSegmentList(segment.getType()).add(segment);
    }

    /**
     * Gets the set of JPEG segment type identifiers.
     */
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        Set<JpegSegmentType> segmentTypes = new HashSet<JpegSegmentType>();

        for (JpegSegmentType segmentType : _segmentDataMap.keySet())
        {
            segmentTypes.add(segmentType);
        }

        return segmentTypes;
    }

    /**
     * Gets the first JPEG segment data for the specified type.
     *
     * @param segmentType the JpegSegmentType for the desired segment
     * @return a byte[] containing segment data or null if no data exists for that segment
     */
    @Nullable
    public JpegSegment getSegment(@NotNull JpegSegmentType segmentType)
    {
        return getSegment(segmentType, 0);
    }

    /**
     * Gets segment data for a specific occurrence and type.  Use this method when more than one occurrence
     * of segment data for a given type exists.
     *
     * @param segmentType identifies the required segment
     * @param occurrence  the zero-based index of the occurrence
     * @return the segment data as a byte[], or null if no segment exists for the type &amp; occurrence
     */
    @Nullable
    public JpegSegment getSegment(@NotNull JpegSegmentType segmentType, int occurrence)
    {
        final List<JpegSegment> segmentList = getSegmentList(segmentType);

        return segmentList != null && segmentList.size() > occurrence
                ? segmentList.get(occurrence)
                : null;
    }

    /**
     * Returns all instances of a given JPEG segment.  If no instances exist, an empty sequence is returned.
     *
     * @param segmentType a number which identifies the type of JPEG segment being queried
     * @return zero or more byte arrays, each holding the data of a JPEG segment
     */
    @NotNull
    public Iterable<JpegSegment> getSegments(JpegSegmentType segmentType)
    {
        final List<JpegSegment> segmentList = getSegmentList(segmentType);
        return segmentList == null ? new ArrayList<JpegSegment>() : segmentList;
    }

    @Nullable
    private List<JpegSegment> getSegmentList(JpegSegmentType segmentType)
    {
        return _segmentDataMap.get(segmentType);
    }

    @NotNull
    private List<JpegSegment> getOrCreateSegmentList(JpegSegmentType segmentType)
    {
        List<JpegSegment> segmentList;
        if (_segmentDataMap.containsKey(segmentType)) {
            segmentList = _segmentDataMap.get(segmentType);
        } else {
            segmentList = new ArrayList<JpegSegment>();
            _segmentDataMap.put(segmentType, segmentList);
        }
        return segmentList;
    }

    /**
     * Returns the count of segment data byte arrays stored for a given segment type.
     *
     * @param segmentType identifies the required segment
     * @return the segment count (zero if no segments exist).
     */
    public int getSegmentCount(@NotNull JpegSegmentType segmentType)
    {
        final List<JpegSegment> segmentList = getSegmentList(segmentType);
        return segmentList == null ? 0 : segmentList.size();
    }

    /**
     * Removes a specified instance of a segment's data from the collection.  Use this method when more than one
     * occurrence of segment data exists for a given type exists.
     *
     * @param segmentType identifies the required segment
     * @param occurrence  the zero-based index of the segment occurrence to remove.
     */
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    public void removeSegmentOccurrence(@NotNull JpegSegmentType segmentType, int occurrence)
    {
        final List<JpegSegment> segmentList = _segmentDataMap.get(segmentType);
        segmentList.remove(occurrence);
    }

    /**
     * Removes all segments from the collection having the specified type.
     *
     * @param segmentType identifies the required segment
     */
    public void removeSegment(@NotNull JpegSegmentType segmentType)
    {
        _segmentDataMap.remove(segmentType);
    }

    /**
     * Determines whether data is present for a given segment type.
     *
     * @param segmentType identifies the required segment
     * @return true if data exists, otherwise false
     */
    public boolean containsSegment(@NotNull JpegSegmentType segmentType)
    {
        return _segmentDataMap.containsKey(segmentType);
    }
}
