package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

public class Schema
{
    /**
     * XMP tag namespace. TODO the older "xap", "xapBJ", "xapMM" or "xapRights" namespace prefixes should be translated to the newer "xmp", "xmpBJ",
     * "xmpMM" and "xmpRights" prefixes for use in family 1 group names
     */
    @NotNull
    public static final String XMP_PROPERTIES = "http://ns.adobe.com/xap/1.0/";
    @NotNull
    public static final String EXIF_SPECIFIC_PROPERTIES = "http://ns.adobe.com/exif/1.0/";
    @NotNull
    public static final String EXIF_ADDITIONAL_PROPERTIES = "http://ns.adobe.com/exif/1.0/aux/";
    @NotNull
    public static final String EXIF_TIFF_PROPERTIES = "http://ns.adobe.com/tiff/1.0/";
    @NotNull
    public static final String DUBLIN_CORE_SPECIFIC_PROPERTIES = "http://purl.org/dc/elements/1.1/";
}
