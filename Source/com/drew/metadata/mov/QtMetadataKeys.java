package com.drew.metadata.mov;

import java.util.HashMap;

public class QtMetadataKeys
{
    public static final String TAG_ALBUM = "com.apple.quicktime.album";
    public static final String TAG_ARTIST = "com.apple.quicktime.artist";
    public static final String TAG_ARTWORK = "com.apple.quicktime.artwork";
    public static final String TAG_AUTHOR = "com.apple.quicktime.author";
    public static final String TAG_COMMENT = "com.apple.quicktime.comment";
    public static final String TAG_COPYRIGHT = "com.apple.quicktime.copyright";
    public static final String TAG_CREATION_DATE = "com.apple.quicktime.creationdate";
    public static final String TAG_DESCRIPTION = "com.apple.quicktime.description";
    public static final String TAG_DIRECTOR = "com.apple.quicktime.director";
    public static final String TAG_TITLE = "com.apple.quicktime.title";
    public static final String TAG_GENRE = "com.apple.quicktime.genre";
    public static final String TAG_INFORMATION = "com.apple.quicktime.information";
    public static final String TAG_KEYWORDS = "com.apple.quicktime.keywords";
    public static final String TAG_LOCATION_ISO6709 = "com.apple.quicktime.location.ISO6709";
    public static final String TAG_PRODUCER = "com.apple.quicktime.producer";
    public static final String TAG_PUBLISHER = "com.apple.quicktime.publisher";
    public static final String TAG_SOFTWARE = "com.apple.quicktime.software";
    public static final String TAG_YEAR = "com.apple.quicktime.year";
    public static final String TAG_COLLECTION_USER = "com.apple.quicktime.collection.user";
    public static final String TAG_RATING_USER = "com.apple.quicktime.rating.user";

    public static final String TAG_LOCATION_NAME = "com.apple.quicktime.location.name";
    public static final String TAG_LOCATION_BODY = "com.apple.quicktime.location.body";
    public static final String TAG_LOCATION_NOTE = "com.apple.quicktime.location.note";
    public static final String TAG_LOCATION_ROLE = "com.apple.quicktime.location.role";
    public static final String TAG_LOCATION_DATE = "com.apple.quicktime.location.date";
    public static final String TAG_DIRECTION_FACING = "com.apple.quicktime.direction.facing";
    public static final String TAG_DIRECTION_MOTION = "com.apple.quicktime.direction.motion";

    public static HashMap<String, String> _metadataKeys = new HashMap<String, String>();

    static {
        _metadataKeys.put(TAG_ALBUM, "Album");
        _metadataKeys.put(TAG_ARTIST, "Artist");
        _metadataKeys.put(TAG_ARTWORK, "Artwork");
        _metadataKeys.put(TAG_AUTHOR, "Author");
        _metadataKeys.put(TAG_COMMENT, "Comment");
        _metadataKeys.put(TAG_COPYRIGHT, "Copyright");
        _metadataKeys.put(TAG_CREATION_DATE, "Creation Date");
        _metadataKeys.put(TAG_DESCRIPTION, "Description");
        _metadataKeys.put(TAG_DIRECTOR, "Director");
        _metadataKeys.put(TAG_TITLE, "Title");
        _metadataKeys.put(TAG_GENRE, "Genre");
        _metadataKeys.put(TAG_INFORMATION, "Information");
        _metadataKeys.put(TAG_KEYWORDS, "Keywords");
        _metadataKeys.put(TAG_LOCATION_ISO6709, "ISO 6709");
        _metadataKeys.put(TAG_PRODUCER, "Producer");
        _metadataKeys.put(TAG_PUBLISHER, "Publisher");
        _metadataKeys.put(TAG_SOFTWARE, "Software");
        _metadataKeys.put(TAG_YEAR, "Year");
        _metadataKeys.put(TAG_COLLECTION_USER, "Collection User");
        _metadataKeys.put(TAG_RATING_USER, "Rating User");
        _metadataKeys.put(TAG_LOCATION_NAME, "Location Name");
        _metadataKeys.put(TAG_LOCATION_BODY, "Location Body");
        _metadataKeys.put(TAG_LOCATION_NOTE, "Location Note");
        _metadataKeys.put(TAG_LOCATION_ROLE, "Location Role");
        _metadataKeys.put(TAG_LOCATION_DATE, "Location Date");
        _metadataKeys.put(TAG_DIRECTION_FACING, "Direction Facing");
        _metadataKeys.put(TAG_DIRECTION_MOTION, "Direction Motion");
    }
}
