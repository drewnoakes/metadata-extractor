package com.drew.metadata.mp4;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class Mp4BoxTypes
{
    public static final String ATOM_FILE_TYPE                = "ftyp";
    public static final String ATOM_MOVIE_HEADER             = "mvhd";
    public static final String ATOM_VIDEO_MEDIA_INFO         = "vmhd";
    public static final String ATOM_SOUND_MEDIA_INFO         = "smhd";
    public static final String ATOM_HINT_MEDIA_INFO          = "hmhd";
    public static final String ATOM_NULL_MEDIA_INFO          = "nmhd";
    public static final String ATOM_HANDLER                  = "hdlr";
    public static final String ATOM_SAMPLE_DESCRIPTION       = "stsd";
    public static final String ATOM_TIME_TO_SAMPLE           = "stts";
    public static final String ATOM_MEDIA_HEADER             = "mdhd";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_FILE_TYPE);
        _atomList.add(ATOM_MOVIE_HEADER);
        _atomList.add(ATOM_VIDEO_MEDIA_INFO);
        _atomList.add(ATOM_SOUND_MEDIA_INFO);
        _atomList.add(ATOM_HINT_MEDIA_INFO);
        _atomList.add(ATOM_NULL_MEDIA_INFO);
        _atomList.add(ATOM_HANDLER);
        _atomList.add(ATOM_SAMPLE_DESCRIPTION);
        _atomList.add(ATOM_TIME_TO_SAMPLE);
        _atomList.add(ATOM_MEDIA_HEADER);
    }
}
