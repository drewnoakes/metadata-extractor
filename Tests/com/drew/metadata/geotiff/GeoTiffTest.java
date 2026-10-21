/*
 * Copyright 2002-2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.geotiff;

import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.Charsets;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifTiffHandler;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

import static com.drew.metadata.exif.ExifDirectoryBase.*;
import static com.drew.metadata.geotiff.GeoTiffDirectory.*;
import static org.junit.Assert.*;

public class GeoTiffTest
{
    private static final ByteOrder[] ORDERS = { ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN };

    @Test
    public void testKeysAndGeoreferencingInBothTiffLayouts() throws Exception
    {
        for (boolean big : new boolean[] { false, true }) {
            for (ByteOrder order : ORDERS) {
                String citation = "WGS 84 / UTM zone 14N|";
                double[] matrix = { 1, 0, 0, 702954, 0, -1, 0, 3978341, 0, 0, 1, 0, 0, 0, 0, 1 };
                Field[] fields = {
                    shorts(TAG_GEOTIFF_GEO_KEYS, keys(
                        TAG_MODEL_TYPE, 0, 1, 1,
                        TAG_RASTER_TYPE, 0, 1, 1,
                        TAG_PROJECTED_CS_TYPE, 0, 1, 32614,
                        TAG_PROJECTED_CS_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, citation.length(), 0,
                        TAG_GEOGRAPHIC_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 7, citation.length(),
                        TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS, TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 1, 0)),
                    doubles(TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 6378137),
                    ascii(TAG_GEOTIFF_GEO_ASCII_PARAMS, citation + "WGS 84|\0"),
                    doubles(TAG_PIXEL_SCALE, 0.05, 0.05, 0),
                    doubles(TAG_MODEL_TIE_POINT, 0, 0, 0, 702954, 3978341, 0),
                    doubles(TAG_MODEL_TRANSFORMATION, matrix),
                    shorts(TAG_IMAGE_WIDTH, 7671)
                };
                Metadata metadata = read(big, order, 13, true, fields);
                assertFalse(metadata.hasErrors());
                GeoTiffDirectory geo = geo(metadata);
                ExifIFD0Directory image = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                assertSame(image, geo.getParent());
                assertEquals("Projected", geo.getDescription(TAG_MODEL_TYPE));
                assertEquals("PixelIsArea", geo.getDescription(TAG_RASTER_TYPE));
                assertEquals(32614, geo.getInt(TAG_PROJECTED_CS_TYPE));
                assertEquals("WGS 84 / UTM zone 14N", geo.getString(TAG_PROJECTED_CS_CITATION));
                assertEquals("WGS 84", geo.getString(TAG_GEOGRAPHIC_CITATION));
                assertArrayEquals(new double[] { 6378137 }, (double[])geo.getObject(TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS), 0);
                assertTrue(image.getObject(TAG_GEOTIFF_GEO_DOUBLE_PARAMS) instanceof Double);
                assertArrayEquals(matrix, (double[])image.getObject(TAG_MODEL_TRANSFORMATION), 0);
                assertEquals("Model Transformation", image.getTagName(TAG_MODEL_TRANSFORMATION));
                assertEquals(7671, image.getInt(TAG_IMAGE_WIDTH));
                for (Field field : fields)
                    assertTrue(image.containsTag(field.tag));
            }
        }
    }

    @Test
    public void testShortAndDoubleArraySlicesAndUnknownKeys() throws Exception
    {
        for (boolean big : new boolean[] { false, true }) {
            for (ByteOrder order : ORDERS) {
                Metadata metadata = read(big, order, 0, true, new Field[] {
                    shorts(TAG_GEOTIFF_GEO_KEYS,
                        1, 1, 1, 3,
                        65000, TAG_GEOTIFF_GEO_KEYS, 2, 16,
                        TAG_GEOGRAPHIC_TO_WGS84, TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 2, 1,
                        TAG_PROJECTED_CS_TYPE, 0, 1, 65535,
                        32768, 65535),
                    doubles(TAG_GEOTIFF_GEO_DOUBLE_PARAMS, -1, 598.1, 73.7)
                });
                assertFalse(metadata.hasErrors());
                GeoTiffDirectory geo = geo(metadata);
                assertArrayEquals(new int[] { 32768, 65535 }, geo.getIntArray(65000));
                assertArrayEquals(new double[] { 598.1, 73.7 }, (double[])geo.getObject(TAG_GEOGRAPHIC_TO_WGS84), 0);
                assertEquals("Unknown (65535)", geo.getDescription(TAG_PROJECTED_CS_TYPE));
            }
        }
    }

    @Test
    public void testAsciiByteSlicesAndSingleTrailingPipe() throws Exception
    {
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] {
            shorts(TAG_GEOTIFF_GEO_KEYS, keys(
                TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 6, 2,
                TAG_GEOGRAPHIC_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 1, 7,
                TAG_PROJECTED_CS_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 4, 9,
                65000, TAG_GEOTIFF_GEO_ASCII_PARAMS, 7, 2)),
            ascii(TAG_GEOTIFF_GEO_ASCII_PARAMS, "XXA|B|C||tail\0")
        });
        assertFalse(metadata.hasErrors());
        assertEquals("A|B|C", geo(metadata).getString(TAG_CITATION));
        assertEquals("", geo(metadata).getString(TAG_GEOGRAPHIC_CITATION));
        assertEquals("tail", geo(metadata).getString(TAG_PROJECTED_CS_CITATION));
        assertEquals("A|B|C|", geo(metadata).getString(65000));
    }

    @Test
    public void testEachIfdUsesItsOwnParametersAndParent() throws Exception
    {
        for (boolean big : new boolean[] { false, true }) {
            for (ByteOrder order : ORDERS) {
                for (boolean followers : new boolean[] { false, true }) {
                    Field[] first = {
                        shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 4, 0)),
                        ascii(TAG_GEOTIFF_GEO_ASCII_PARAMS, "one|\0"),
                        followers ? shorts(TAG_PAGE_NUMBER, 0) : pointer(TAG_SUB_IFD_OFFSET, 1)
                    };
                    Field[] second = {
                        shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 4, 0)),
                        ascii(TAG_GEOTIFF_GEO_ASCII_PARAMS, "two|\0")
                    };
                    Metadata metadata = read(big, order, 0, followers, first, second);
                    assertFalse(metadata.hasErrors());
                    Collection<GeoTiffDirectory> directories = metadata.getDirectoriesOfType(GeoTiffDirectory.class);
                    assertEquals(2, directories.size());
                    for (GeoTiffDirectory directory : directories) {
                        Directory parent = directory.getParent();
                        String expected = parent instanceof ExifIFD0Directory ? "one" : "two";
                        assertEquals(expected, directory.getString(TAG_CITATION));
                        assertEquals(expected + "|", ((StringValue)parent.getObject(TAG_GEOTIFF_GEO_ASCII_PARAMS)).toString());
                    }
                }
            }
        }
    }

    @Test
    public void testFollowerWithoutPageNumberAndNoKeysInParent() throws Exception
    {
        Metadata metadata = read(false, ByteOrder.BIG_ENDIAN, 0, true,
            new Field[] { shorts(TAG_IMAGE_WIDTH, 12) },
            new Field[] { shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_MODEL_TYPE, 0, 1, 2)) });
        assertFalse(metadata.hasErrors());
        assertEquals(1, metadata.getDirectoriesOfType(GeoTiffDirectory.class).size());
        assertNotSame(metadata.getFirstDirectoryOfType(ExifIFD0Directory.class), geo(metadata).getParent());
        assertEquals("Geographic", geo(metadata).getDescription(TAG_MODEL_TYPE));
    }

    @Test
    public void testInvalidHeadersRetainRawMetadata() throws Exception
    {
        int[][] headers = {
            { 1 }, { 1, 1, 0 }, { 2, 1, 0, 0 }, { 1, 2, 0, 0 }, { 1, 1, 2, 0 },
            { 1, 1, 0, 65535 }, { 1, 1, 0, 1, 1024, 0, 1 }
        };
        for (boolean big : new boolean[] { false, true }) {
            for (ByteOrder order : ORDERS) {
                for (int[] header : headers) {
                    Metadata metadata = read(big, order, 0, true, new Field[] {
                        shorts(TAG_GEOTIFF_GEO_KEYS, header), shorts(TAG_IMAGE_WIDTH, 123)
                    });
                    assertEquals(1, geo(metadata).getErrorCount());
                    assertEquals(0, geo(metadata).getTagCount());
                    Directory parent = geo(metadata).getParent();
                    assertEquals(123, parent.getInt(TAG_IMAGE_WIDTH));
                    assertArrayEquals(header, parent.getIntArray(TAG_GEOTIFF_GEO_KEYS));
                }
            }
        }
    }

    @Test
    public void testInvalidKeysDoNotHideValidKeys() throws Exception
    {
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] {
            shorts(TAG_GEOTIFF_GEO_KEYS, keys(
                TAG_MODEL_TYPE, 0, 1, 1,
                TAG_MODEL_TYPE, 0, 1, 2,
                TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 0, 0,
                TAG_RASTER_TYPE, 0, 2, 1,
                65001, 42, 1, 0,
                TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS, TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 1, 0,
                TAG_GEOGRAPHIC_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 1, 0,
                65002, TAG_GEOTIFF_GEO_KEYS, 1, 0,
                65003, TAG_GEOTIFF_GEO_KEYS, 65535, 65535,
                TAG_PROJECTED_CS_TYPE, 0, 1, 32614))
        });
        GeoTiffDirectory geo = geo(metadata);
        assertEquals(8, geo.getErrorCount());
        assertEquals(1, geo.getInt(TAG_MODEL_TYPE));
        assertEquals(32614, geo.getInt(TAG_PROJECTED_CS_TYPE));
        assertEquals(2, geo.getTagCount());
        assertNotNull(geo.getParent().getObject(TAG_GEOTIFF_GEO_KEYS));
    }

    @Test
    public void testInvalidParameterTypesAndBounds() throws Exception
    {
        for (Field pool : new Field[] {
            shorts(TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 1),
            doubles(TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 1),
            doubles(TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 1, 2)
        }) {
            Metadata metadata = read(true, ByteOrder.BIG_ENDIAN, 0, true, new Field[] {
                shorts(TAG_GEOTIFF_GEO_KEYS, keys(
                    TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS, TAG_GEOTIFF_GEO_DOUBLE_PARAMS, 2, 1)),
                pool
            });
            assertEquals(1, geo(metadata).getErrorCount());
            assertNotNull(geo(metadata).getParent().getObject(pool.tag));
        }
        for (Field pool : new Field[] {
            shorts(TAG_GEOTIFF_GEO_ASCII_PARAMS, 1), ascii(TAG_GEOTIFF_GEO_ASCII_PARAMS, "A|\0")
        }) {
            Metadata metadata = read(false, ByteOrder.BIG_ENDIAN, 0, true, new Field[] {
                shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 65535, 65535)), pool
            });
            assertEquals(1, geo(metadata).getErrorCount());
        }
    }

    @Test
    public void testNonAsciiParametersAreReported() throws Exception
    {
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] {
            shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_CITATION, TAG_GEOTIFF_GEO_ASCII_PARAMS, 2, 0)),
            new Field(TAG_GEOTIFF_GEO_ASCII_PARAMS, 2, new byte[] { (byte)0xE9, '|', 0 })
        });
        assertEquals(1, geo(metadata).getErrorCount());
        assertNull(geo(metadata).getObject(TAG_CITATION));
    }

    @Test
    public void testWrongDirectoryTypeAndNonGeoTiff() throws Exception
    {
        for (Field field : new Field[] {
            doubles(TAG_GEOTIFF_GEO_KEYS, 1, 1, 0, 0),
            new Field(TAG_GEOTIFF_GEO_KEYS, 8, keys(TAG_MODEL_TYPE, 0, 1, 1)),
            new Field(TAG_GEOTIFF_GEO_KEYS, 9, keys(TAG_MODEL_TYPE, 0, 1, 1))
        }) {
            Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] { field });
            assertEquals(1, geo(metadata).getErrorCount());
            assertEquals("GeoTIFF key directory must have type SHORT", geo(metadata).getErrors().iterator().next());
        }
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] { shorts(TAG_IMAGE_WIDTH, 123) });
        assertFalse(metadata.hasErrors());
        assertNull(metadata.getFirstDirectoryOfType(GeoTiffDirectory.class));
    }

    @Test
    public void testLastDirectoryTagDeterminesItsStorageType() throws Exception
    {
        Field unsigned = shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_MODEL_TYPE, 0, 1, 1));
        Field signed = new Field(TAG_GEOTIFF_GEO_KEYS, 9, keys(TAG_MODEL_TYPE, 0, 1, 2));
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] { unsigned, signed });
        assertEquals(1, geo(metadata).getErrorCount());
        assertNull(geo(metadata).getObject(TAG_MODEL_TYPE));
        metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] { signed, unsigned });
        assertFalse(metadata.hasErrors());
        assertEquals(1, geo(metadata).getInt(TAG_MODEL_TYPE));
    }

    @Test
    public void testInvalidFirstDuplicateIsNotReplaced() throws Exception
    {
        Metadata metadata = read(false, ByteOrder.LITTLE_ENDIAN, 0, true, new Field[] {
            shorts(TAG_GEOTIFF_GEO_KEYS, keys(TAG_MODEL_TYPE, 0, 0, 2, TAG_MODEL_TYPE, 0, 1, 1))
        });
        assertEquals(2, geo(metadata).getErrorCount());
        assertNull(geo(metadata).getObject(TAG_MODEL_TYPE));
    }

    @Test
    public void testEmptyDirectoryAndSingleShortParameter() throws Exception
    {
        for (boolean big : new boolean[] { false, true }) {
            for (ByteOrder order : ORDERS) {
                Metadata metadata = read(big, order, 0, true,
                    new Field[] { shorts(TAG_GEOTIFF_GEO_KEYS, keys()) });
                assertFalse(metadata.hasErrors());
                assertEquals(0, geo(metadata).getTagCount());
                metadata = read(big, order, 0, true, new Field[] {
                    shorts(TAG_GEOTIFF_GEO_KEYS, 1, 1, 0, 1, TAG_MODEL_TYPE, TAG_GEOTIFF_GEO_KEYS, 1, 8, 2)
                });
                assertFalse(metadata.hasErrors());
                assertArrayEquals(new int[] { 2 }, geo(metadata).getIntArray(TAG_MODEL_TYPE));
                assertEquals("Geographic", geo(metadata).getDescription(TAG_MODEL_TYPE));
            }
        }
    }

    @Test
    public void testHistoricalCoordinateSystemDescriptionsAndLabels()
    {
        GeoTiffDirectory directory = new GeoTiffDirectory();
        directory.setInt(TAG_PROJECTED_CS_TYPE, 28191);
        assertEquals("Palestine 1923 Palestine Grid", directory.getDescription(TAG_PROJECTED_CS_TYPE));
        directory.setInt(TAG_PROJECTED_CS_TYPE, 32064);
        assertEquals("NAD27 BLM 14N FtUS", directory.getDescription(TAG_PROJECTED_CS_TYPE));
        directory.setInt(TAG_PROJECTED_CS_TYPE, 32767);
        assertEquals("User Defined", directory.getDescription(TAG_PROJECTED_CS_TYPE));
        assertEquals("Prime Meridian Longitude", directory.getTagName(TAG_GEOGRAPHIC_PRIME_MERIDIAN_LONG));
        assertEquals("Projection Rectified Grid Angle", directory.getTagName(TAG_PROJ_RECTIFIED_GRID_ANGLE));
    }

    private static GeoTiffDirectory geo(Metadata metadata)
    {
        GeoTiffDirectory directory = metadata.getFirstDirectoryOfType(GeoTiffDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    private static int[] keys(int... entries)
    {
        int[] keys = new int[entries.length + 4];
        keys[0] = keys[1] = 1;
        keys[3] = entries.length / 4;
        System.arraycopy(entries, 0, keys, 4, entries.length);
        return keys;
    }

    private static Field shorts(int tag, int... values) { return new Field(tag, 3, values); }
    private static Field doubles(int tag, double... values) { return new Field(tag, 12, values); }
    private static Field ascii(int tag, String value) { return new Field(tag, 2, value.getBytes(Charsets.ASCII)); }
    private static Field pointer(int tag, int ifd) { return new Field(tag, 4, ifd); }

    private static Metadata read(boolean big, ByteOrder order, int prefix, boolean followers, Field[]... ifds) throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(prefix + 8192).order(order);
        int[] offsets = new int[ifds.length];
        int payloadOffset = big ? 16 : 8;
        for (int i = 0; i < ifds.length; i++) {
            offsets[i] = payloadOffset;
            payloadOffset += (big ? 16 : 6) + ifds[i].length * (big ? 20 : 12);
        }
        buffer.position(prefix);
        buffer.putShort(order == ByteOrder.BIG_ENDIAN ? (short)0x4D4D : (short)0x4949);
        buffer.putShort((short)(big ? 43 : 42));
        if (big)
            buffer.putShort((short)8).putShort((short)0).putLong(offsets[0]);
        else
            buffer.putInt(offsets[0]);

        for (int i = 0; i < ifds.length; i++) {
            buffer.position(prefix + offsets[i]);
            if (big)
                buffer.putLong(ifds[i].length);
            else
                buffer.putShort((short)ifds[i].length);
            for (Field field : ifds[i]) {
                ByteBuffer value = ByteBuffer.allocate(256).order(order);
                if (field.values instanceof int[]) {
                    for (int item : (int[])field.values) {
                        if (field.format == 9)
                            value.putInt(item);
                        else
                            value.putShort((short)item);
                    }
                } else if (field.values instanceof double[]) {
                    for (double item : (double[])field.values)
                        value.putDouble(item);
                } else if (field.values instanceof Integer) {
                    value.putInt(offsets[(Integer)field.values]);
                } else {
                    value.put((byte[])field.values);
                }
                int size = value.position();
                int count = size / (field.format == 3 || field.format == 8 ? 2
                    : field.format == 12 ? 8 : field.format == 4 || field.format == 9 ? 4 : 1);
                buffer.putShort((short)field.tag).putShort((short)field.format);
                if (big)
                    buffer.putLong(count);
                else
                    buffer.putInt(count);
                int inlineSize = big ? 8 : 4;
                if (size <= inlineSize) {
                    buffer.put(value.array(), 0, size);
                    for (int j = size; j < inlineSize; j++)
                        buffer.put((byte)0);
                } else {
                    if (big)
                        buffer.putLong(payloadOffset);
                    else
                        buffer.putInt(payloadOffset);
                    System.arraycopy(value.array(), 0, buffer.array(), prefix + payloadOffset, size);
                    payloadOffset += size;
                }
            }
            int next = followers && i + 1 < ifds.length ? offsets[i + 1] : 0;
            if (big)
                buffer.putLong(next);
            else
                buffer.putInt(next);
        }
        Metadata metadata = new Metadata();
        new TiffReader().processTiff(new ByteArrayReader(Arrays.copyOf(buffer.array(), prefix + payloadOffset)),
            new ExifTiffHandler(metadata, null, prefix), prefix);
        return metadata;
    }

    private static final class Field
    {
        final int tag;
        final int format;
        final Object values;

        Field(int tag, int format, Object values)
        {
            this.tag = tag;
            this.format = format;
            this.values = values;
        }
    }
}
