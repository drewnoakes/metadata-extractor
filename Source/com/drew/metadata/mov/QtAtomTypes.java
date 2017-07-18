package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtAtomTypes
{
    public static final String ATOM_WIDE = "wide";
    public static final String ATOM_MEDIA_HEADER = "mdhd";
    public static final String ATOM_VIDEO_HEADER = "vmhd";
    public static final String ATOM_VIDEO_INFO = "vmhd";
    public static final String ATOM_TIME_TO_SAMPLE = "stts";
    public static final String ATOM_META_LIST = "ilst";
    public static final String ATOM_META_KEYS = "keys";
    public static final String ATOM_META_HDLR = "hdlr";

    public static final String ATOM_FILE_TYPE = "ftyp";
    public static final String ATOM_PREVIEW = "pnot";
    public static final String ATOM_PROFILE = "prfl";
    public static final String ATOM_MOVIE_HEADER = "mvhd";
    public static final String ATOM_CLIPPING_REGION = "crgn";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_FILE_TYPE);
        _atomList.add(ATOM_PREVIEW);
        _atomList.add(ATOM_PROFILE);
        _atomList.add(ATOM_MOVIE_HEADER);
        _atomList.add(ATOM_CLIPPING_REGION);

        _atomList.add(ATOM_WIDE);
        _atomList.add(ATOM_MEDIA_HEADER);
        _atomList.add(ATOM_VIDEO_HEADER);
        _atomList.add(ATOM_VIDEO_INFO);
        _atomList.add(ATOM_TIME_TO_SAMPLE);
        _atomList.add(ATOM_META_LIST);
        _atomList.add(ATOM_META_KEYS);
        _atomList.add(ATOM_META_HDLR);
    }

    public static boolean isAccepted(String fourCC)
    {
        return _atomList.contains(fourCC);
    }
}
