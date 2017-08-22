package com.drew.metadata.zip;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class ZipDirectory extends Directory
{
    // Central Directory
    public static final int TAG_VERSION = 1;
    public static final int TAG_VERSION_NEEDED = 2;
    public static final int TAG_FLAGS = 3;
    public static final int TAG_COMPRESSION = 4;
    public static final int TAG_MOD_TIME = 5;
    public static final int TAG_MOD_DATE = 6;
    public static final int TAG_CRC32 = 7;
    public static final int TAG_COMPRESSED_SIZE = 8;
    public static final int TAG_UNCOMPRESSED_SIZE = 9;
    public static final int TAG_DISK_NUM = 10;
    public static final int TAG_INTERNAL_ATTRIBUTE = 11;
    public static final int TAG_FILE_NAME = 12;
    public static final int TAG_EXTRA_FIELD = 13;
    public static final int TAG_FILE_COMMENT = 14;
    public static final int TAG_COMPATIBILITY = 15;
    public static final int TAG_ENCRYPTION = 16;
    public static final int TAG_ZIP_FILE_COUNT = 17;
    public static final int TAG_ZIP_FILE_COMMENT = 18;

    @NotNull
    public static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_VERSION, "Version(s)");
        _tagNameMap.put(TAG_VERSION_NEEDED, "Version(s) Needed");
        _tagNameMap.put(TAG_FLAGS, "Flags");
        _tagNameMap.put(TAG_COMPRESSION, "Compression(s)");
        _tagNameMap.put(TAG_MOD_TIME, "Last Modification Time(s)");
        _tagNameMap.put(TAG_MOD_DATE, "Last Modification Date(s)");
        _tagNameMap.put(TAG_CRC32, "Crc-32");
        _tagNameMap.put(TAG_COMPRESSED_SIZE, "Compressed Size(s)");
        _tagNameMap.put(TAG_UNCOMPRESSED_SIZE, "Uncompressed Size(s)");
        _tagNameMap.put(TAG_DISK_NUM, "Disk Number(s)");
        _tagNameMap.put(TAG_INTERNAL_ATTRIBUTE, "Internal Attribute(s)");
        _tagNameMap.put(TAG_FILE_NAME, "File Name(s)");
        _tagNameMap.put(TAG_EXTRA_FIELD, "Extra Field(s)");
        _tagNameMap.put(TAG_FILE_COMMENT, "File Comment(s)");
        _tagNameMap.put(TAG_COMPATIBILITY, "Compatibility of File(s)");
        _tagNameMap.put(TAG_ENCRYPTION, "Encryption");
        _tagNameMap.put(TAG_ZIP_FILE_COUNT, "Archive File Count");
        _tagNameMap.put(TAG_ZIP_FILE_COMMENT, "Archive File Comment");
    }

    public ZipDirectory()
    {
        this.setDescriptor(new ZipDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Zip";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
