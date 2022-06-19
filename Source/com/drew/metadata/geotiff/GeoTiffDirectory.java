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

import java.util.HashMap;

import com.drew.metadata.Directory;

public class GeoTiffDirectory extends Directory {

    public static final int TAG_MODEL_TYPE                            = 0x0400;
    public static final int TAG_RASTER_TYPE                           = 0x0401;
    public static final int TAG_CITATION                              = 0x0402;

    public static final int TAG_GEOGRAPHIC_TYPE                       = 0x0800;
    public static final int TAG_GEOGRAPHIC_CITATION                   = 0x0801;
    public static final int TAG_GEODETIC_DATUM                        = 0x0802;
    public static final int TAG_GEOGRAPHIC_PRIME_MERIDIAN             = 0x0803;
    public static final int TAG_GEOGRAPHIC_LINEAR_UNITS               = 0x0804;
    public static final int TAG_GEOGRAPHIC_LINEAR_UNIT_SIZE           = 0x0805;
    public static final int TAG_GEOGRAPHIC_ANGULAR_UNITS              = 0x0806;
    public static final int TAG_GEOGRAPHIC_ANGULAR_UNIT_SIZE          = 0x0807;
    public static final int TAG_GEOGRAPHIC_ELLIPSOID                  = 0x0808;
    public static final int TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS            = 0x0809;
    public static final int TAG_GEOGRAPHIC_SEMI_MINOR_AXIS            = 0x080a;
    public static final int TAG_GEOGRAPHIC_INV_FLATTENING             = 0x080b;
    public static final int TAG_GEOGRAPHIC_AZIMUTH_UNITS              = 0x080c;
    public static final int TAG_GEOGRAPHIC_PRIME_MERIDIAN_LONG        = 0x080d;
    public static final int TAG_GEOGRAPHIC_TO_WGS84                   = 0x080e;

    // https://trac.osgeo.org/gdal/ticket/3901#comment:7
    // https://github.com/opengeospatial/geotiff/pull/106
    public static final int TAG_GDAL_PROJ_LINEAR_UNITS_INTERP_CORRECT = 0x0bf3;

    public static final int TAG_PROJECTED_CS_TYPE                     = 0x0c00;
    public static final int TAG_PROJECTED_CSCITATION                  = 0x0c01;
    public static final int TAG_PROJECTION                            = 0x0c02;
    public static final int TAG_PROJECTED_COORDINATE_TRANSFORM        = 0x0c03;
    public static final int TAG_PROJ_LINEAR_UNITS                     = 0x0c04;
    public static final int TAG_PROJ_LINEAR_UNIT_SIZE                 = 0x0c05;
    public static final int TAG_PROJ_STD_PARALLEL_1                   = 0x0c06;
    public static final int TAG_PROJ_STD_PARALLEL_2                   = 0x0c07;
    public static final int TAG_PROJ_NAT_ORIGIN_LONG                  = 0x0c08;
    public static final int TAG_PROJ_NAT_ORIGIN_LAT                   = 0x0c09;
    public static final int TAG_PROJ_FALSE_EASTING                    = 0x0c0a;
    public static final int TAG_PROJ_FALSE_NORTHING                   = 0x0c0b;
    public static final int TAG_PROJ_FALSE_ORIGIN_LONG                = 0x0c0c;
    public static final int TAG_PROJ_FALSE_ORIGIN_LAT                 = 0x0c0d;
    public static final int TAG_PROJ_FALSE_ORIGIN_EASTING             = 0x0c0e;
    public static final int TAG_PROJ_FALSE_ORIGIN_NORTHING            = 0x0c0f;
    public static final int TAG_PROJ_CENTER_LONG                      = 0x0c10;
    public static final int TAG_PROJ_CENTER_LAT                       = 0x0c11;
    public static final int TAG_PROJ_CENTER_EASTING                   = 0x0c12;
    public static final int TAG_PROJ_CENTER_NORTHING                  = 0x0c13;
    public static final int TAG_PROJ_SCALE_AT_NAT_ORIGIN              = 0x0c14;
    public static final int TAG_PROJ_SCALE_AT_CENTER                  = 0x0c15;
    public static final int TAG_PROJ_AZIMUTH_ANGLE                    = 0x0c16;
    public static final int TAG_PROJ_STRAIGHT_VERT_POLE_LONG          = 0x0c17;
    public static final int TAG_PROJ_RECTIFIED_GRID_ANGLE             = 0x0c18;

