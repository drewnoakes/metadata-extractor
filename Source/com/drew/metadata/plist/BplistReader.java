/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.plist;

import java.io.IOException;
import java.util.*;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.Nullable;

/**
 * A limited-functionality binary property list (BPLIST) utility.
 * Parser functionality accounts for &quot;dict&quot; (with simple integer and string values) and &quot;data&quot;.
 *
 * @author Bob Johnson
 * @see https://opensource.apple.com/source/CF/CF-550/ForFoundationOnly.h
 * @see https://opensource.apple.com/source/CF/CF-550/CFBinaryPList.c
 * @see https://synalysis.com/how-to-decode-apple-binary-property-list-files/
 */
public class BplistReader
{
    private static final String PLIST_DTD = "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";

    private static final byte[] BPLIST_HEADER = {'b', 'p', 'l', 'i', 's', 't', '0', '0'};

    /**
     * Ensure that a BPLIST is valid.
     */
    public static boolean isValid(byte[] bplist)
    {
        if (bplist.length < BPLIST_HEADER.length) {
            return false;
        }

        boolean valid = true;
        for (int i = 0; i < BPLIST_HEADER.length; i++) {
            if (bplist[i] != BPLIST_HEADER[i]) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    public static PropertyListResults parse(byte[] bplist) throws IOException
    {
        if (!isValid(bplist)) {
            throw new IllegalArgumentException("Input is not a bplist");
        }

        final ArrayList<Object> objects = new ArrayList<Object>();
        final Trailer trailer = readTrailer(bplist);

        // List out the pointers
        SequentialByteArrayReader reader = new SequentialByteArrayReader(bplist, (int)(trailer.offsetTableOffset + trailer.topObject));
        int[] offsets = new int[(int)trailer.numObjects];
        for (long i = 0; i < trailer.numObjects; i++) {
            if (trailer.offsetIntSize == 1) {
                offsets[(int)i] = reader.getByte();
            } else if (trailer.offsetIntSize == 2) {
                offsets[(int)i] = reader.getUInt16();
            }
        }

        for (int i = 0; i < offsets.length; i++) {
            reader = new SequentialByteArrayReader(bplist, offsets[i]);
            byte marker = reader.getByte();
            int objectFormat = marker >> 4 & 0x0F;
            switch (objectFormat) {
                case 0x0D:    // dict
                    handleDict(i, marker, reader, objects);
                    break;
                case 0x05:    // string (ASCII)
                    int charCount = marker & 0x0F;
                    objects.add(i, reader.getString(charCount));
                    break;
                case 0x04:    // data
                    handleData(i, marker, reader, objects);
                    break;
                case 0x01:    // int
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
        int objectSize = (int)Math.pow(2, (marker & 0x0F));
        if (objectSize == 1) {
            objects.add(objectIndex, reader.getByte());
        } else if (objectSize == 2) {
            objects.add(objectIndex, reader.getUInt16());
        } else if (objectSize == 4) {
            objects.add(objectIndex, reader.getUInt32());
        } else if (objectSize == 8) {
            objects.add(objectIndex, reader.getInt64());
        }
    }

    private static void handleDict(final int objectIndex, final byte marker, final SequentialByteArrayReader reader, final ArrayList<Object> objects) throws IOException
    {
        // Using linked map preserves the key order
        LinkedHashMap<Byte, Byte> map = new LinkedHashMap<Byte, Byte>();
        int dictEntries = marker & 0x0F;
        byte[] keyRefs = new byte[dictEntries];

        for (int j = 0; j < dictEntries; j++) {
            keyRefs[j] = reader.getByte();
        }
        for (int j = 0; j < dictEntries; j++) {
            map.put(keyRefs[j], reader.getByte());
        }

        objects.add(objectIndex, map);
    }

    private static void handleData(final int objectIndex, final byte marker, final SequentialByteArrayReader reader, final ArrayList<Object> objects) throws IOException
    {
        int byteCount = marker & 0x0F;
        if (byteCount == 0x0F) {
            byte sizeMarker = reader.getByte();
            if ((sizeMarker >> 4 & 0x0F) != 1) {
                throw new IllegalArgumentException("Invalid size marker");
            }

            int objectSizeWidth = (int)Math.pow(2, sizeMarker & 0x0F);
            if (objectSizeWidth == 1) {
                byteCount = reader.getInt8();
            } else if (objectSizeWidth == 2) {
                byteCount = reader.getUInt16();
            }
        }

        objects.add(objectIndex, reader.getBytes(byteCount));
    }

    public static class PropertyListResults
    {
        private final List<Object> objects;
        private final Trailer trailer;

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

        @Nullable
        public Set<Map.Entry<Byte, Byte>> getEntrySet()
        {
            final Object topObject = this.getObjects().get((int)this.getTrailer().topObject);

            if (topObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Byte, Byte> dict = (Map<Byte, Byte>)topObject;
                return dict.entrySet();
            }

            return null;
        }

        /**
         * Returns this result object in XML format.
         */
        public String toXML()
        {
            final StringBuilder xml = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append(PLIST_DTD)
                .append("<plist version=\"1.0\">");

            final Set<Map.Entry<Byte, Byte>> entrySet = getEntrySet();

            if (entrySet != null) {
                xml.append("<dict>");

                for (Map.Entry<Byte, Byte> entry : entrySet) {
                    xml.append("<key>")
                       .append((String)this.getObjects().get(entry.getKey()))
                       .append("</key>");
                    xml.append("<integer>")
                       .append(this.getObjects().get(entry.getValue()).toString())
                       .append("</integer>");
                }

                xml.append("</dict>");
            }

            xml.append("</plist>");

            return xml.toString();
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
        reader.skip(5L);    // Skip the 5-byte _unused values
        reader.skip(1L);    // Skip 1-byte sort version

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
}
