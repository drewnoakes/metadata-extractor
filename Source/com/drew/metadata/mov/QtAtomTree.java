package com.drew.metadata.mov;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtAtomTree
{
    QtAtomNode root;

    public QtAtomTree()
    {
        root = new QtAtomNode(0, 0, "root");
    }

    public boolean contains(QtAtomNode node)
    {
        return containsHelper(root, node.getName());
    }

    public boolean containsHelper(QtAtomNode node, String name)
    {
        for (QtAtomNode child : node.getChildren()) {
            if (child.getName().equals(name)) {
                return true;
            } else if (child.getChildren() == null) {
                return false;
            } else {
                containsHelper(child, name);
            }
        }
        return false;
    }
}
