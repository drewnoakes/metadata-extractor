package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtContainerTypes
{
    public static final String ATOM_MOVIE = "moov";
    public static final String ATOM_MOVIE_CLIPPING = "clip";
    public static final String ATOM_USER_DATA = "udta";
    public static final String ATOM_TRACK = "trak";
    public static final String ATOM_MEDIA = "mdia";
    public static final String ATOM_MEDIA_INFO = "minf";
    public static final String ATOM_META = "meta";
    public static final String ATOM_TRACK_HEADER = "tkhd";

    public static final String ATOM_MDIA_MINF = "minf";
    public static final String ATOM_MINF_STBL = "stbl";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_MOVIE);
        _containerList.add(ATOM_MOVIE_CLIPPING);
        _containerList.add(ATOM_USER_DATA);
        _containerList.add(ATOM_TRACK);
        _containerList.add(ATOM_MEDIA);
        _containerList.add(ATOM_MEDIA_INFO);
        _containerList.add(ATOM_META);
        _containerList.add(ATOM_TRACK_HEADER);

        _containerList.add(ATOM_MDIA_MINF);
        _containerList.add(ATOM_MINF_STBL);
    }

    public static boolean isContainer(String fourCC)
    {
        return _containerList.contains(fourCC);
    }
}
