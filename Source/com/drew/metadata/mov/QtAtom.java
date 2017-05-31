package com.drew.metadata.mov;

import java.util.ArrayList;
import java.util.List;

public class QtAtom {

    protected long size;
    protected String type;
    protected long offset;

    protected List<QtAtom> children;

    public QtAtom(long size, String type, long offset)
    {
        this.size = size;
        this.type = type;
        this.offset = offset;

        children = new ArrayList<QtAtom>();
    }

    public QtAtom(long size, String type, long offset, List<QtAtom> children)
    {
        this.size = size;
        this.type = type;
        this.offset = offset;
        this.children = children;
    }

    public QtAtom(QtAtom atom)
    {
        size = atom.getSize();
        type = atom.getType();
        offset = atom.getOffset();
        children = atom.getChildren();
    }

    public QtAtom(List<QtAtom> children)
    {
        this.children = children;
        this.type = QtAtomTypes.ROOT_ATOM;
    }

    public List<QtAtom> getChildren()
    {
        return children;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getOffset()
    {
        return offset;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public boolean isLeafNode()
    {
        return (children.size() == 0);
    }

    public String toString()
    {
        if (children.size() == 0)
        {
            return new String(type + " (" + size + " bytes)(offset:" + offset + ")");
        }
        else
        {
            String childrenString = "child";
            if (children.size() > 1)
            {
                childrenString = "children";
            }
            return new String(type+ " (" + size + "bytes) (offset:" + offset +")" + " - " + children.size() + " " + childrenString);
        }
    }
}
