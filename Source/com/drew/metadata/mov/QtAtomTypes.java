package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtAtomTypes
{
    public static final String ATOM_MOVIE_HEADER = "mvhd";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_MOVIE_HEADER);
    }

    public static boolean isAccepted(String fourCC)
    {
        return _atomList.contains(fourCC);
    }
}
