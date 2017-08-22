package com.drew.metadata.xmp.handlers;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.indd.InddDescriptor;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class InddXmpDirectory extends Directory
{
    public int currentKey = 1;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {

    }

    public InddXmpDirectory()
    {
        this.setDescriptor(new InddXmpDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "XMP";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
