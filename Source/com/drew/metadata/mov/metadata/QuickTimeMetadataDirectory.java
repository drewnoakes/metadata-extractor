/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.mov.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QuickTimeDirectory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class QuickTimeMetadataDirectory extends QuickTimeDirectory
{
    // User Data Types Holder (0x0400 - 0x04FF)
    // https://sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html#Meta

    // User Metadata Types Holder (0x0500 - 0x05FF)
    // https://developer.apple.com/library/content/documentation/QuickTime/QTFF/Metadata/Metadata.html#//apple_ref/doc/uid/TP40000939-CH1-SW43
    // https://sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html#Meta
    public static final int TAG_ALBUM                   = 0x0500;
    public static final int TAG_ARTIST                  = 0x0501;
    public static final int TAG_ARTWORK                 = 0x0502;
    public static final int TAG_AUTHOR                  = 0x0503;
    public static final int TAG_COMMENT                 = 0x0504;
    public static final int TAG_COPYRIGHT               = 0x0505;
    public static final int TAG_CREATION_DATE           = 0x0506;
    public static final int TAG_DESCRIPTION             = 0x0507;
    public static final int TAG_DIRECTOR                = 0x0508;
    public static final int TAG_TITLE                   = 0x0509;
    public static final int TAG_GENRE                   = 0x050A;
    public static final int TAG_INFORMATION             = 0x050B;
    public static final int TAG_KEYWORDS                = 0x050C;
    public static final int TAG_LOCATION_ISO6709        = 0x050D;
    public static final int TAG_PRODUCER                = 0x050E;
    public static final int TAG_PUBLISHER               = 0x050F;
    public static final int TAG_SOFTWARE                = 0x0510;
    public static final int TAG_YEAR                    = 0x0511;
    public static final int TAG_COLLECTION_USER         = 0x0512;
    public static final int TAG_RATING_USER             = 0x0513;
    public static final int TAG_LOCATION_NAME           = 0x0514;
    public static final int TAG_LOCATION_BODY           = 0x0515;
    public static final int TAG_LOCATION_NOTE           = 0x0516;
    public static final int TAG_LOCATION_ROLE           = 0x0517;
    public static final int TAG_LOCATION_DATE           = 0x0518;
    public static final int TAG_DIRECTION_FACING        = 0x0519;
    public static final int TAG_DIRECTION_MOTION        = 0x051A;
    public static final int TAG_DISPLAY_NAME            = 0x051B;
    public static final int TAG_CONTENT_IDENTIFIER      = 0x051C;
    public static final int TAG_MAKE                    = 0x051D;
    public static final int TAG_MODEL                   = 0x051E;
    public static final int TAG_ORIGINATING_SIGNATURE   = 0x051F;
    public static final int TAG_PIXEL_DENSITY           = 0x0520;

    public QuickTimeMetadataDirectory()
    {
        this.setDescriptor(new QuickTimeMetadataDescriptor(this));
    }

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    @NotNull
    static final HashMap<String, Integer> _tagIntegerMap = new HashMap<String, Integer>();

    static
    {
        _tagIntegerMap.put("com.apple.quicktime.album", TAG_ALBUM);
        _tagIntegerMap.put("com.apple.quicktime.artist", TAG_ARTIST);
        _tagIntegerMap.put("com.apple.quicktime.artwork", TAG_ARTWORK);
        _tagIntegerMap.put("com.apple.quicktime.author", TAG_AUTHOR);
        _tagIntegerMap.put("com.apple.quicktime.comment", TAG_COMMENT);
        _tagIntegerMap.put("com.apple.quicktime.copyright", TAG_COPYRIGHT);
        _tagIntegerMap.put("com.apple.quicktime.creationdate", TAG_CREATION_DATE);
        _tagIntegerMap.put("com.apple.quicktime.description", TAG_DESCRIPTION);
        _tagIntegerMap.put("com.apple.quicktime.director", TAG_DIRECTOR);
        _tagIntegerMap.put("com.apple.quicktime.title", TAG_TITLE);
        _tagIntegerMap.put("com.apple.quicktime.genre", TAG_GENRE);
        _tagIntegerMap.put("com.apple.quicktime.information", TAG_INFORMATION);
        _tagIntegerMap.put("com.apple.quicktime.keywords", TAG_KEYWORDS);
        _tagIntegerMap.put("com.apple.quicktime.location.ISO6709", TAG_LOCATION_ISO6709);
        _tagIntegerMap.put("com.apple.quicktime.producer", TAG_PRODUCER);
        _tagIntegerMap.put("com.apple.quicktime.publisher", TAG_PUBLISHER);
        _tagIntegerMap.put("com.apple.quicktime.software", TAG_SOFTWARE);
        _tagIntegerMap.put("com.apple.quicktime.year", TAG_YEAR);
        _tagIntegerMap.put("com.apple.quicktime.collection.user", TAG_COLLECTION_USER);
        _tagIntegerMap.put("com.apple.quicktime.rating.user", TAG_RATING_USER);
        _tagIntegerMap.put("com.apple.quicktime.location.name", TAG_LOCATION_NAME);
        _tagIntegerMap.put("com.apple.quicktime.location.body", TAG_LOCATION_BODY);
        _tagIntegerMap.put("com.apple.quicktime.location.note", TAG_LOCATION_NOTE);
        _tagIntegerMap.put("com.apple.quicktime.location.role", TAG_LOCATION_ROLE);
        _tagIntegerMap.put("com.apple.quicktime.location.date", TAG_LOCATION_DATE);
        _tagIntegerMap.put("com.apple.quicktime.direction.facing", TAG_DIRECTION_FACING);
        _tagIntegerMap.put("com.apple.quicktime.direction.motion", TAG_DIRECTION_MOTION);
        _tagIntegerMap.put("com.apple.quicktime.displayname", TAG_DISPLAY_NAME);
        _tagIntegerMap.put("com.apple.quicktime.content.identifier", TAG_CONTENT_IDENTIFIER);
        _tagIntegerMap.put("com.apple.quicktime.make", TAG_MAKE);
        _tagIntegerMap.put("com.apple.quicktime.model", TAG_MODEL);
        _tagIntegerMap.put("com.apple.photos.originating.signature", TAG_ORIGINATING_SIGNATURE);
        _tagIntegerMap.put("com.apple.quicktime.pixeldensity", TAG_PIXEL_DENSITY);

        _tagIntegerMap.put("----", 0x0400);
        _tagIntegerMap.put("@PST", 0x0401);
        _tagIntegerMap.put("@ppi", 0x0402);
        _tagIntegerMap.put("@pti", 0x0403);
        _tagIntegerMap.put("@sti", 0x0404);
        _tagIntegerMap.put("AACR", 0x0405);
        _tagIntegerMap.put("CDEK", 0x0406);
        _tagIntegerMap.put("CDET", 0x0407);
        _tagIntegerMap.put("GUID", 0x0408);
        _tagIntegerMap.put("VERS", 0x0409);
        _tagIntegerMap.put("aART", 0x040A);
        _tagIntegerMap.put("akID", 0x040B);
        _tagIntegerMap.put("albm", 0x040C);
        _tagIntegerMap.put("apID", 0x040D);
        _tagIntegerMap.put("atID", 0x040E);
        _tagIntegerMap.put("auth", 0x040F);
        _tagIntegerMap.put("catg", 0x0410);
        _tagIntegerMap.put("cnID", 0x0411);
        _tagIntegerMap.put("covr", 0x0412);
        _tagIntegerMap.put("cpil", 0x0413);
        _tagIntegerMap.put("cprt", 0x0414);
        _tagIntegerMap.put("desc", 0x0415);
        _tagIntegerMap.put("disk", 0x0416);
        _tagIntegerMap.put("dscp", 0x0417);
        _tagIntegerMap.put("egid", 0x0418);
        _tagIntegerMap.put("geID", 0x0419);
        _tagIntegerMap.put("gnre", 0x041A);
        _tagIntegerMap.put("grup", 0x041B);
        _tagIntegerMap.put("gshh", 0x041C);
        _tagIntegerMap.put("gspm", 0x041D);
        _tagIntegerMap.put("gspu", 0x041E);
        _tagIntegerMap.put("gssd", 0x041F);
        _tagIntegerMap.put("gsst", 0x0420);
        _tagIntegerMap.put("gstd", 0x0421);
        _tagIntegerMap.put("hdvd", 0x0422);
        _tagIntegerMap.put("itnu", 0x0423);
        _tagIntegerMap.put("keyw", 0x0424);
        _tagIntegerMap.put("ldes", 0x0425);
        _tagIntegerMap.put("pcst", 0x0426);
        _tagIntegerMap.put("perf", 0x0427);
        _tagIntegerMap.put("pgap", 0x0428);
        _tagIntegerMap.put("plID", 0x0429);
        _tagIntegerMap.put("prID", 0x042A);
        _tagIntegerMap.put("purd", 0x042B);
        _tagIntegerMap.put("purl", 0x042C);
        _tagIntegerMap.put("rate", 0x042D);
        _tagIntegerMap.put("rldt", 0x042E);
        _tagIntegerMap.put("rtng", 0x042F);
        _tagIntegerMap.put("sfID", 0x0430);
        _tagIntegerMap.put("soaa", 0x0431);
        _tagIntegerMap.put("soal", 0x0432);
        _tagIntegerMap.put("soar", 0x0433);
        _tagIntegerMap.put("soco", 0x0434);
        _tagIntegerMap.put("sonm", 0x0435);
        _tagIntegerMap.put("sosn", 0x0436);
        _tagIntegerMap.put("stik", 0x0437);
        _tagIntegerMap.put("titl", 0x0438);
        _tagIntegerMap.put("tmpo", 0x0439);
        _tagIntegerMap.put("trkn", 0x043A);
        _tagIntegerMap.put("tven", 0x043B);
        _tagIntegerMap.put("tves", 0x043C);
        _tagIntegerMap.put("tvnn", 0x043D);
        _tagIntegerMap.put("tvsh", 0x043E);
        _tagIntegerMap.put("tvsn", 0x043F);
        _tagIntegerMap.put("yrrc", 0x0440);
        _tagIntegerMap.put("�ART", 0x0441);
        _tagIntegerMap.put("�alb", 0x0442);
        _tagIntegerMap.put("�cmt", 0x0443);
        _tagIntegerMap.put("�com", 0x0444);
        _tagIntegerMap.put("�cpy", 0x0445);
        _tagIntegerMap.put("�day", 0x0446);
        _tagIntegerMap.put("�des", 0x0447);
        _tagIntegerMap.put("�enc", 0x0448);
        _tagIntegerMap.put("�gen", 0x0449);
        _tagIntegerMap.put("�grp", 0x044A);
        _tagIntegerMap.put("�lyr", 0x044B);
        _tagIntegerMap.put("�nam", 0x044C);
        _tagIntegerMap.put("�nrt", 0x044D);
        _tagIntegerMap.put("�pub", 0x044E);
        _tagIntegerMap.put("�too", 0x044F);
        _tagIntegerMap.put("�trk", 0x0450);
        _tagIntegerMap.put("�wrt", 0x0451);

        _tagNameMap.put(TAG_ALBUM, "Album");
        _tagNameMap.put(TAG_ARTIST, "Artist");
        _tagNameMap.put(TAG_ARTWORK, "Artwork");
        _tagNameMap.put(TAG_AUTHOR, "Author");
        _tagNameMap.put(TAG_COMMENT, "Comment");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_CREATION_DATE, "Creation Date");
        _tagNameMap.put(TAG_DESCRIPTION, "Description");
        _tagNameMap.put(TAG_DIRECTOR, "Director");
        _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_GENRE, "Genre");
        _tagNameMap.put(TAG_INFORMATION, "Information");
        _tagNameMap.put(TAG_KEYWORDS, "Keywords");
        _tagNameMap.put(TAG_LOCATION_ISO6709, "ISO 6709");
        _tagNameMap.put(TAG_PRODUCER, "Producer");
        _tagNameMap.put(TAG_PUBLISHER, "Publisher");
        _tagNameMap.put(TAG_SOFTWARE, "Software");
        _tagNameMap.put(TAG_YEAR, "Year");
        _tagNameMap.put(TAG_COLLECTION_USER, "Collection User");
        _tagNameMap.put(TAG_RATING_USER, "Rating User");
        _tagNameMap.put(TAG_LOCATION_NAME, "Location Name");
        _tagNameMap.put(TAG_LOCATION_BODY, "Location Body");
        _tagNameMap.put(TAG_LOCATION_NOTE, "Location Note");
        _tagNameMap.put(TAG_LOCATION_ROLE, "Location Role");
        _tagNameMap.put(TAG_LOCATION_DATE, "Location Date");
        _tagNameMap.put(TAG_DIRECTION_FACING, "Direction Facing");
        _tagNameMap.put(TAG_DIRECTION_MOTION, "Direction Motion");
        _tagNameMap.put(TAG_DISPLAY_NAME, "Display Name");
        _tagNameMap.put(TAG_CONTENT_IDENTIFIER, "Content Identifier");
        _tagNameMap.put(TAG_MAKE, "Make");
        _tagNameMap.put(TAG_MODEL, "Model");
        _tagNameMap.put(TAG_ORIGINATING_SIGNATURE, "Originating Signature");
        _tagNameMap.put(TAG_PIXEL_DENSITY, "Pixel Density");

        _tagNameMap.put(0x0400, "iTunes Info");
        _tagNameMap.put(0x0401, "Parent Short Title");
        _tagNameMap.put(0x0402, "Parent Product ID");
        _tagNameMap.put(0x0403, "Parent Title");
        _tagNameMap.put(0x0404, "Short Title");
        _tagNameMap.put(0x0405, "Unknown_AACR?");
        _tagNameMap.put(0x0406, "Unknown_CDEK?");
        _tagNameMap.put(0x0407, "Unknown_CDET?");
        _tagNameMap.put(0x0408, "GUID");
        _tagNameMap.put(0x0409, "Product Version");
        _tagNameMap.put(0x040A, "Album Artist");
        _tagNameMap.put(0x040B, "Apple Store Account Type");
        _tagNameMap.put(0x040C, "Album");
        _tagNameMap.put(0x040D, "Apple Store Account");
        _tagNameMap.put(0x040E, "Album Title ID");
        _tagNameMap.put(0x040F, "Author");
        _tagNameMap.put(0x0410, "Category");
        _tagNameMap.put(0x0411, "Apple Store Catalog ID");
        _tagNameMap.put(0x0412, "Cover Art");
        _tagNameMap.put(0x0413, "Compilation");
        _tagNameMap.put(0x0414, "Copyright");
        _tagNameMap.put(0x0415, "Description");
        _tagNameMap.put(0x0416, "Disk Number");
        _tagNameMap.put(0x0417, "Description");
        _tagNameMap.put(0x0418, "Episode Global Unique ID");
        _tagNameMap.put(0x0419, "Genre ID");
        _tagNameMap.put(0x041A, "Genre");
        _tagNameMap.put(0x041B, "Grouping");
        _tagNameMap.put(0x041C, "Google Host Header");
        _tagNameMap.put(0x041D, "Google Ping Message");
        _tagNameMap.put(0x041E, "Google Ping URL");
        _tagNameMap.put(0x041F, "Google Source Data");
        _tagNameMap.put(0x0420, "Google Start Time");
        _tagNameMap.put(0x0421, "Google Track Duration");
        _tagNameMap.put(0x0422, "HD Video");
        _tagNameMap.put(0x0423, "iTunes U");
        _tagNameMap.put(0x0424, "Keyword");
        _tagNameMap.put(0x0425, "Long Description");
        _tagNameMap.put(0x0426, "Podcast");
        _tagNameMap.put(0x0427, "Performer");
        _tagNameMap.put(0x0428, "Play Gap");
        _tagNameMap.put(0x0429, "Play List ID");
        _tagNameMap.put(0x042A, "Product ID");
        _tagNameMap.put(0x042B, "Purchase Date");
        _tagNameMap.put(0x042C, "Podcast URL");
        _tagNameMap.put(0x042D, "Rating Percent");
        _tagNameMap.put(0x042E, "Release Date");
        _tagNameMap.put(0x042F, "Rating");
        _tagNameMap.put(0x0430, "Apple Store Country");
        _tagNameMap.put(0x0431, "Sort Album Artist");
        _tagNameMap.put(0x0432, "Sort Album");
        _tagNameMap.put(0x0433, "Sort Artist");
        _tagNameMap.put(0x0434, "Sort Composer");
        _tagNameMap.put(0x0435, "Sort Name");
        _tagNameMap.put(0x0436, "Sort Show");
        _tagNameMap.put(0x0437, "Media Type");
        _tagNameMap.put(0x0438, "Title");
        _tagNameMap.put(0x0439, "Beats Per Minute");
        _tagNameMap.put(0x043A, "Track Number");
        _tagNameMap.put(0x043B, "TV Episode ID");
        _tagNameMap.put(0x043C, "TV Episode");
        _tagNameMap.put(0x043D, "TV Network Name");
        _tagNameMap.put(0x043E, "TV Show");
        _tagNameMap.put(0x043F, "TV Season");
        _tagNameMap.put(0x0440, "Year");
        _tagNameMap.put(0x0441, "Artist");
        _tagNameMap.put(0x0442, "Album");
        _tagNameMap.put(0x0443, "Comment");
        _tagNameMap.put(0x0444, "Composer");
        _tagNameMap.put(0x0445, "Copyright");
        _tagNameMap.put(0x0446, "Content Create Date");
        _tagNameMap.put(0x0447, "Description");
        _tagNameMap.put(0x0448, "Encoded By");
        _tagNameMap.put(0x0449, "Genre");
        _tagNameMap.put(0x044A, "Grouping");
        _tagNameMap.put(0x044B, "Lyrics");
        _tagNameMap.put(0x044C, "Title");
        _tagNameMap.put(0x044D, "Narrator");
        _tagNameMap.put(0x044E, "Publisher");
        _tagNameMap.put(0x044F, "Encoder");
        _tagNameMap.put(0x0450, "Track");
        _tagNameMap.put(0x0451, "Composer");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QuickTime Metadata";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
