/*
 * Created by dnoakes on 29-Nov-2002 09:07:43 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.metadata.Directory;

import java.util.HashMap;

public class MockDirectory extends Directory
{
    private final HashMap _tagNameMap;

    public MockDirectory()
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
