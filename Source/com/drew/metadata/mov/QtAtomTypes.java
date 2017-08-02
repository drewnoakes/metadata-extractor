package com.drew.metadata.mov;

import com.drew.imaging.quicktime.AtomTypesSample;

/**
 * @author Payton Garland
 */
public class QtAtomTypes extends AtomTypesSample
{
    public static final String ATOM_FILE_TYPE                = "ftyp";
    public static final String ATOM_MOVIE_HEADER             = "mvhd";
    public static final String ATOM_VIDEO_MEDIA_INFO         = "vmhd";
    public static final String ATOM_SOUND_MEDIA_INFO         = "smhd";
    public static final String ATOM_BASE_MEDIA_INFO          = "gmhd";
    public static final String ATOM_TIMECODE_MEDIA_INFO      = "tcmi";
    public static final String ATOM_HANDLER                  = "hdlr";
    public static final String ATOM_KEYS                     = "keys";
    public static final String ATOM_DATA                     = "data";
    public static final String ATOM_SAMPLE_DESCRIPTION       = "stsd";
    public static final String ATOM_TIME_TO_SAMPLE           = "stts";
    public static final String ATOM_MEDIA_HEADER             = "mdhd";

    static {
        _atomList.add(ATOM_FILE_TYPE);
        _atomList.add(ATOM_MOVIE_HEADER);
        _atomList.add(ATOM_VIDEO_MEDIA_INFO);
        _atomList.add(ATOM_SOUND_MEDIA_INFO);
        _atomList.add(ATOM_BASE_MEDIA_INFO);
        _atomList.add(ATOM_TIMECODE_MEDIA_INFO);
        _atomList.add(ATOM_HANDLER);
        _atomList.add(ATOM_KEYS);
        _atomList.add(ATOM_DATA);
        _atomList.add(ATOM_SAMPLE_DESCRIPTION);
        _atomList.add(ATOM_TIME_TO_SAMPLE);
        _atomList.add(ATOM_MEDIA_HEADER);
    }
}
