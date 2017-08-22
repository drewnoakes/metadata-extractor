package com.drew.metadata.indd;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class InddDirectory extends Directory
{
    public static final int TAG_DATABASE_ID = 1;
    public static final int TAG_DATABASE_TYPE = 2;
    public static final int TAG_SEQUENCE_NUMBER = 3;
    public static final int TAG_FILE_PAGES = 4;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_DATABASE_ID, "Database Identifier");
        _tagNameMap.put(TAG_DATABASE_TYPE, "Database Type");
        _tagNameMap.put(TAG_SEQUENCE_NUMBER, "Master Page Sequence Number");
        _tagNameMap.put(TAG_FILE_PAGES, "4 KB Page Count");
    }

    public InddDirectory()
    {
        this.setDescriptor(new InddDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "INDD";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
