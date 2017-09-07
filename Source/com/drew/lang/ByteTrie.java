/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.lang;

import com.drew.lang.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores values using a prefix tree (aka 'trie', i.e. reTRIEval data structure).
 *
 * @param <T> the type of value to store for byte sequences
 */
public class ByteTrie<T>
{
    /** A node in the trie. Has children and may have an associated value. */
    static class ByteTrieNode<T>
    {
        private final Map<Byte, ByteTrieNode<T>> _children = new HashMap<Byte, ByteTrieNode<T>>();
        private T _value = null;

        public void setValue(T value)
        {
            if (_value != null)
                throw new RuntimeException("Value already set for this trie node");
            _value = value;
        }
    }

    private final ByteTrieNode<T> _root = new ByteTrieNode<T>();
    private int _maxDepth;

    /**
     * Return the most specific value stored for this byte sequence.
     * If not found, returns <code>null</code> or a default values as specified by
     * calling {@link ByteTrie#setDefaultValue}.
     */
    @Nullable
    public T find(byte[] bytes)
    {
        ByteTrieNode<T> node = _root;
        T value = node._value;
        for (byte b : bytes) {
            ByteTrieNode<T> child = node._children.get(b);
            if (child == null)
                break;
            node = child;
            if (node._value != null)
                value = node._value;
        }
        return value;
    }

    /** Store the given value at the specified path. */
    public void addPath(T value, byte[]... parts)
    {
        int depth = 0;
        ByteTrieNode<T> node = _root;
        for (byte[] part : parts) {
            for (byte b : part) {
                ByteTrieNode<T> child = node._children.get(b);
                if (child == null) {
                    child = new ByteTrieNode<T>();
                    node._children.put(b, child);
                }
                node = child;
                depth++;
            }
        }
        if (depth == 0)
            throw new IllegalArgumentException("Parts must contain at least one byte.");
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
