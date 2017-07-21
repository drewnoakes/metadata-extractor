package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtAtomTypes
{
    // Main file identifier
    public static final String ATOM_FILE_TYPE = "ftyp";

    // Specifies characteristics of the whole movie
    public static final String ATOM_MOVIE_HEADER = "mvhd";

    // Media information atoms
    public static final String ATOM_VIDEO_MEDIA_INFO = "vmhd";
    public static final String ATOM_SOUND_MEDIA_INFO = "smhd";
    public static final String ATOM_TIMECODE_MEDIA_INFO = "tcmi";

    // Generic atoms used across file
    public static final String ATOM_HANDLER = "hdlr";
    public static final String ATOM_KEYS = "keys";
    public static final String ATOM_DATA = "data";
    public static final String ATOM_SAMPLE_DESCRIPTION = "stsd";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_FILE_TYPE);

        _atomList.add(ATOM_MOVIE_HEADER);

        _atomList.add(ATOM_VIDEO_MEDIA_INFO);
        _atomList.add(ATOM_SOUND_MEDIA_INFO);
        _atomList.add(ATOM_TIMECODE_MEDIA_INFO);

        _atomList.add(ATOM_HANDLER);
        _atomList.add(ATOM_KEYS);
        _atomList.add(ATOM_DATA);
        _atomList.add(ATOM_SAMPLE_DESCRIPTION);
    }
}
