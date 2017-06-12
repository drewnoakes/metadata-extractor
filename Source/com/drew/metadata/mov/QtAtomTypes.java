package com.drew.metadata.mov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QtAtomTypes {
    public static String ROOT_ATOM = "root";
    public static String SAMPLE_DESCRIPTION_ATOM = "stsd";
    public static String MOVIE_HEADER_ATOM = "mvhd";
    public static String HANDLER_REFERENCE_ATOM = "hdlr";
    public static String TRACK_ATOM = "trak";
    public static String USER_DATA_ATOM = "udta";
    public static String SOUND_HANDLER_TYPE = "soun";
    public static String VIDEO_HANDLER_TYPE = "vide";
    public static String COMPRESSED_MOVIE_ATOM = "cmov";
    public static String MOVIE_ATOM = "moov";
    public static String TIME_TO_SAMPLE_ATOM = "stts";
    public static String MEDIA_HEADER_ATOM = "mdhd";
    public static String PROFILE_ATOM = "prfl";
    public static String MOVIE_CLIPPING_ATOM = "clip";
    public static String META_ATOM = "meta";

    public static final HashMap<String, String> _atomTypeMap = new HashMap<String, String>();

    private static String[] CONTAINER_TYPES_LIST = {"moov", "trak", "udta", "tref", "imap", "mdia", "minf", "stbl", "edts", "mdra", "rmra", "imag", "vnrp", "dinf", "prfl", "clip", "meta", "hdlr", "mhdr", "keys", "ilst" };
    public static List<String> CONTAINER_TYPES = new ArrayList<String>();
    static
    {
        for (int i=0; i<CONTAINER_TYPES_LIST.length; i++)
        {
            CONTAINER_TYPES.add(CONTAINER_TYPES_LIST[i]);
        }
    }
}
