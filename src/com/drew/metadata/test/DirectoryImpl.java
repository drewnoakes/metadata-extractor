/*
 * Created by dnoakes on 29-Nov-2002 09:07:43 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.metadata.Directory;

import java.util.HashMap;

public class DirectoryImpl extends Directory
{
    private HashMap _tagNameMap;

    public DirectoryImpl()
    {
        this._tagNameMap = new HashMap();
    }

    public String getName()
    {
        return "";
    }

    protected HashMap getTagNameMap()
    {
        return _tagNameMap;
    }
}
