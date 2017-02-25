/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.imaging.x3f;

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Anthony Mandra http://anthonymandra.com
 */
public class X3fReader
{
    /**
     * Processes a X3F data sequence.
     *
     * @param reader the {@link RandomAccessReader} from which the data should be read
     * @param x3fHeaderOffset the offset within <code>reader</code> at which the TIFF header starts
     * @throws TiffProcessingException if an error occurred during the processing of TIFF data that could not be
     *                                 ignored or recovered from
     * @throws IOException an error occurred while accessing the required data
     */
    public void processX3f(@NotNull final RandomAccessReader reader,
                           final int x3fHeaderOffset) throws IOException
    {
        reader.setMotorolaByteOrder(false); // little endian

        // For, posterity, we already identified this file "FOVb"
//        String identifier =             reader.getString(0, 4, Charsets.ASCII);

        final int versionMinor =        reader.getInt16(4 + x3fHeaderOffset);
        final int versionMajor =        reader.getInt16(6 + x3fHeaderOffset);
        final long uid =                reader.getInt64(8 + x3fHeaderOffset);
        final int mark =                reader.getInt32(24 + x3fHeaderOffset);
        final int width =               reader.getInt32(28 + x3fHeaderOffset);
        final int height =              reader.getInt32(32 + x3fHeaderOffset);
        final int rotation =            reader.getInt32(36 + x3fHeaderOffset);

        // For self-documentation
//        0 None Extended data slot is unused.
//        1 FLOAT32 Exposure adjust.
//        2 FLOAT32 Contrast adjust.
//        3 FLOAT32 Shadow adjust.
//        4 FLOAT32 Highlight adjust.
//        5 FLOAT32 Saturation adjust.
//        6 FLOAT32 Sharpness adjust.
//        7 FLOAT32 Color adjust red.
//        8 FLOAT32 Color adjust green.
//        9 FLOAT32 Color adjust blue.
//        10 FLOAT32 X3 Fill Light adjust
        if (versionMajor == 2 && versionMinor == 2 || versionMinor == 1)    //extended was only 2.1, 2.2?
        {
            final String whiteBalance = reader.getNullTerminatedString(40 + x3fHeaderOffset, 32, Charsets.ASCII);

            final byte[] extendedTypes = new byte[32];
            final float[] extendedDatums = new float[32];
            final int extendedTypeOffset = 72 + x3fHeaderOffset;
            final int extendedDatumOffset = 104 + x3fHeaderOffset;

            for (int i = 0; i < 32; i++)
            {
                extendedTypes[i] = reader.getByte(i + extendedTypeOffset);
                extendedDatums[i] = reader.getFloat32(i * 4 + extendedDatumOffset);
            }
        }

        // Max 2G file
        final int directoryPosition = reader.getInt32((int)reader.getLength() - 4);     // TODO: took a notable time (debug), unattached was unnoticeable,  may need optimization
        final String directoryId = reader.getString(directoryPosition, 4, Charsets.ASCII);  // "Should be "SECd"
        final int version = reader.getInt32(4 + directoryPosition);
        final int directoryCount = reader.getInt32(8 + directoryPosition);
        DirectoryIndex[] directoryIndices = new DirectoryIndex[directoryCount];

        int indexPosition = 12 + directoryPosition;
        for (int i = 0; i < directoryCount; i++)
        {
            int directoryIndexOffset = i * 12;
            DirectoryIndex d = new DirectoryIndex();
            d.Offset = reader.getInt32(directoryIndexOffset + directoryPosition);
            d.Length = reader.getInt32(4 + directoryIndexOffset + directoryPosition);
            d.Type = reader.getString(8 + directoryIndexOffset + directoryPosition, 4, Charsets.ASCII);
            directoryIndices[i] = d;
        }

        for (DirectoryIndex dir : directoryIndices)
        {
            if ("PROP".equals(dir.Type)) //PROP, IMAG, IMA2, CAMF, (One was garbage)
            {
                String propId = reader.getString(dir.Offset, 4, Charsets.ASCII);  // "Should be "SECp"
                int propVersion = reader.getInt32(4 + dir.Offset);
                int propCount = reader.getInt32(8 + dir.Offset);
                int charFormat = reader.getInt32(12 + dir.Offset); // 0=char16 uni
//                int reserved = reader.getInt32(16 + dir.Offset); // for posterity
                int propLength = reader.getInt32(20 + dir.Offset);  // property data size in char (16bit)
                int propIndexOffset = 24 + dir.Offset;

                final List<Property> properties = new ArrayList<Property>();
                int propPairOffset = 0;
                for (int i = 0; i < propCount; i++)
                {
                    propPairOffset = i * 8 + propIndexOffset;

                    Property p = new Property();
                    p.NameOffset = reader.getInt32(propPairOffset);
                    p.ValueOffset = reader.getInt32(4 + propPairOffset);

                    properties.add(p);
                }

                int endOfPropHeader = propPairOffset + 8;
                HashMap<String, String> propMap = new HashMap<String, String>();
                for (Property p : properties)
                {
                    // 16-bit null-terminated Unicode character strings (is propLength name AND value?)
                    p.Name = reader.getNullTerminatedString16(endOfPropHeader + 2 * p.NameOffset, propLength, Charsets.UTF_16LE); // TODO: using propLength is NOT ideal here
                    p.Value = reader.getNullTerminatedString16(endOfPropHeader + 2 * p.ValueOffset, propLength, Charsets.UTF_16LE);
                    propMap.put(p.Name, p.Value);
                }
                String name = properties.get(0).Name;
            }
        }
    }

    // quick dirty pojo
    class DirectoryIndex
    {
        int Offset;
        int Length;
        String Type;
    }

    // quick dirty pojo
    class Property
    {
        int NameOffset;
        int ValueOffset;
        String Name;
        String Value;
    }
}
