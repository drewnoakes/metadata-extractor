package com.drew.imaging.zip;

import com.drew.lang.ByteTrie;
import com.drew.lang.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Payton Garland
 */
public class ConditionTree<T>
{
    /** A node in the trie. Has children and may have an associated value. */
    static class ConditionTreeNode<T>
    {
        private final Map<Boolean, ConditionTreeNode<T>> _children = new HashMap<Boolean, ConditionTreeNode<T>>();
        private T _value = null;

        public void setValue(T value)
        {
            if (_value != null)
                throw new RuntimeException("Value already set for this trie node");
            _value = value;
        }
    }

    private final ConditionTreeNode<T> _root = new ConditionTreeNode<T>();
    private int _maxDepth;

    /**
     * Return the most specific value stored for this byte sequence.
     * If not found, returns <code>null</code> or a default values as specified by
     * calling {@link ByteTrie#setDefaultValue}.
     */
    @Nullable
    public T find(Boolean[] booleans)
    {
        ConditionTreeNode<T> node = _root;
        T value = node._value;
        for (Boolean b : booleans) {
            ConditionTreeNode<T> child = node._children.get(b);
            if (child == null)
                break;
            node = child;
            if (node._value != null)
                value = node._value;
        }
        return value;
    }

    /** Store the given value at the specified path. */
    public void addPath(T value, Boolean[] booleans)
    {
        int depth = 0;
        ConditionTreeNode<T> node = _root;
        for (Boolean b : booleans) {
            ConditionTreeNode<T> child = node._children.get(b);
            if (child == null) {
                child = new ConditionTreeNode<T>();
                node._children.put(b, child);
            }
            node = child;
            depth++;
        }
        node.setValue(value);
        _maxDepth = Math.max(_maxDepth, depth);
    }

    /** Sets the default value to use in {@link ByteTrie#find(byte[])} when no path matches. */
    public void setDefaultValue(T defaultValue)
    {
        _root.setValue(defaultValue);
    }

    /** Gets the maximum depth stored in this trie. */
    public int getMaxDepth()
    {
        return _maxDepth;
    }
}
