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
package com.drew.metadata.jpeg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

/**
 * Directory of tables for the DHT (Define Huffman Table(s)) segment.
 *
 * @author Nadahar
 */
@SuppressWarnings("WeakerAccess")
public class HuffmanTablesDirectory extends Directory {

    public static final int TAG_NUMBER_OF_TABLES = 1;

    protected static final byte[] TYPICAL_LUMINANCE_DC_LENGTHS = {
        (byte) 0x00, (byte) 0x01, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    protected static final byte[] TYPICAL_LUMINANCE_DC_VALUES = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
        (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B
    };

    protected static final byte[] TYPICAL_CHROMINANCE_DC_LENGTHS = {
        (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };

    protected static final byte[] TYPICAL_CHROMINANCE_DC_VALUES = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
        (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B
    };

    protected static final byte[] TYPICAL_LUMINANCE_AC_LENGTHS = {
        (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x03, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x03,
        (byte) 0x05, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x7D
    };

    protected static final byte[] TYPICAL_LUMINANCE_AC_VALUES = {
        (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x05, (byte) 0x12,
        (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x06, (byte) 0x13, (byte) 0x51, (byte) 0x61, (byte) 0x07,
        (byte) 0x22, (byte) 0x71, (byte) 0x14, (byte) 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xA1, (byte) 0x08,
        (byte) 0x23, (byte) 0x42, (byte) 0xB1, (byte) 0xC1, (byte) 0x15, (byte) 0x52, (byte) 0xD1, (byte) 0xF0,
        (byte) 0x24, (byte) 0x33, (byte) 0x62, (byte) 0x72, (byte) 0x82, (byte) 0x09, (byte) 0x0A, (byte) 0x16,
        (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28,
        (byte) 0x29, (byte) 0x2A, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39,
        (byte) 0x3A, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49,
        (byte) 0x4A, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59,
        (byte) 0x5A, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69,
        (byte) 0x6A, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79,
        (byte) 0x7A, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89,
        (byte) 0x8A, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98,
        (byte) 0x99, (byte) 0x9A, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
        (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4, (byte) 0xB5, (byte) 0xB6,
        (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xC2, (byte) 0xC3, (byte) 0xC4, (byte) 0xC5,
        (byte) 0xC6, (byte) 0xC7, (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4,
        (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xE1, (byte) 0xE2,
        (byte) 0xE3, (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9, (byte) 0xEA,
        (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8,
        (byte) 0xF9, (byte) 0xFA
    };

    protected static final byte[] TYPICAL_CHROMINANCE_AC_LENGTHS = {
        (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x04, (byte) 0x03, (byte) 0x04,
        (byte) 0x07, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x77
    };

    protected static final byte[] TYPICAL_CHROMINANCE_AC_VALUES = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x11, (byte) 0x04, (byte) 0x05, (byte) 0x21,
        (byte) 0x31, (byte) 0x06, (byte) 0x12, (byte) 0x41, (byte) 0x51, (byte) 0x07, (byte) 0x61, (byte) 0x71,
        (byte) 0x13, (byte) 0x22, (byte) 0x32, (byte) 0x81, (byte) 0x08, (byte) 0x14, (byte) 0x42, (byte) 0x91,
        (byte) 0xA1, (byte) 0xB1, (byte) 0xC1, (byte) 0x09, (byte) 0x23, (byte) 0x33, (byte) 0x52, (byte) 0xF0,
        (byte) 0x15, (byte) 0x62, (byte) 0x72, (byte) 0xD1, (byte) 0x0A, (byte) 0x16, (byte) 0x24, (byte) 0x34,
        (byte) 0xE1, (byte) 0x25, (byte) 0xF1, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x26,
        (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2A, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
        (byte) 0x39, (byte) 0x3A, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48,
        (byte) 0x49, (byte) 0x4A, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
        (byte) 0x59, (byte) 0x5A, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68,
        (byte) 0x69, (byte) 0x6A, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78,
        (byte) 0x79, (byte) 0x7A, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
        (byte) 0x88, (byte) 0x89, (byte) 0x8A, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
        (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9A, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5,
        (byte) 0xA6, (byte) 0xA7, (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4,
        (byte) 0xB5, (byte) 0xB6, (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xC2, (byte) 0xC3,
        (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7, (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xD2,
        (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA,
        (byte) 0xE2, (byte) 0xE3, (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9,
        (byte) 0xEA, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8,
        (byte) 0xF9, (byte) 0xFA
    };

    @NotNull
    protected final List<HuffmanTable> tables = new ArrayList<HuffmanTable>(4);

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_NUMBER_OF_TABLES, "Number of Tables");
    }

    public HuffmanTablesDirectory()
    {
        this.setDescriptor(new HuffmanTablesDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Huffman";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * @param tableNumber The zero-based index of the table. This number is normally between 0 and 3.
     *                    Use {@link #getNumberOfTables} for bounds-checking.
     * @return The {@link HuffmanTable} having the specified number.
     */
    @NotNull
    public HuffmanTable getTable(int tableNumber)
    {
        return tables.get(tableNumber);
    }

    /**
     * @return The number of Huffman tables held by this {@link HuffmanTablesDirectory} instance.
     */
    public int getNumberOfTables() throws MetadataException
    {
        return getInt(HuffmanTablesDirectory.TAG_NUMBER_OF_TABLES);
    }

    /**
     * @return The {@link List} of {@link HuffmanTable}s in this
     *         {@link Directory}.
     */
    @NotNull
    protected List<HuffmanTable> getTables() {
        return tables;
    }

    /**
     * Evaluates whether all the tables in this {@link HuffmanTablesDirectory}
     * are "typical" Huffman tables.
     * <p>
     * "Typical" has a special meaning in this context as the JPEG standard
     * (ISO/IEC 10918 or ITU-T T.81) defines 4 Huffman tables that has been
     * developed from the average statistics of a large set of images with 8-bit
     * precision. Using these instead of calculating the optimal Huffman tables
     * for a given image is faster, and is preferred by many hardware encoders
     * and some hardware decoders.
     * <p>
     * Even though the JPEG standard doesn't define these as "standard tables"
     * and requires a decoder to be able to read any valid Huffman tables, some
     * are in reality limited decoding images using these "typical" tables.
     * Standards like DCF (Design rule for Camera File system) and DLNA (Digital
     * Living Network Alliance) actually requires any compliant JPEG to use only
     * the "typical" Huffman tables.
     * <p>
     * This is also related to the term "optimized" JPEG. An "optimized" JPEG is
     * a JPEG that doesn't use the "typical" Huffman tables.
     *
     * @return Whether or not all the tables in this
     *         {@link HuffmanTablesDirectory} are the predefined "typical"
     *         Huffman tables.
     */
    public boolean isTypical() {
        if (tables.size() == 0) {
            return false;
        }
        for (HuffmanTable table : tables) {
            if (!table.isTypical()) {
                return false;
            }
        }
        return true;
    }

    /**
     * The opposite of {@link #isTypical()}.
     *
     * @return Whether or not the tables in this {@link HuffmanTablesDirectory}
     *         are "optimized" - which means that at least one of them aren't
     *         one of the "typical" Huffman tables.
     */
    public boolean isOptimized() {
        return !isTypical();
    }

    /**
     * An instance of this class holds a JPEG Huffman table.
     */
    public static class HuffmanTable {
        private final int tableLength;
        private final HuffmanTableClass tableClass;
        private final int tableDestinationId;
        private final byte[] lengthBytes;
        private final byte[] valueBytes;

        public HuffmanTable (
            @NotNull HuffmanTableClass
            tableClass,
            int tableDestinationId,
            @NotNull byte[] lBytes,
            @NotNull byte[] vBytes
        ) {
            this.tableClass = tableClass;
            this.tableDestinationId = tableDestinationId;
            this.lengthBytes = lBytes;
            this.valueBytes = vBytes;
            this.tableLength = vBytes.length + 17;
        }

        /**
         * @return The table length in bytes.
         */
        public int getTableLength() {
            return tableLength;
        }


        /**
         * @return The {@link HuffmanTableClass} of this table.
         */
        public HuffmanTableClass getTableClass() {
            return tableClass;
        }


        /**
         * @return the the destination identifier for this table.
         */
        public int getTableDestinationId() {
            return tableDestinationId;
        }


        /**
         * @return A byte array with the L values for this table.
         */
        public byte[] getLengthBytes() {
            if (lengthBytes == null)
                return null;
            byte[] result = new byte[lengthBytes.length];
            System.arraycopy(lengthBytes, 0, result, 0, lengthBytes.length);
            return result;
        }


        /**
         * @return A byte array with the V values for this table.
         */
        public byte[] getValueBytes() {
            if (valueBytes == null)
                return null;
            byte[] result = new byte[valueBytes.length];
            System.arraycopy(valueBytes, 0, result, 0, valueBytes.length);
            return result;
        }

        /**
         * Evaluates whether this table is a "typical" Huffman table.
         * <p>
         * "Typical" has a special meaning in this context as the JPEG standard
         * (ISO/IEC 10918 or ITU-T T.81) defines 4 Huffman tables that has been
         * developed from the average statistics of a large set of images with
         * 8-bit precision. Using these instead of calculating the optimal
         * Huffman tables for a given image is faster, and is preferred by many
         * hardware encoders and some hardware decoders.
         * <p>
         * Even though the JPEG standard doesn't define these as
         * "standard tables" and requires a decoder to be able to read any valid
         * Huffman tables, some are in reality limited decoding images using
         * these "typical" tables. Standards like DCF (Design rule for Camera
         * File system) and DLNA (Digital Living Network Alliance) actually
         * requires any compliant JPEG to use only the "typical" Huffman tables.
         * <p>
         * This is also related to the term "optimized" JPEG. An "optimized"
         * JPEG is a JPEG that doesn't use the "typical" Huffman tables.
         *
         * @return Whether or not this table is one of the predefined "typical"
         *         Huffman tables.
         */
        public boolean isTypical() {
            if (tableClass == HuffmanTableClass.DC) {
                return
                    Arrays.equals(lengthBytes, TYPICAL_LUMINANCE_DC_LENGTHS) &&
                    Arrays.equals(valueBytes, TYPICAL_LUMINANCE_DC_VALUES) ||
                    Arrays.equals(lengthBytes, TYPICAL_CHROMINANCE_DC_LENGTHS) &&
                    Arrays.equals(valueBytes, TYPICAL_CHROMINANCE_DC_VALUES);
            } else if (tableClass == HuffmanTableClass.AC) {
                return
                    Arrays.equals(lengthBytes, TYPICAL_LUMINANCE_AC_LENGTHS) &&
                    Arrays.equals(valueBytes, TYPICAL_LUMINANCE_AC_VALUES) ||
                    Arrays.equals(lengthBytes, TYPICAL_CHROMINANCE_AC_LENGTHS) &&
                    Arrays.equals(valueBytes, TYPICAL_CHROMINANCE_AC_VALUES);
            }
            return false;
        }

        /**
         * The opposite of {@link #isTypical()}.
         *
         * @return Whether or not this table is "optimized" - which means that
         *         it isn't one of the "typical" Huffman tables.
         */
        public boolean isOptimized() {
            return !isTypical();
        }

        public enum HuffmanTableClass {
            DC,
            AC,
            UNKNOWN;

            public static HuffmanTableClass typeOf(int value) {
                switch (value) {
                    case 0: return DC;
                    case 1 : return AC;
                    default: return UNKNOWN;
                }
            }
        }
    }
}
