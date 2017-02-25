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
package com.drew.imaging.tiff;

import com.drew.lang.RandomAccessReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Processes TIFF-formatted data, calling into client code via that {@link TiffHandler} interface.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class TiffReader
{
    /**
     * Processes a TIFF data sequence.
     *
     * @param reader the {@link RandomAccessReader} from which the data should be read
     * @param handler the {@link TiffHandler} that will coordinate processing and accept read values
     * @param tiffHeaderOffset the offset within <code>reader</code> at which the TIFF header starts
     * @throws TiffProcessingException if an error occurred during the processing of TIFF data that could not be
     *                                 ignored or recovered from
     * @throws IOException an error occurred while accessing the required data
     */
    public void processTiff(@NotNull final RandomAccessReader reader,
                            @NotNull final TiffHandler handler,
                            final int tiffHeaderOffset) throws TiffProcessingException, IOException
    {
        // This must be either "MM" or "II".
        short byteOrderIdentifier = reader.getInt16(tiffHeaderOffset);

        if (byteOrderIdentifier == 0x4d4d) { // "MM"
            reader.setMotorolaByteOrder(true);
        } else if (byteOrderIdentifier == 0x4949) { // "II"
            reader.setMotorolaByteOrder(false);
        } else {
            throw new TiffProcessingException("Unclear distinction between Motorola/Intel byte ordering: " + byteOrderIdentifier);
        }

        // Check the next two values for correctness.
        final int tiffMarker = reader.getUInt16(2 + tiffHeaderOffset);
        handler.setTiffMarker(tiffMarker);

        int firstIfdOffset = reader.getInt32(4 + tiffHeaderOffset) + tiffHeaderOffset;

        // David Ekholm sent a digital camera image that has this problem
        // TODO getLength should be avoided as it causes RandomAccessStreamReader to read to the end of the stream
        if (firstIfdOffset >= reader.getLength() - 1) {
            handler.warn("First IFD offset is beyond the end of the TIFF data segment -- trying default offset");
            // First directory normally starts immediately after the offset bytes, so try that
            firstIfdOffset = tiffHeaderOffset + 2 + 2 + 4;
        }

        Set<Integer> processedIfdOffsets = new HashSet<Integer>();
        processIfd(handler, reader, processedIfdOffsets, firstIfdOffset, tiffHeaderOffset);
    }

    /**
     * Processes a TIFF IFD.
     *
     * IFD Header:
     * <ul>
     *     <li><b>2 bytes</b> number of tags</li>
     * </ul>
     * Tag structure:
     * <ul>
     *     <li><b>2 bytes</b> tag type</li>
     *     <li><b>2 bytes</b> format code (values 1 to 12, inclusive)</li>
     *     <li><b>4 bytes</b> component count</li>
     *     <li><b>4 bytes</b> inline value, or offset pointer if too large to fit in four bytes</li>
     * </ul>
     *
     *
     * @param handler the {@link com.drew.imaging.tiff.TiffHandler} that will coordinate processing and accept read values
     * @param reader the {@link com.drew.lang.RandomAccessReader} from which the data should be read
     * @param processedIfdOffsets the set of visited IFD offsets, to avoid revisiting the same IFD in an endless loop
     * @param ifdOffset the offset within <code>reader</code> at which the IFD data starts
     * @param tiffHeaderOffset the offset within <code>reader</code> at which the TIFF header starts
     * @throws IOException an error occurred while accessing the required data
     */
    public static void processIfd(@NotNull final TiffHandler handler,
                                  @NotNull final RandomAccessReader reader,
                                  @NotNull final Set<Integer> processedIfdOffsets,
                                  final int ifdOffset,
                                  final int tiffHeaderOffset) throws IOException
    {
        Boolean resetByteOrder = null;
        try {
            // check for directories we've already visited to avoid stack overflows when recursive/cyclic directory structures exist
            if (processedIfdOffsets.contains(Integer.valueOf(ifdOffset))) {
                return;
            }

            // remember that we've visited this directory so that we don't visit it again later
            processedIfdOffsets.add(ifdOffset);

            if (ifdOffset >= reader.getLength() || ifdOffset < 0) {
                handler.error("Ignored IFD marked to start outside data segment");
                return;
            }

            // First two bytes in the IFD are the number of tags in this directory
            int dirTagCount = reader.getUInt16(ifdOffset);

            // Some software modifies the byte order of the file, but misses some IFDs (such as makernotes).
            // The entire test image repository doesn't contain a single IFD with more than 255 entries.
            // Here we detect switched bytes that suggest this problem, and temporarily swap the byte order.
            // This was discussed in GitHub issue #136.
            if (dirTagCount > 0xFF && (dirTagCount & 0xFF) == 0) {
                resetByteOrder = reader.isMotorolaByteOrder();
                dirTagCount >>= 8;
                reader.setMotorolaByteOrder(!reader.isMotorolaByteOrder());
            }

            int dirLength = (2 + (12 * dirTagCount) + 4);
            if (dirLength + ifdOffset > reader.getLength()) {
                handler.error("Illegally sized IFD");
                return;
            }

            //
            // Handle each tag in this directory
            //
            int invalidTiffFormatCodeCount = 0;
            for (int tagNumber = 0; tagNumber < dirTagCount; tagNumber++) {
                final int tagOffset = calculateTagOffset(ifdOffset, tagNumber);

                // 2 bytes for the tag id
                final int tagId = reader.getUInt16(tagOffset);

                // 2 bytes for the format code
                final int formatCode = reader.getUInt16(tagOffset + 2);
                final TiffDataFormat format = TiffDataFormat.fromTiffFormatCode(formatCode);

                // 4 bytes dictate the number of components in this tag's data
                final long componentCount = reader.getUInt32(tagOffset + 4);

                final long byteCount;
                if (format == null) {
                    Long byteCountOverride = handler.tryCustomProcessFormat(tagId, formatCode, componentCount);
                    if (byteCountOverride == null) {
                        // This error suggests that we are processing at an incorrect index and will generate
                        // rubbish until we go out of bounds (which may be a while).  Exit now.
                        handler.error(String.format("Invalid TIFF tag format code %d for tag 0x%04X", formatCode, tagId));
                        // TODO specify threshold as a parameter, or provide some other external control over this behaviour
                        if (++invalidTiffFormatCodeCount > 5) {
                            handler.error("Stopping processing as too many errors seen in TIFF IFD");
                            return;
                        }
                        continue;
                    }
                    byteCount = byteCountOverride;
                } else {
                    byteCount = componentCount * format.getComponentSizeBytes();
                }

                final long tagValueOffset;
                if (byteCount > 4) {
                    // If it's bigger than 4 bytes, the dir entry contains an offset.
                    final long offsetVal = reader.getUInt32(tagOffset + 8);
                    if (offsetVal + byteCount > reader.getLength()) {
                        // Bogus pointer offset and / or byteCount value
                        handler.error("Illegal TIFF tag pointer offset");
                        continue;
                    }
                    tagValueOffset = tiffHeaderOffset + offsetVal;
                } else {
                    // 4 bytes or less and value is in the dir entry itself.
                    tagValueOffset = tagOffset + 8;
                }

                if (tagValueOffset < 0 || tagValueOffset > reader.getLength()) {
                    handler.error("Illegal TIFF tag pointer offset");
                    continue;
                }

                // Check that this tag isn't going to allocate outside the bounds of the data array.
                // This addresses an uncommon OutOfMemoryError.
                if (byteCount < 0 || tagValueOffset + byteCount > reader.getLength()) {
                    handler.error("Illegal number of bytes for TIFF tag data: " + byteCount);
                    continue;
                }

                // Some tags point to one or more additional IFDs to process
                boolean isIfdPointer = false;
                if (byteCount == 4 * componentCount) {
                    for (int i = 0; i < componentCount; i++) {
                        if (handler.tryEnterSubIfd(tagId)) {
                            isIfdPointer = true;
                            int subDirOffset = tiffHeaderOffset + reader.getInt32((int) (tagValueOffset + i * 4));
                            processIfd(handler, reader, processedIfdOffsets, subDirOffset, tiffHeaderOffset);
                        }
                    }
                }

                // If it wasn't an IFD pointer, allow custom tag processing to occur
                if (!isIfdPointer && !handler.customProcessTag((int) tagValueOffset, processedIfdOffsets, tiffHeaderOffset, reader, tagId, (int) byteCount)) {
                    // If no custom processing occurred, process the tag in the standard fashion
                    processTag(handler, tagId, (int) tagValueOffset, (int) componentCount, formatCode, reader);
                }
            }

            // at the end of each IFD is an optional link to the next IFD
            final int finalTagOffset = calculateTagOffset(ifdOffset, dirTagCount);
            int nextIfdOffset = reader.getInt32(finalTagOffset);
            if (nextIfdOffset != 0) {
                nextIfdOffset += tiffHeaderOffset;
                if (nextIfdOffset >= reader.getLength()) {
                    // Last 4 bytes of IFD reference another IFD with an address that is out of bounds
                    // Note this could have been caused by jhead 1.3 cropping too much
                    return;
                } else if (nextIfdOffset < ifdOffset) {
                    // TODO is this a valid restriction?
                    // Last 4 bytes of IFD reference another IFD with an address that is before the start of this directory
                    return;
                }

                if (handler.hasFollowerIfd()) {
                    processIfd(handler, reader, processedIfdOffsets, nextIfdOffset, tiffHeaderOffset);
                }
            }
        } finally {
            handler.endingIFD();
            if (resetByteOrder != null)
                reader.setMotorolaByteOrder(resetByteOrder);
        }
    }

    private static void processTag(@NotNull final TiffHandler handler,
                                   final int tagId,
                                   final int tagValueOffset,
                                   final int componentCount,
                                   final int formatCode,
                                   @NotNull final RandomAccessReader reader) throws IOException
    {
        switch (formatCode) {
            case TiffDataFormat.CODE_UNDEFINED:
                // this includes exif user comments
                handler.setByteArray(tagId, reader.getBytes(tagValueOffset, componentCount));
                break;
            case TiffDataFormat.CODE_STRING:
                handler.setString(tagId, reader.getNullTerminatedStringValue(tagValueOffset, componentCount, null));
                break;
            case TiffDataFormat.CODE_RATIONAL_S:
                if (componentCount == 1) {
                    handler.setRational(tagId, new Rational(reader.getInt32(tagValueOffset), reader.getInt32(tagValueOffset + 4)));
                } else if (componentCount > 1) {
                    Rational[] array = new Rational[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = new Rational(reader.getInt32(tagValueOffset + (8 * i)), reader.getInt32(tagValueOffset + 4 + (8 * i)));
                    handler.setRationalArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_RATIONAL_U:
                if (componentCount == 1) {
                    handler.setRational(tagId, new Rational(reader.getUInt32(tagValueOffset), reader.getUInt32(tagValueOffset + 4)));
                } else if (componentCount > 1) {
                    Rational[] array = new Rational[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = new Rational(reader.getUInt32(tagValueOffset + (8 * i)), reader.getUInt32(tagValueOffset + 4 + (8 * i)));
                    handler.setRationalArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_SINGLE:
                if (componentCount == 1) {
                    handler.setFloat(tagId, reader.getFloat32(tagValueOffset));
                } else {
                    float[] array = new float[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getFloat32(tagValueOffset + (i * 4));
                    handler.setFloatArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_DOUBLE:
                if (componentCount == 1) {
                    handler.setDouble(tagId, reader.getDouble64(tagValueOffset));
                } else {
                    double[] array = new double[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getDouble64(tagValueOffset + (i * 4));
                    handler.setDoubleArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT8_S:
                if (componentCount == 1) {
                    handler.setInt8s(tagId, reader.getInt8(tagValueOffset));
                } else {
                    byte[] array = new byte[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getInt8(tagValueOffset + i);
                    handler.setInt8sArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT8_U:
                if (componentCount == 1) {
                    handler.setInt8u(tagId, reader.getUInt8(tagValueOffset));
                } else {
                    short[] array = new short[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getUInt8(tagValueOffset + i);
                    handler.setInt8uArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT16_S:
                if (componentCount == 1) {
                    handler.setInt16s(tagId, (int)reader.getInt16(tagValueOffset));
                } else {
                    short[] array = new short[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getInt16(tagValueOffset + (i * 2));
                    handler.setInt16sArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT16_U:
                if (componentCount == 1) {
                    handler.setInt16u(tagId, reader.getUInt16(tagValueOffset));
                } else {
                    int[] array = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getUInt16(tagValueOffset + (i * 2));
                    handler.setInt16uArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT32_S:
                // NOTE 'long' in this case means 32 bit, not 64
                if (componentCount == 1) {
                    handler.setInt32s(tagId, reader.getInt32(tagValueOffset));
                } else {
                    int[] array = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getInt32(tagValueOffset + (i * 4));
                    handler.setInt32sArray(tagId, array);
                }
                break;
            case TiffDataFormat.CODE_INT32_U:
                // NOTE 'long' in this case means 32 bit, not 64
                if (componentCount == 1) {
                    handler.setInt32u(tagId, reader.getUInt32(tagValueOffset));
                } else {
                    long[] array = new long[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        array[i] = reader.getUInt32(tagValueOffset + (i * 4));
                    handler.setInt32uArray(tagId, array);
                }
                break;
            default:
                handler.error(String.format("Invalid TIFF tag format code %d for tag 0x%04X", formatCode, tagId));
        }
    }

    /**
     * Determine the offset of a given tag within the specified IFD.
     *
     * @param ifdStartOffset the offset at which the IFD starts
     * @param entryNumber    the zero-based entry number
     */
    private static int calculateTagOffset(int ifdStartOffset, int entryNumber)
    {
        // Add 2 bytes for the tag count.
        // Each entry is 12 bytes.
        return ifdStartOffset + 2 + (12 * entryNumber);
    }
}