    public static final int TAG_VERTICAL_CS_TYPE                      = 0x1000;
    public static final int TAG_VERTICAL_CITATION                     = 0x1001;
    public static final int TAG_VERTICAL_DATUM                        = 0x1002;
    public static final int TAG_VERTICAL_UNITS                        = 0x1003;

    public static final int TAG_CHART_FORMAT                          = 0xb799;
    public static final int TAG_CHART_SOURCE                          = 0xb79a;
    public static final int TAG_CHART_SOURCE_EDITION                  = 0xb79b;
    public static final int TAG_CHART_SOURCE_DATE                     = 0xb79c;
    public static final int TAG_CHART_CORR_DATE                       = 0xb79d;
    public static final int TAG_CHART_COUNTRY_ORIGIN                  = 0xb79e;
    public static final int TAG_CHART_RASTER_EDITION                  = 0xb79f;
    public static final int TAG_CHART_SOUNDING_DATUM                  = 0xb7a0;
    public static final int TAG_CHART_DEPTH_UNITS                     = 0xb7a1;
    public static final int TAG_CHART_MAG_VAR                         = 0xb7a2;
    public static final int TAG_CHART_MAG_VAR_YEAR                    = 0xb7a3;
    public static final int TAG_CHART_MAG_VAR_ANN_CHANGE              = 0xb7a4;
    public static final int TAG_CHART_WGS_NS_SHIFT                    = 0xb7a5;
    public static final int TAG_INSET_NW_PIXEL_X                      = 0xb7a7;
    public static final int TAG_INSET_NW_PIXEL_Y                      = 0xb7a8;
    public static final int TAG_CHART_CONTOUR_INTERVAL                = 0xb7a9;

    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();
    static
    {
        _tagNameMap.put(TAG_MODEL_TYPE, "Model Type");
        _tagNameMap.put(TAG_RASTER_TYPE, "Raster Type");
        _tagNameMap.put(TAG_CITATION, "Citation");
        _tagNameMap.put(TAG_GEOGRAPHIC_TYPE, "Geographic Type");
        _tagNameMap.put(TAG_GEOGRAPHIC_CITATION, "Geographic Citation");
        _tagNameMap.put(TAG_GEODETIC_DATUM, "Geodetic Datum");
        _tagNameMap.put(TAG_GEOGRAPHIC_PRIME_MERIDIAN, "Prime Meridian");
        _tagNameMap.put(TAG_GEOGRAPHIC_LINEAR_UNITS, "Geographic Linear Units");
        _tagNameMap.put(TAG_GEOGRAPHIC_LINEAR_UNIT_SIZE, "Geographic Linear Unit Size");
        _tagNameMap.put(TAG_GEOGRAPHIC_ANGULAR_UNITS, "Geographic Angular Units");
        _tagNameMap.put(TAG_GEOGRAPHIC_ANGULAR_UNIT_SIZE, "Geographic Angular Unit Size");
        _tagNameMap.put(TAG_GEOGRAPHIC_ELLIPSOID, "Geographic Ellipsoid");
        _tagNameMap.put(TAG_GEOGRAPHIC_SEMI_MAJOR_AXIS, "Semi-major axis");
        _tagNameMap.put(TAG_GEOGRAPHIC_SEMI_MINOR_AXIS, "Semi-minor axis");
        _tagNameMap.put(TAG_GEOGRAPHIC_INV_FLATTENING, "Inv. Flattening");
        _tagNameMap.put(TAG_GEOGRAPHIC_AZIMUTH_UNITS, "Azimuth Units");
        _tagNameMap.put(TAG_GEOGRAPHIC_PRIME_MERIDIAN_LONG, "To WGS84");
        _tagNameMap.put(TAG_GEOGRAPHIC_TO_WGS84, "To WGS84");
        _tagNameMap.put(TAG_GDAL_PROJ_LINEAR_UNITS_INTERP_CORRECT, "GDAL ProjLinearUnitsInterpCorrectGeoKey");
        _tagNameMap.put(TAG_PROJECTED_CS_TYPE, "Projected Coordinate System Type");
        _tagNameMap.put(TAG_PROJECTED_CSCITATION, "Projected Coordinate System Citation");
        _tagNameMap.put(TAG_PROJECTION, "Projection");
        _tagNameMap.put(TAG_PROJECTED_COORDINATE_TRANSFORM, "Projected Coordinate Transform");
        _tagNameMap.put(TAG_PROJ_LINEAR_UNITS, "Projection Linear Units");
        _tagNameMap.put(TAG_PROJ_LINEAR_UNIT_SIZE, "Projection Linear Unit Size");
        _tagNameMap.put(TAG_PROJ_STD_PARALLEL_1, "Projection Standard Parallel 1");
        _tagNameMap.put(TAG_PROJ_STD_PARALLEL_2, "Projection Standard Parallel 2");
        _tagNameMap.put(TAG_PROJ_NAT_ORIGIN_LONG, "Projection Natural Origin Longitude");
        _tagNameMap.put(TAG_PROJ_NAT_ORIGIN_LAT, "Projection Natural Origin Latitude");
        _tagNameMap.put(TAG_PROJ_FALSE_EASTING, "Projection False Easting");
        _tagNameMap.put(TAG_PROJ_FALSE_NORTHING, "Projection False Northing");
        _tagNameMap.put(TAG_PROJ_FALSE_ORIGIN_LONG, "Projection False Origin Longitude");
        _tagNameMap.put(TAG_PROJ_FALSE_ORIGIN_LAT, "Projection False Origin Latitude");
        _tagNameMap.put(TAG_PROJ_FALSE_ORIGIN_EASTING, "Projection False Origin Easting");
        _tagNameMap.put(TAG_PROJ_FALSE_ORIGIN_NORTHING, "Projection False Origin Northing");
        _tagNameMap.put(TAG_PROJ_CENTER_LONG, "Projection Center Longitude");
        _tagNameMap.put(TAG_PROJ_CENTER_LAT, "Projection Center Latitude");
        _tagNameMap.put(TAG_PROJ_CENTER_EASTING, "Projection Center Easting");
        _tagNameMap.put(TAG_PROJ_CENTER_NORTHING, "Projection Center Northing");
        _tagNameMap.put(TAG_PROJ_SCALE_AT_NAT_ORIGIN, "Projection Scale at Natural Origin");
        _tagNameMap.put(TAG_PROJ_SCALE_AT_CENTER, "Projection Scale at Center");
        _tagNameMap.put(TAG_PROJ_AZIMUTH_ANGLE, "Projection Azimuth Angle");
        _tagNameMap.put(TAG_PROJ_STRAIGHT_VERT_POLE_LONG, "Projection Straight Vertical Pole Longitude");
        _tagNameMap.put(TAG_PROJ_RECTIFIED_GRID_ANGLE, "Projection Straight Vertical Pole Latitude");
        _tagNameMap.put(TAG_VERTICAL_CS_TYPE, "Vertical Coordinate System Type");
        _tagNameMap.put(TAG_VERTICAL_CITATION, "Vertical Citation");
        _tagNameMap.put(TAG_VERTICAL_DATUM, "Vertical Datum");
        _tagNameMap.put(TAG_VERTICAL_UNITS, "Vertical Units");
        _tagNameMap.put(TAG_CHART_FORMAT, "Chart Format");
        _tagNameMap.put(TAG_CHART_SOURCE, "Chart Source");
        _tagNameMap.put(TAG_CHART_SOURCE_EDITION, "Chart Source Edition");
        _tagNameMap.put(TAG_CHART_SOURCE_DATE, "Chart Source Date");
        _tagNameMap.put(TAG_CHART_CORR_DATE, "Chart Corr Date");
        _tagNameMap.put(TAG_CHART_COUNTRY_ORIGIN, "Chart Country Origin");
        _tagNameMap.put(TAG_CHART_RASTER_EDITION, "Chart Raster Edition");
        _tagNameMap.put(TAG_CHART_SOUNDING_DATUM, "Chart Sounding Datum");
        _tagNameMap.put(TAG_CHART_DEPTH_UNITS, "Chart Depth Units");
        _tagNameMap.put(TAG_CHART_MAG_VAR, "Chart Mag Var");
        _tagNameMap.put(TAG_CHART_MAG_VAR_YEAR, "Chart Mag Var Year");
        _tagNameMap.put(TAG_CHART_MAG_VAR_ANN_CHANGE, "Chart Mag Var Annual Change");
        _tagNameMap.put(TAG_CHART_WGS_NS_SHIFT, "Chart WGSNS Shift");
        _tagNameMap.put(TAG_INSET_NW_PIXEL_X, "Inset NW Pixel X");
        _tagNameMap.put(TAG_INSET_NW_PIXEL_Y, "Inset NW Pixel Y");
        _tagNameMap.put(TAG_CHART_CONTOUR_INTERVAL, "Chart Contour Interval");
    }

    public GeoTiffDirectory()
    {
        setDescriptor(new GeoTiffDescriptor(this));
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    @Override
    public String getName()
    {
        return "GeoTIFF";
    }
}
