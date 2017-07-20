package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class QtDirectory extends Directory {

    // Movie Header Atom (https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-56313)
    public static final int TAG_CREATION_TIME = 0x0100;
    public static final int TAG_MODIFICATION_TIME = 0x0101;
    public static final int TAG_TIME_SCALE = 0x0102;
    public static final int TAG_DURATION = 0x0103;
    public static final int TAG_PREFERRED_RATE = 0x0104;
    public static final int TAG_PREFERRED_VOLUME = 0x0105;
    public static final int TAG_PREVIEW_TIME = 0x0108;
    public static final int TAG_PREVIEW_DURATION = 0x0109;
    public static final int TAG_POSTER_TIME = 0x010A;
    public static final int TAG_SELECTION_TIME = 0x010B;
    public static final int TAG_SELECTION_DURATION = 0x010C;
    public static final int TAG_CURRENT_TIME = 0x010D;
    public static final int TAG_NEXT_TRACK_ID = 0x010E;

    // Video Sample Description Atom
    public static final int TAG_VENDOR = 0X0201;
    public static final int TAG_TEMPORAL_QUALITY = 0x0202;
    public static final int TAG_SPATIAL_QUALITY = 0x0203;
    public static final int TAG_WIDTH = 0x0204;
    public static final int TAG_HEIGHT = 0x0205;
    public static final int TAG_HORIZONTAL_RESOLUTION = 0x0206;
    public static final int TAG_VERTICAL_RESOLUTION = 0x0207;
    public static final int TAG_COMPRESSOR_NAME = 0x0208;
    public static final int TAG_DEPTH = 0x0209;
    public static final int TAG_COMPRESSION_TYPE = 0x020A;

    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT = 0x0301;
    public static final int TAG_NUMBER_OF_CHANNELS = 0x0302;
    public static final int TAG_SAMPLE_SIZE = 0x0303;
    public static final int TAG_SAMPLE_RATE = 0x0304;

    public static final int TAG_SOUND_BALANCE = 0x0305;

    // User Data Types Holder (0x0400 - 0x04FF)
    public static int USER_DATA_TYPES_POS = 0x0400;

    // User Metadata Types Holder (0x0500 - 0x05FF)
    public static int METADATA_KEYS_POS = 0x0500;

    // Video Media Information Header Atom
    public static final int TAG_GRAPHICS_MODE = 0x0311;
    public static final int TAG_OPCOLOR = 0x0312;

    public static final int TAG_MAJOR_BRAND = 0x0001;
    public static final int TAG_MINOR_VERSION = 0x0002;
    public static final int TAG_COMPATIBLE_BRANDS = 0x0003;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    @NotNull
    protected static final HashMap<String, Integer> _tagIntegerMap = new HashMap<String, Integer>();

    static {
        _tagNameMap.put(TAG_MAJOR_BRAND, "Major Brand");
        _tagNameMap.put(TAG_MINOR_VERSION, "Minor Version");
        _tagNameMap.put(TAG_COMPATIBLE_BRANDS, "Compatible Brands");

        _tagNameMap.put(TAG_CREATION_TIME, "Creation Time");
        _tagNameMap.put(TAG_MODIFICATION_TIME, "Modification Time");
        _tagNameMap.put(TAG_TIME_SCALE, "Time Scale");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_PREFERRED_RATE, "Preferred Rate");
        _tagNameMap.put(TAG_PREFERRED_VOLUME, "Preferred Volume");
        _tagNameMap.put(TAG_PREVIEW_TIME, "Preview Time");
        _tagNameMap.put(TAG_PREVIEW_DURATION, "Preview Duration");
        _tagNameMap.put(TAG_POSTER_TIME, "Poster Time");
        _tagNameMap.put(TAG_SELECTION_TIME, "Selection Time");
        _tagNameMap.put(TAG_SELECTION_DURATION, "Selection Duration");
        _tagNameMap.put(TAG_CURRENT_TIME, "Current Time");
        _tagNameMap.put(TAG_NEXT_TRACK_ID, "Next Track ID");

        _tagNameMap.put(TAG_VENDOR, "Vendor");
        _tagNameMap.put(TAG_TEMPORAL_QUALITY, "Temporal Quality");
        _tagNameMap.put(TAG_SPATIAL_QUALITY, "Spatial Quality");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_HORIZONTAL_RESOLUTION, "Horizontal Resolution");
        _tagNameMap.put(TAG_VERTICAL_RESOLUTION, "Vertical Resolution");
        _tagNameMap.put(TAG_COMPRESSOR_NAME, "Compressor Name");
        _tagNameMap.put(TAG_DEPTH, "Depth");
        _tagNameMap.put(TAG_COMPRESSION_TYPE, "Compression Type");

        _tagNameMap.put(TAG_AUDIO_FORMAT, "Audio Format");
        _tagNameMap.put(TAG_NUMBER_OF_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_SAMPLE_RATE, "Sample Rate");

        _tagNameMap.put(TAG_SOUND_BALANCE, "Sound Balance");

        _tagNameMap.put(TAG_GRAPHICS_MODE, "Graphics Mode");
        _tagNameMap.put(TAG_OPCOLOR, "Opcolor");

        _tagIntegerMap.put("com.apple.quicktime.album", 0x0500);
        _tagIntegerMap.put("com.apple.quicktime.artist", 0x0501);
        _tagIntegerMap.put("com.apple.quicktime.artwork", 0x0502);
        _tagIntegerMap.put("com.apple.quicktime.author", 0x0503);
        _tagIntegerMap.put("com.apple.quicktime.comment", 0x0504);
        _tagIntegerMap.put("com.apple.quicktime.copyright", 0x0505);
        _tagIntegerMap.put("com.apple.quicktime.creationdate", 0x0506);
        _tagIntegerMap.put("com.apple.quicktime.description", 0x0507);
        _tagIntegerMap.put("com.apple.quicktime.director", 0x0508);
        _tagIntegerMap.put("com.apple.quicktime.title", 0x0509);
        _tagIntegerMap.put("com.apple.quicktime.genre", 0x050A);
        _tagIntegerMap.put("com.apple.quicktime.information", 0x050B);
        _tagIntegerMap.put("com.apple.quicktime.keywords", 0x050C);
        _tagIntegerMap.put("com.apple.quicktime.location.ISO6709", 0x050D);
        _tagIntegerMap.put("com.apple.quicktime.producer", 0x050E);
        _tagIntegerMap.put("com.apple.quicktime.publisher", 0x050F);
        _tagIntegerMap.put("com.apple.quicktime.software", 0x0510);
        _tagIntegerMap.put("com.apple.quicktime.year", 0x0511);
        _tagIntegerMap.put("com.apple.quicktime.collection.user", 0x0512);
        _tagIntegerMap.put("com.apple.quicktime.rating.user", 0x0513);
        _tagIntegerMap.put("com.apple.quicktime.location.name", 0x0514);
        _tagIntegerMap.put("com.apple.quicktime.location.body", 0x0515);
        _tagIntegerMap.put("com.apple.quicktime.location.note", 0x0516);
        _tagIntegerMap.put("com.apple.quicktime.location.role", 0x0517);
        _tagIntegerMap.put("com.apple.quicktime.location.date", 0x0518);
        _tagIntegerMap.put("com.apple.quicktime.direction.facing", 0x0519);
        _tagIntegerMap.put("com.apple.quicktime.direction.motion", 0x051A);
        _tagIntegerMap.put("com.apple.quicktime.displayname", 0x051B);

        _tagIntegerMap.put("©arg", 0x0401);
        _tagIntegerMap.put("©ark", 0x0402);
        _tagIntegerMap.put("©cok", 0x0403);
        _tagIntegerMap.put("©com", 0x0404);
        _tagIntegerMap.put("©cpy", 0x0405);
        _tagIntegerMap.put("©day", 0x0406);
        _tagIntegerMap.put("©dir", 0x0407);
        _tagIntegerMap.put("©ed1", 0x0408);
        _tagIntegerMap.put("©ed2", 0x0409);
        _tagIntegerMap.put("©ed3", 0x040A);
        _tagIntegerMap.put("©ed4", 0x040B);
        _tagIntegerMap.put("©ed5", 0x040C);
        _tagIntegerMap.put("©ed6", 0x040D);
        _tagIntegerMap.put("©ed7", 0x040E);
        _tagIntegerMap.put("©ed8", 0x040F);
        _tagIntegerMap.put("©ed9", 0x0410);
        _tagIntegerMap.put("©fmt", 0x0411);
        _tagIntegerMap.put("©inf", 0x0412);
        _tagIntegerMap.put("©isr", 0x0413);
        _tagIntegerMap.put("©lab", 0x0414);
        _tagIntegerMap.put("©lal", 0x0415);
        _tagIntegerMap.put("©mak", 0x0416);
        _tagIntegerMap.put("©mal", 0x0417);
        _tagIntegerMap.put("©nak", 0x0418);
        _tagIntegerMap.put("©nam", 0x0419);
        _tagIntegerMap.put("©pdk", 0x041A);
        _tagIntegerMap.put("©phg", 0x041B);
        _tagIntegerMap.put("©prd", 0x041C);
        _tagIntegerMap.put("©prf", 0x041D);
        _tagIntegerMap.put("©prk", 0x041E);
        _tagIntegerMap.put("©prl", 0x041F);
        _tagIntegerMap.put("©req", 0x0420);
        _tagIntegerMap.put("©snk", 0x0421);
        _tagIntegerMap.put("©snm", 0x0422);
        _tagIntegerMap.put("©src", 0x0423);
        _tagIntegerMap.put("©swf", 0x0424);
        _tagIntegerMap.put("©swk", 0x0425);
        _tagIntegerMap.put("©swr", 0x0426);
        _tagIntegerMap.put("©wrt", 0x0427);
        _tagIntegerMap.put("AllF", 0x0428);
        _tagIntegerMap.put("desc", 0x0429);

        _tagNameMap.put(0x0401, "Arranger Name");
        _tagNameMap.put(0x0402, "Arranger Keywords");
        _tagNameMap.put(0x0403, "Composer Keywords");
        _tagNameMap.put(0x0404, "Composer Name");
        _tagNameMap.put(0x0405, "Copyright");
        _tagNameMap.put(0x0406, "Day Created");
        _tagNameMap.put(0x0407, "Director Name");
        _tagNameMap.put(0x0408, "Edit Date 1");
        _tagNameMap.put(0x0409, "Edit Date 2");
        _tagNameMap.put(0x040A, "Edit Date 3");
        _tagNameMap.put(0x040B, "Edit Date 4");
        _tagNameMap.put(0x040C, "Edit Date 5");
        _tagNameMap.put(0x040D, "Edit Date 6");
        _tagNameMap.put(0x040E, "Edit Date 7");
        _tagNameMap.put(0x040F, "Edit Date 8");
        _tagNameMap.put(0x0410, "Edit Date 9");
        _tagNameMap.put(0x0411, "Movie Format");
        _tagNameMap.put(0x0412, "Movie Information");
        _tagNameMap.put(0x0413, "ISRC");
        _tagNameMap.put(0x0414, "Record Label Name");
        _tagNameMap.put(0x0415, "Record Label URL");
        _tagNameMap.put(0x0416, "Creator Name");
        _tagNameMap.put(0x0417, "Creator URL");
        _tagNameMap.put(0x0418, "Title Keywords");
        _tagNameMap.put(0x0419, "Title Name");
        _tagNameMap.put(0x041A, "Producer Keywords");
        _tagNameMap.put(0x041B, "Recording Copyright Statement");
        _tagNameMap.put(0x041C, "Producer Name");
        _tagNameMap.put(0x041D, "Performer Names");
        _tagNameMap.put(0x041E, "Main Artist/Performer Keywords");
        _tagNameMap.put(0x041F, "Main Artist/Performer URL");
        _tagNameMap.put(0x0420, "Special Hardware/Software Requirements");
        _tagNameMap.put(0x0421, "Subtitle Keywords");
        _tagNameMap.put(0x0422, "Subtitle");
        _tagNameMap.put(0x0423, "Credits");
        _tagNameMap.put(0x0424, "Songwriter Name");
        _tagNameMap.put(0x0425, "Songwriter Keywords");
        _tagNameMap.put(0x0426, "Software");
        _tagNameMap.put(0x0427, "Writer Name");
        _tagNameMap.put(0x0428, "Play All Frames");
        _tagNameMap.put(0x0429, "Description");

        _tagNameMap.put(0x0500, "Album");
        _tagNameMap.put(0x0501, "Artist");
        _tagNameMap.put(0x0502, "Artwork");
        _tagNameMap.put(0x0503, "Author");
        _tagNameMap.put(0x0504, "Comment");
        _tagNameMap.put(0x0505, "Copyright");
        _tagNameMap.put(0x0506, "Creation Date");
        _tagNameMap.put(0x0507, "Description");
        _tagNameMap.put(0x0508, "Director");
        _tagNameMap.put(0x0509, "Title");
        _tagNameMap.put(0x050A, "Genre");
        _tagNameMap.put(0x050B, "Information");
        _tagNameMap.put(0x050C, "Keywords");
        _tagNameMap.put(0x050D, "ISO 6709");
        _tagNameMap.put(0x050E, "Producer");
        _tagNameMap.put(0x050F, "Publisher");
        _tagNameMap.put(0x0510, "Software");
        _tagNameMap.put(0x0511, "Year");
        _tagNameMap.put(0x0512, "Collection User");
        _tagNameMap.put(0x0513, "Rating User");
        _tagNameMap.put(0x0514, "Location Name");
        _tagNameMap.put(0x0515, "Location Body");
        _tagNameMap.put(0x0516, "Location Note");
        _tagNameMap.put(0x0517, "Location Role");
        _tagNameMap.put(0x0518, "Location Date");
        _tagNameMap.put(0x0519, "Direction Facing");
        _tagNameMap.put(0x051A, "Direction Motion");
        _tagNameMap.put(0x051B, "Display Name");
    }

    public QtDirectory()
    {
        this.setDescriptor(new QtDescriptor(this));
    }

    @Override
    @NotNull
    public String getName() { return "Quicktime"; }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
