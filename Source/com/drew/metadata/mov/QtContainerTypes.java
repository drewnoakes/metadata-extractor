package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtContainerTypes
{
    // Contains information that describes the movie's data
    public static final String ATOM_MOVIE = "moov";

    public static final String ATOM_MOVIE_CLIPPING = "clip";
    public static final String ATOM_USER_DATA = "udta";
    public static final String ATOM_TRACK = "trak";
    public static final String ATOM_MEDIA = "mdia";
    public static final String ATOM_MEDIA_INFORMATION = "minf";
    public static final String ATOM_DATA_INFORMATION = "dinf";
    public static final String ATOM_SAMPLE_TABLE = "stbl";

    public static final String ATOM_METADATA = "meta";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_MOVIE);

        _containerList.add(ATOM_MOVIE_CLIPPING);
        _containerList.add(ATOM_USER_DATA);
        _containerList.add(ATOM_TRACK);
        _containerList.add(ATOM_MEDIA);
        _containerList.add(ATOM_MEDIA_INFORMATION);
        _containerList.add(ATOM_DATA_INFORMATION);
        _containerList.add(ATOM_SAMPLE_TABLE);

        _containerList.add(ATOM_METADATA);
    }

    public static boolean isContainer(String fourCC)
    {
        return _containerList.contains(fourCC);
    }
}
