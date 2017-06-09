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
    public static final String[] ATOM_UDTA_TYPES = new String[]{"©arg", "©ark", "©cok", "©com", "©cpy", "©day", "©dir",
        "©ed1' to '©ed9", "©fmt", "©inf", "©isr", "©lab", "©lal", "©mak", "©mal", "©nak", "©nam", "©pdk", "©phg",
        "©prd", "©prf", "©prk", "©prl", "©req", "©snk", "©snm", "©src", "©swf", "©swk", "©swr", "©wrt", "AllF",
        "hinf", "hnti", "name", "tnam", "tagc", "LOOP", "ptv ", "SelO", "WLOC", "desc"};

    public static String ATOM_META_KEYS = "keys";
    public static String ATOM_META_HEADER = "mhdr";
    public static String ATOM_META_LIST = "ilst";
    public static final String ATOM_UDTA_ARG = "©arg";
    public static final String ATOM_UDTA_ARK = "©ark";
    public static final String ATOM_UDTA_COK = "©cok";
    public static final String ATOM_UDTA_COM = "©com";
    public static final String ATOM_UDTA_CPY = "©cpy";
    public static final String ATOM_UDTA_DAY = "©day";
    public static final String ATOM_UDTA_DIR = "©dir";
    public static final String ATOM_UDTA_ED1 = "©ed1";
    public static final String ATOM_UDTA_ED2 = "©ed2";
    public static final String ATOM_UDTA_ED3 = "©ed3";
    public static final String ATOM_UDTA_ED4 = "©ed4";
    public static final String ATOM_UDTA_ED5 = "©ed5";
    public static final String ATOM_UDTA_ED6 = "©ed6";
    public static final String ATOM_UDTA_ED7 = "©ed7";
    public static final String ATOM_UDTA_ED8 = "©ed8";
    public static final String ATOM_UDTA_ED9 = "©ed9";
    public static final String ATOM_UDTA_FMT = "©fmt";
    public static final String ATOM_UDTA_INF = "©inf";
    public static final String ATOM_UDTA_ISR = "©isr";
    public static final String ATOM_UDTA_LAB = "©lab";
    public static final String ATOM_UDTA_LAL = "©lal";
    public static final String ATOM_UDTA_MAK = "©mak";
    public static final String ATOM_UDTA_MAL = "©mal";
    public static final String ATOM_UDTA_NAK = "©nak";
    public static final String ATOM_UDTA_NAM = "©nam";
    public static final String ATOM_UDTA_PDK = "©pdk";
    public static final String ATOM_UDTA_PHG = "©phg";
    public static final String ATOM_UDTA_PRD = "©prd";
    public static final String ATOM_UDTA_PRF = "©prf";
    public static final String ATOM_UDTA_PRK = "©prk";
    public static final String ATOM_UDTA_PRL = "©prl";
    public static final String ATOM_UDTA_REQ = "©req";
    public static final String ATOM_UDTA_SNK = "©snk";
    public static final String ATOM_UDTA_SNM = "©snm";
    public static final String ATOM_UDTA_SRC = "©src";
    public static final String ATOM_UDTA_SWF = "©swf";
    public static final String ATOM_UDTA_SWK = "©swk";
    public static final String ATOM_UDTA_SWR = "©swr";
    public static final String ATOM_UDTA_WRT = "©wrt";
    public static final String ATOM_UDTA_ALLF = "AllF";
    public static final String ATOM_UDTA_HINF = "hinf";
    public static final String ATOM_UDTA_HNTI = "hnti";
    public static final String ATOM_UDTA_NAME = "name";
    public static final String ATOM_UDTA_TNAM = "tnam";
    public static final String ATOM_UDTA_TAGC = "tagc";
    public static final String ATOM_UDTA_LOOP = "LOOP";
    public static final String ATOM_UDTA_PTV  = "ptv ";
    public static final String ATOM_UDTA_SELO = "SelO";
    public static final String ATOM_UDTA_WLOC = "WLOC";

    public static final HashMap<String, String> _atomTypeMap = new HashMap<String, String>();

    static {
        _atomTypeMap.put(ATOM_UDTA_ARG, "Name of arranger");
        _atomTypeMap.put(ATOM_UDTA_ARK, "Keywords for arranger");
        _atomTypeMap.put(ATOM_UDTA_COK, "Keywords for composer");
        _atomTypeMap.put(ATOM_UDTA_COM, "Name of composer");
        _atomTypeMap.put(ATOM_UDTA_CPY, "Copyright statement");
        _atomTypeMap.put(ATOM_UDTA_DAY, "Date the movie content was created");
        _atomTypeMap.put(ATOM_UDTA_DIR, "Name of movie’s director");
        _atomTypeMap.put(ATOM_UDTA_ED1, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED2, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED3, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED4, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED5, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED6, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED7, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED8, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_ED9, "Edit dates and descriptions");
        _atomTypeMap.put(ATOM_UDTA_FMT, "Indication of movie format (computer-generated, digitized, and so on)");
        _atomTypeMap.put(ATOM_UDTA_INF, "Information about the movie");
        _atomTypeMap.put(ATOM_UDTA_ISR, "ISRC code");
        _atomTypeMap.put(ATOM_UDTA_LAB, "Name of record label");
        _atomTypeMap.put(ATOM_UDTA_LAL, "URL of record label");
        _atomTypeMap.put(ATOM_UDTA_MAK, "Name of file creator or maker");
        _atomTypeMap.put(ATOM_UDTA_MAL, "URL of file creator or maker");
        _atomTypeMap.put(ATOM_UDTA_NAK, "Title keywords of the content");
        _atomTypeMap.put(ATOM_UDTA_NAM, "Title of the content");
        _atomTypeMap.put(ATOM_UDTA_PDK, "Keywords for producer");
        _atomTypeMap.put(ATOM_UDTA_PHG, "Recording copyright statement, normally preceded by the symbol");
        _atomTypeMap.put(ATOM_UDTA_PRD, "Name of producer");
        _atomTypeMap.put(ATOM_UDTA_PRF, "Names of performers");
        _atomTypeMap.put(ATOM_UDTA_PRK, "Keywords of main artist and performer");
        _atomTypeMap.put(ATOM_UDTA_PRL, "URL of main artist and performer");
        _atomTypeMap.put(ATOM_UDTA_REQ, "Special hardware and software requirements");
        _atomTypeMap.put(ATOM_UDTA_SNK, "Subtitle keywords of the content");
        _atomTypeMap.put(ATOM_UDTA_SNM, "Subtitle of content");
        _atomTypeMap.put(ATOM_UDTA_SRC, "Credits for those who provided movie source content");
        _atomTypeMap.put(ATOM_UDTA_SWF, "Name of songwriter");
        _atomTypeMap.put(ATOM_UDTA_SWK, "Keywords for songwriter");
        _atomTypeMap.put(ATOM_UDTA_SWR, "Name and version number of the software (or hardware) that generated this movie");
        _atomTypeMap.put(ATOM_UDTA_WRT, "Name of movie’s writer");
        _atomTypeMap.put(ATOM_UDTA_ALLF, "Play all frames—byte indicating that all frames of video should be played, regardless of timing");
        _atomTypeMap.put(ATOM_UDTA_HINF, "Hint track information—statistical data for real-time streaming of a particular track. For more information, see Hint Track User Data Atom.");
        _atomTypeMap.put(ATOM_UDTA_HNTI, "Hint info atom—data used for real-time streaming of a movie or a track. For more information, see Movie Hint Info Atom and Hint Track User Data Atom.");
        _atomTypeMap.put(ATOM_UDTA_NAME, "Name of object");
        _atomTypeMap.put(ATOM_UDTA_TNAM, "Localized track name optionally present in Track user data. The payload is described in Track Name.");
        _atomTypeMap.put(ATOM_UDTA_TAGC, "Media characteristic optionally present in Track user data—specialized text that describes something of interest about the track. For more information, see Media Characteristic Tags.");
        _atomTypeMap.put(ATOM_UDTA_LOOP, "Long integer indicating looping style. This atom is not present unless the movie is set to loop. Values are 0 for normal looping, 1 for palindromic looping.");
        _atomTypeMap.put(ATOM_UDTA_PTV , "Print to video—display movie in full screen mode. This atom contains a 16-byte structure, described in Print to Video (Full Screen Mode).");
        _atomTypeMap.put(ATOM_UDTA_SELO, "Play selection only—byte indicating that only the selected area of the movie should be played");
        _atomTypeMap.put(ATOM_UDTA_WLOC, "Default window location for movie—two 16-bit values, {x,y}");
    }

    private static String[] CONTAINER_TYPES_LIST = {"moov", "trak", "udta", "tref", "imap", "mdia", "minf", "stbl", "edts", "mdra", "rmra", "imag", "vnrp", "dinf", "prfl", "clip", "©arg", "©ark", "©cok", "©com", "©cpy", "©day", "©dir",
        "©ed1' to '©ed9", "©fmt", "©inf", "©isr", "©lab", "©lal", "©mak", "©mal", "©nak", "©nam", "©pdk", "©phg",
        "©prd", "©prf", "©prk", "©prl", "©req", "©snk", "©snm", "©src", "©swf", "©swk", "©swr", "©wrt", "AllF",
        "hinf", "hnti", "name", "tnam", "tagc", "LOOP", "ptv ", "SelO", "WLOC", "meta", "hdlr", "mhdr", "keys", "ilst" };
    public static List<String> CONTAINER_TYPES = new ArrayList<String>();
    static
    {
        for (int i=0; i<CONTAINER_TYPES_LIST.length; i++)
        {
            CONTAINER_TYPES.add(CONTAINER_TYPES_LIST[i]);
        }
    }
}
