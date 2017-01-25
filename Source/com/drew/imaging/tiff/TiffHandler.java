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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.StringValue;

import java.io.IOException;
import java.util.Set;

/**
 * Interface of an class capable of handling events raised during the reading of a TIFF file
 * via {@link TiffReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface TiffHandler
{
    /**
     * Receives the 2-byte marker found in the TIFF header.
     * <p>
     * Implementations are not obligated to use this information for any purpose, though it may be useful for
     * validation or perhaps differentiating the type of mapping to use for observed tags and IFDs.
     *
     * @param marker the 2-byte value found at position 2 of the TIFF header
     */
    void setTiffMarker(int marker) throws TiffProcessingException;

    boolean tryEnterSubIfd(int tagId);
    boolean hasFollowerIfd();

    void endingIFD();

    @Nullable
    Long tryCustomProcessFormat(int tagId, int formatCode, long componentCount);

    boolean customProcessTag(int tagOffset,
                             @NotNull Set<Integer> processedIfdOffsets,
                             int tiffHeaderOffset,
                             @NotNull RandomAccessReader reader,
                             int tagId,
                             int byteCount) throws IOException;

    void warn(@NotNull String message);
    void error(@NotNull String message);

    void setByteArray(int tagId, @NotNull byte[] bytes);
    void setString(int tagId, @NotNull StringValue string);
    void setRational(int tagId, @NotNull Rational rational);
    void setRationalArray(int tagId, @NotNull Rational[] array);
    void setFloat(int tagId, float float32);
    void setFloatArray(int tagId, @NotNull float[] array);
    void setDouble(int tagId, double double64);
    void setDoubleArray(int tagId, @NotNull double[] array);
    void setInt8s(int tagId, byte int8s);
    void setInt8sArray(int tagId, @NotNull byte[] array);
    void setInt8u(int tagId, short int8u);
    void setInt8uArray(int tagId, @NotNull short[] array);
    void setInt16s(int tagId, int int16s);
    void setInt16sArray(int tagId, @NotNull short[] array);
    void setInt16u(int tagId, int int16u);
    void setInt16uArray(int tagId, @NotNull int[] array);
    void setInt32s(int tagId, int int32s);
    void setInt32sArray(int tagId, @NotNull int[] array);
    void setInt32u(int tagId, long int32u);
    void setInt32uArray(int tagId, @NotNull long[] array);
}
