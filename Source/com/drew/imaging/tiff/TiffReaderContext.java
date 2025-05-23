/*
 * Copyright 2002-2022 Drew Noakes and contributors
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
package com.drew.imaging.tiff;

import com.drew.lang.RandomAccessReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TiffReaderContext {

    private final RandomAccessReader _reader;
    private final boolean _isMotorolaByteOrder;
    private final boolean _isBigTiff;
    private final Set<Integer> _visitedIfds;

    /**
     * Gets an object via which TIFF data may be read by index.
     *
     * @return an object via which TIFF data may be read by index.
     */
    public RandomAccessReader getReader()
    {
        return _reader;
    }

    /**
     * Gets whether the TIFF data advertises itself as having Motorola byte order.
     * Note that this value may differ from the byte order of Reader
     * if during reading it is determined that the byte order should change for some reason.
     *
     * @return whether the TIFF data advertises itself as having Motorola byte order.
     */
    public boolean isMotorolaByteOrder()
    {
        return _isMotorolaByteOrder;
    }

    /**
     * Gets whether the TIFF data stream is encoded using the BigTIFF standard.
     *
     * @return whether the TIFF data stream is encoded using the BigTIFF standard.
     */
    public boolean isBigTiff()
    {
        return _isBigTiff;
    }

    public TiffReaderContext(RandomAccessReader reader, boolean isMotorolaByteOrder, boolean isBigTiff)
    {
        _reader = reader;
        _isMotorolaByteOrder = isMotorolaByteOrder;
        _isBigTiff = isBigTiff;
        _visitedIfds = new HashSet<Integer>();
    }

    /**
     * Gets whether the specified IFD should be processed or not, based on whether it has
     * been processed before.
     * @param ifdOffset The offset at which the IFD starts.
     * @return {@code true} if the IFD should be processed, otherwise {@code false}.
     */
    public boolean tryVisitIfd(int ifdOffset)
    {
        // Note that we track these offsets in the global frame, not the reader's local frame.
        int globalIfdOffset = _reader.toUnshiftedOffset(ifdOffset);

        return _visitedIfds.add(globalIfdOffset);
    }

    /**
     * Returns a copy of this context object with a reader observing the specified byte order.
     * Note that this method does not change the value of {@code isMotorolaByteOrder} which
     * represents the advertised byte order at the start of the TIFF data stream.
     * @throws IOException if an I/O error occurs.
     */
    public TiffReaderContext withByteOrder(boolean isMotorolaByteOrder) throws IOException
    {
        return new TiffReaderContext(_reader.withByteOrder(isMotorolaByteOrder), isMotorolaByteOrder, _isBigTiff);
    }

    /**
     * Returns a copy of this context object with a reader having a shifted base offset.
     * @throws IOException if an I/O error occurs.
     */
    public TiffReaderContext withShiftedBaseOffset(int baseOffset) throws IOException
    {
        return new TiffReaderContext(_reader.withShiftedBaseOffset(baseOffset), _isMotorolaByteOrder, _isBigTiff);
    }
}
