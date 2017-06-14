package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtAtomNode
{
    private ArrayList<QtAtomNode> children;

    private long pos;
    private long size;
    private String name;

    public QtAtomNode(long pos, long size, @NotNull String name)
    {
        this.pos = pos;
        this.size = size;
        this.name = name;
    }

    public ArrayList<QtAtomNode> getChildren()
    {
        return children;
    }

    public long getPos()
    {
        return pos;
    }

    public long getSize()
    {
        return size;
    }

    public String getName()
    {
        return name;
    }

    public void addChild(QtAtomNode child)
    {
        children.add(child);
    }


}
