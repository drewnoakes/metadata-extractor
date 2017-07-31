package com.drew.metadata.mp4;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class Mp4ContainerTypes
{
    public static final String BOX_MOVIE                            = "moov";
    public static final String BOX_USER_DATA                        = "udta";
    public static final String BOX_TRACK                            = "trak";
    public static final String BOX_MEDIA                            = "mdia";
    public static final String BOX_MEDIA_INFORMATION                = "minf";
    public static final String BOX_SAMPLE_TABLE                     = "stbl";
    public static final String BOX_METADATA_LIST                    = "ilst";
    public static final String BOX_METADATA                         = "meta";
    public static final String BOX_COMPRESSED_MOVIE                 = "cmov";
    public static final String BOX_MEDIA_TEXT                       = "text";
    public static final String BOX_MEDIA_SUBTITLE                   = "sbtl";
    public static final String BOX_MEDIA_BASE                       = "gmhd";
    public static final String BOX_MEDIA_NULL                       = "nmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(BOX_MOVIE);
        _containerList.add(BOX_USER_DATA);
        _containerList.add(BOX_TRACK);
        _containerList.add(BOX_MEDIA);
        _containerList.add(BOX_MEDIA_INFORMATION);
        _containerList.add(BOX_SAMPLE_TABLE);
        _containerList.add(BOX_METADATA);
        _containerList.add(BOX_METADATA_LIST);
        _containerList.add(BOX_COMPRESSED_MOVIE);
        _containerList.add(BOX_MEDIA_TEXT);
        _containerList.add(BOX_MEDIA_SUBTITLE);
        _containerList.add(BOX_MEDIA_BASE);
        _containerList.add(BOX_MEDIA_NULL);
    }
}
