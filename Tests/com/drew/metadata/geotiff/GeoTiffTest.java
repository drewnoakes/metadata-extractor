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
package com.drew.metadata.geotiff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;

import org.junit.Test;

import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;

public class GeoTiffTest {

	@Test
	public void testGeogToWGS84GeoKey5() throws Exception {
		Metadata metadata = TiffMetadataReader.readMetadata(new File("Tests/Data/GeogToWGS84GeoKey5.tif"));
		assertNotNull(metadata);

		ExifIFD0Directory exif = checkExif(metadata, 24);

		assertEquals("[32 values]", exif.getDescription(ExifDirectoryBase.TAG_GEOTIFF_GEO_KEYS));
		assertEquals("[768 values]", exif.getDescription(ExifDirectoryBase.TAG_COLOR_MAP));
		assertEquals("0 0 1", exif.getDescription(ExifDirectoryBase.TAG_PIXEL_SCALE));
		assertEquals("50.5 50.5 0 9.001 52.001 0", exif.getDescription(ExifDirectoryBase.TAG_MODEL_TIE_POINT).replace(',', '.'));
		assertNull(exif.getDescription(ExifDirectoryBase.TAG_GEOTIFF_GEO_ASCII_PARAMS));
		assertNull(exif.getDescription(ExifDirectoryBase.TAG_GEOTIFF_GEO_DOUBLE_PARAMS));
		assertNull(exif.getDescription(ExifDirectoryBase.TAG_GDAL_METADATA));
		assertNull(exif.getDescription(ExifDirectoryBase.TAG_GDAL_NO_DATA));

		GeoTiffDirectory geotiff = checkGeoTiff(metadata);

		assertEquals("Geographic", geotiff.getDescription(GeoTiffDirectory.TAG_MODEL_TYPE));
		assertEquals("PixelIsArea", geotiff.getDescription(GeoTiffDirectory.TAG_RASTER_TYPE));
		assertEquals("User Defined", geotiff.getDescription(GeoTiffDirectory.TAG_GEOGRAPHIC_TYPE));
		assertEquals("User Defined", geotiff.getDescription(GeoTiffDirectory.TAG_GEODETIC_DATUM));
		assertEquals("Angular Degree", geotiff.getDescription(GeoTiffDirectory.TAG_GEOGRAPHIC_ANGULAR_UNITS));
		assertEquals("Bessel 1841", geotiff.getDescription(GeoTiffDirectory.TAG_GEOGRAPHIC_ELLIPSOID));
		assertEquals("598.1 73.7 418.2 0.202 0.045 -2.455 6.7", geotiff.getDescription(GeoTiffDirectory.TAG_GEOGRAPHIC_TO_WGS84).replace(',', '.'));
		assertEquals(7, geotiff.getTagCount());
	}

	@Test
	public void testLibgeotiff() throws Exception {
		for (File tiffFile : new File("Tests/Data/libgeotiff").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".tif");
			}
		}))
		{
			Metadata metadata = TiffMetadataReader.readMetadata(tiffFile);
			assertNotNull(tiffFile.getName(), metadata);
			checkExif(metadata, 14);
			String description = tiffFile.getName() + "\n  " + checkGeoTiff(metadata).getTags();
			assertFalse(description, description.contains("Unknown"));
		}
	}

	private static ExifIFD0Directory checkExif(Metadata metadata, int numberOfTags) {
		ExifIFD0Directory exif = metadata.getDirectoriesOfType(ExifIFD0Directory.class).iterator().next();
		assertNotNull(exif);
		assertFalse(Objects.toString(exif.getErrors()), exif.hasErrors());
		assertEquals(numberOfTags, exif.getTagCount());
		return exif;
	}

	private static GeoTiffDirectory checkGeoTiff(Metadata metadata) {
		GeoTiffDirectory geotiff = metadata.getDirectoriesOfType(GeoTiffDirectory.class).iterator().next();
		assertNotNull(geotiff);
		assertFalse(Objects.toString(geotiff.getErrors()), geotiff.hasErrors());
		assertTrue(geotiff.getTagCount() > 0);
		return geotiff;
	}
}
