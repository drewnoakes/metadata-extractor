package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.AtomTypesSample;

/**
 * @author Payton Garland
 */
public class Mp4BoxTypes extends AtomTypesSample
{
    public static final String BOX_FILE_TYPE                        = "ftyp";
    public static final String BOX_MOVIE_HEADER                     = "mvhd";
    public static final String BOX_VIDEO_MEDIA_INFO                 = "vmhd";
    public static final String BOX_SOUND_MEDIA_INFO                 = "smhd";
    public static final String BOX_HINT_MEDIA_INFO                  = "hmhd";
    public static final String BOX_NULL_MEDIA_INFO                  = "nmhd";
    public static final String BOX_HANDLER                          = "hdlr";
    public static final String BOX_SAMPLE_DESCRIPTION               = "stsd";
    public static final String BOX_TIME_TO_SAMPLE                   = "stts";
    public static final String BOX_MEDIA_HEADER                     = "mdhd";

    static {
        _atomList.add(BOX_FILE_TYPE);
        _atomList.add(BOX_MOVIE_HEADER);
        _atomList.add(BOX_VIDEO_MEDIA_INFO);
        _atomList.add(BOX_SOUND_MEDIA_INFO);
        _atomList.add(BOX_HINT_MEDIA_INFO);
        _atomList.add(BOX_NULL_MEDIA_INFO);
        _atomList.add(BOX_HANDLER);
        _atomList.add(BOX_SAMPLE_DESCRIPTION);
        _atomList.add(BOX_TIME_TO_SAMPLE);
        _atomList.add(BOX_MEDIA_HEADER);
    }
}
