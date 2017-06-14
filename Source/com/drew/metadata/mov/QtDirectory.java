package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class QtDirectory extends Directory {

    public static final int TAG_MEDIA_TIME_SCALE                  = 1;
    public static final int TAG_FRAME_RATE                        = 2;
    public static final int TAG_SCREEN_WIDTH_PX                   = 3;
    public static final int TAG_SCREEN_HEIGHT_PX                  = 4;
    public static final int TAG_VIDEO_CODEC                       = 5;
    public static final int TAG_DURATION                          = 6;
    public static final int TAG_CREATION_TIMESTAMP                = 7;
    public static final int TAG_MODIFICATION_TIMESTAMP            = 8;
    public static final int TAG_CHANNELS                          = 9;
    public static final int TAG_SAMPLE_SIZE                       = 10;
    public static final int TAG_SAMPLE_RATE                       = 11;
    public static final int TAG_AUDIO_CODEC                       = 12;

    // MovieHeader + 0x0100
    public static final int TAG_MOOV_MVHD_VERSION                 = 0x0100;
    public static final int TAG_MOOV_MVHD_CREATE_DATE             = 0x0101;
    public static final int TAG_MOOV_MVHD_MODIFY_DATE             = 0x0102;
    public static final int TAG_MOOV_MVHD_TIME_SCALE              = 0x0103;
    public static final int TAG_MOOV_MVHD_DURATION                = 0x0104;
    public static final int TAG_MOOV_MVHD_PREFERRED_RATE          = 0x0105;
    public static final int TAG_MOOV_MVHD_PREFERRED_VOLUME        = 0x0106;
    public static final int TAG_MOOV_MVHD_MATRIX_STRUCTURE        = 0x0109;
    public static final int TAG_MOOV_MVHD_PREVIEW_TIME            = 0x0112;
    public static final int TAG_MOOV_MVHD_PREVIEW_DURATION        = 0x0113;
    public static final int TAG_MOOV_MVHD_POSTER_TIME             = 0x0114;
    public static final int TAG_MOOV_MVHD_SELECTION_TIME          = 0x0115;
    public static final int TAG_MOOV_MVHD_SELECTION_DURATION      = 0x0116;
    public static final int TAG_MOOV_MVHD_CURRENT_TIME            = 0x0117;
    public static final int TAG_MOOV_MVHD_NEXT_TRACK_ID           = 0x0118;

    // Video Sample Description + 0x0200
    public static final int TAG_VIDEO_SAMPLE_VERSION              = 0x0200;
    public static final int TAG_VIDEO_SAMPLE_REVISION_LEVEL       = 0x0201;
    public static final int TAG_VIDEO_SAMPLE_VENDOR               = 0x0202;
    public static final int TAG_VIDEO_SAMPLE_TEMPORAL_QUALITY     = 0x0203;
    public static final int TAG_VIDEO_SAMPLE_SPATIAL_QUALITY      = 0x0204;
    public static final int TAG_VIDEO_SAMPLE_WIDTH                = 0x0205;
    public static final int TAG_VIDEO_SAMPLE_HEIGHT               = 0x0206;
    public static final int TAG_VIDEO_SAMPLE_HORIZONTAL_RES       = 0x0207;
    public static final int TAG_VIDEO_SAMPLE_VERICAL_RES          = 0x0208;
    public static final int TAG_VIDEO_SAMPLE_DATA_SIZE            = 0x0209;
    public static final int TAG_VIDEO_SAMPLE_FRAME_COUNT          = 0x020A;
    public static final int TAG_VIDEO_SAMPLE_COMPRESSOR_NAME      = 0x020B;
    public static final int TAG_VIDEO_SAMPLE_DEPTH                = 0x020C;
    public static final int TAG_VIDEO_SAMPLE_COLOR_TABLE_ID       = 0x020D;

    // Metadata
    public static final int TAG_QUICKTIME_ALBUM                   = "com.apple.quicktime.album".hashCode();
    public static final int TAG_QUICKTIME_ARTIST                  = "com.apple.quicktime.artist".hashCode();
    public static final int TAG_QUICKTIME_ARTWORK                 = "com.apple.quicktime.artwork".hashCode();
    public static final int TAG_QUICKTIME_AUTHOR                  = "com.apple.quicktime.author".hashCode();
    public static final int TAG_QUICKTIME_COMMENT                 = "com.apple.quicktime.comment".hashCode();
    public static final int TAG_QUICKTIME_COPYRIGHT               = "com.apple.quicktime.copyright".hashCode();
    public static final int TAG_QUICKTIME_CREATIONDATE            = "com.apple.quicktime.creationdate".hashCode();
    public static final int TAG_QUICKTIME_DESCRIPTION             = "com.apple.quicktime.description".hashCode();
    public static final int TAG_QUICKTIME_DISPLAYNAME             = "com.apple.quicktime.displayname".hashCode();
    public static final int TAG_QUICKTIME_DIRECTOR                = "com.apple.quicktime.director".hashCode();
    public static final int TAG_QUICKTIME_TITLE                   = "com.apple.quicktime.title".hashCode();
    public static final int TAG_QUICKTIME_GENRE                   = "com.apple.quicktime.genre".hashCode();
    public static final int TAG_QUICKTIME_INFORMATION             = "com.apple.quicktime.information".hashCode();
    public static final int TAG_QUICKTIME_KEYWORDS                = "com.apple.quicktime.keywords".hashCode();
    public static final int TAG_QUICKTIME_LOCATION                = "com.apple.quicktime.location".hashCode();
    public static final int TAG_QUICKTIME_PRODUCER                = "com.apple.quicktime.producer".hashCode();
    public static final int TAG_QUICKTIME_PUBLISHER               = "com.apple.quicktime.publisher".hashCode();
    public static final int TAG_QUICKTIME_SOFTWARE                = "com.apple.quicktime.software".hashCode();
    public static final int TAG_QUICKTIME_YEAR                    = "com.apple.quicktime.year".hashCode();
    public static final int TAG_QUICKTIME_COLLECTION              = "com.apple.quicktime.collection.user".hashCode();
    public static final int TAG_QUICKTIME_RATING                  = "com.apple.quicktime.rating.user".hashCode();

    public static final int TAG_QUICKTIME_LOCATION_NAME           = "com.apple.quicktime.location.name".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_BODY           = "com.apple.quicktime.location.name".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_NOTE           = "com.apple.quicktime.location.note".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_ROLE           = "com.apple.quicktime.location.role".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_DATE           = "com.apple.quicktime.location.date".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_FACING         = "com.apple.quicktime.direction.facing".hashCode();
    public static final int TAG_QUICKTIME_LOCATION_MOTION         = "com.apple.quicktime.direction.motion".hashCode();

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_QUICKTIME_DISPLAYNAME, "Display Name");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_NAME, "Location Name");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_BODY, "Location Body");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_NOTE, "Location Note");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_ROLE, "Location Role");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_DATE, "Location Date");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_FACING, "Location Facing");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION_MOTION, "Location Motion");
        _tagNameMap.put(TAG_QUICKTIME_ALBUM, "Album");
        _tagNameMap.put(TAG_QUICKTIME_ARTIST, "Artist");
        _tagNameMap.put(TAG_QUICKTIME_ARTWORK, "Artwork");
        _tagNameMap.put(TAG_QUICKTIME_AUTHOR, "Author");
        _tagNameMap.put(TAG_QUICKTIME_COMMENT, "Comment");
        _tagNameMap.put(TAG_QUICKTIME_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_QUICKTIME_CREATIONDATE, "Creation Date");
        _tagNameMap.put(TAG_QUICKTIME_DESCRIPTION, "Description");
        _tagNameMap.put(TAG_QUICKTIME_DIRECTOR, "Director");
        _tagNameMap.put(TAG_QUICKTIME_TITLE, "Title");
        _tagNameMap.put(TAG_QUICKTIME_GENRE, "Genre");
        _tagNameMap.put(TAG_QUICKTIME_INFORMATION, "Information");
        _tagNameMap.put(TAG_QUICKTIME_KEYWORDS, "Keywords");
        _tagNameMap.put(TAG_QUICKTIME_LOCATION, "Location");
        _tagNameMap.put(TAG_QUICKTIME_PRODUCER, "Producer");
        _tagNameMap.put(TAG_QUICKTIME_PUBLISHER, "Publisher");
        _tagNameMap.put(TAG_QUICKTIME_SOFTWARE, "Software");
        _tagNameMap.put(TAG_QUICKTIME_YEAR, "Year");
        _tagNameMap.put(TAG_QUICKTIME_COLLECTION, "Collection");
        _tagNameMap.put(TAG_QUICKTIME_RATING, "Rating");
        _tagNameMap.put(TAG_MEDIA_TIME_SCALE, "Media Time Scale");
        _tagNameMap.put(TAG_FRAME_RATE, "Frame Rate");
        _tagNameMap.put(TAG_SCREEN_WIDTH_PX, "Frame Width (px)");
        _tagNameMap.put(TAG_SCREEN_HEIGHT_PX, "Frame Height (px)");
        _tagNameMap.put(TAG_VIDEO_CODEC, "Video Codec");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_CREATION_TIMESTAMP, "Creation Timestamp");
        _tagNameMap.put(TAG_MODIFICATION_TIMESTAMP, "Modification Timestamp");
        _tagNameMap.put(TAG_CHANNELS, "Channels");
        _tagNameMap.put(TAG_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_SAMPLE_RATE, "Sample Rate");
        _tagNameMap.put(TAG_AUDIO_CODEC, "Audio Codec");

        // MovieHeader + 0x0100
        _tagNameMap.put(TAG_MOOV_MVHD_VERSION, "Version");
        _tagNameMap.put(TAG_MOOV_MVHD_CREATE_DATE, "Create Date");
        _tagNameMap.put(TAG_MOOV_MVHD_MODIFY_DATE, "Modify Date");
        _tagNameMap.put(TAG_MOOV_MVHD_TIME_SCALE, "Time Scale");
        _tagNameMap.put(TAG_MOOV_MVHD_DURATION, "Preview Duration");
        _tagNameMap.put(TAG_MOOV_MVHD_PREFERRED_RATE, "Preferred Rate");
        _tagNameMap.put(TAG_MOOV_MVHD_PREFERRED_VOLUME, "Preferred Volume");
        _tagNameMap.put(TAG_MOOV_MVHD_MATRIX_STRUCTURE, "Matrix Structure");
        _tagNameMap.put(TAG_MOOV_MVHD_PREVIEW_TIME, "Preview Time");
        _tagNameMap.put(TAG_MOOV_MVHD_PREVIEW_DURATION, "Preview Duration");
        _tagNameMap.put(TAG_MOOV_MVHD_POSTER_TIME, "Poster Time");
        _tagNameMap.put(TAG_MOOV_MVHD_SELECTION_TIME, "Selection Time");
        _tagNameMap.put(TAG_MOOV_MVHD_SELECTION_DURATION, "Selection Duration");
        _tagNameMap.put(TAG_MOOV_MVHD_CURRENT_TIME, "Current Time");
        _tagNameMap.put(TAG_MOOV_MVHD_NEXT_TRACK_ID, "Next Track ID");

        // Video Sample Description + 0x0200
        _tagNameMap.put(TAG_VIDEO_SAMPLE_VERSION, "Version");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_REVISION_LEVEL, "Revision Level");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_VENDOR, "Vendor");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_TEMPORAL_QUALITY, "Temporal Quality");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_SPATIAL_QUALITY, "Spatial Quality");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_WIDTH, "Width");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_HEIGHT, "Height");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_HORIZONTAL_RES, "Horizontal Resolution");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_VERICAL_RES, "Vertical Resolution");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_DATA_SIZE, "Data Size");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_FRAME_COUNT, "Frame Count");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_COMPRESSOR_NAME, "Compressor Name");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_DEPTH, "Depth");
        _tagNameMap.put(TAG_VIDEO_SAMPLE_COLOR_TABLE_ID, "Color Table ID");

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
