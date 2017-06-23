package com.drew.metadata.mov;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class QtContainerTypes
{
    public static final String ATOM_WIDE = "wide";
    public static final String ATOM_MOVIE = "moov";
    public static final String ATOM_MOVIE_TRAK = "trak";
    public static final String ATOM_TRAK_MDIA = "mdia";
    public static final String ATOM_MOVIE_META = "meta";
    public static final String ATOM_MDIA_MINF = "minf";
    public static final String ATOM_MINF_STBL = "stbl";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_WIDE);
        _containerList.add(ATOM_MOVIE);
        _containerList.add(ATOM_MOVIE_TRAK);
        _containerList.add(ATOM_TRAK_MDIA);
        _containerList.add(ATOM_MDIA_MINF);
        _containerList.add(ATOM_MINF_STBL);
    }

    public static boolean isContainer(String fourCC)
    {
        return _containerList.contains(fourCC);
    }
}
