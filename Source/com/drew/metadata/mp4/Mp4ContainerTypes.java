package com.drew.metadata.mp4;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class Mp4ContainerTypes
{
    public static final String ATOM_MOVIE                       = "moov";
    public static final String ATOM_USER_DATA                   = "udta";
    public static final String ATOM_TRACK                       = "trak";
    public static final String ATOM_MEDIA                       = "mdia";
    public static final String ATOM_MEDIA_INFORMATION           = "minf";
    public static final String ATOM_SAMPLE_TABLE                = "stbl";
    public static final String ATOM_METADATA_LIST               = "ilst";
    public static final String ATOM_METADATA                    = "meta";
    public static final String ATOM_COMPRESSED_MOVIE            = "cmov";
    public static final String ATOM_MEDIA_TEXT                  = "text";
    public static final String ATOM_MEDIA_SUBTITLE              = "sbtl";
    public static final String ATOM_MEDIA_BASE                  = "gmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_MOVIE);
        _containerList.add(ATOM_USER_DATA);
        _containerList.add(ATOM_TRACK);
        _containerList.add(ATOM_MEDIA);
        _containerList.add(ATOM_MEDIA_INFORMATION);
        _containerList.add(ATOM_SAMPLE_TABLE);
        _containerList.add(ATOM_METADATA);
        _containerList.add(ATOM_METADATA_LIST);
        _containerList.add(ATOM_COMPRESSED_MOVIE);
        _containerList.add(ATOM_MEDIA_TEXT);
        _containerList.add(ATOM_MEDIA_SUBTITLE);
        _containerList.add(ATOM_MEDIA_BASE);
    }
}
