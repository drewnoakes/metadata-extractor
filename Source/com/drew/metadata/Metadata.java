/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.util.*;

/**
 * A top-level object that holds the metadata values extracted from an image.
 * <p>
 * Metadata objects may contain zero or more {@link Directory} objects.  Each directory may contain zero or more tags
 * with corresponding values.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public final class Metadata
{
    @NotNull
    private final Map<Class<? extends Directory>,Collection<Directory>> _directoryListByClass = new HashMap<Class<? extends Directory>, Collection<Directory>>();

    /**
     * Returns an iterable set of the {@link Directory} instances contained in this metadata collection.
     *
     * @return an iterable set of directories
     */
    @NotNull
    public Iterable<Directory> getDirectories()
    {
        return new DirectoryIterable(_directoryListByClass);
    }

    @Nullable
    public <T extends Directory> Collection<T> getDirectoriesOfType(Class<T> type)
    {
        return (Collection<T>)_directoryListByClass.get(type);
    }

    /**
     * Returns the count of directories in this metadata collection.
     *
     * @return the number of unique directory types set for this metadata collection
     */
    public int getDirectoryCount()
    {
        int count = 0;
        for (Map.Entry<Class<? extends Directory>,Collection<Directory>> pair : _directoryListByClass.entrySet())
            count += pair.getValue().size();
        return count;
    }

    /**
     * Adds a directory to this metadata collection.
     *
     * @param directory the {@link Directory} to add into this metadata collection.
     */
    public <T extends Directory> void addDirectory(@NotNull T directory)
    {
        getOrCreateDirectoryList(directory.getClass()).add(directory);
    }

    /**
     * Gets the first {@link Directory} of the specified type contained within this metadata collection.
     * If no instances of this type are present, <code>null</code> is returned.
     *
     * @param type the Directory type
     * @param <T> the Directory type
     * @return the first Directory of type T in this metadata collection, or <code>null</code> if none exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Directory> T getFirstDirectoryOfType(@NotNull Class<T> type)
    {
        // We suppress the warning here as the code asserts a map signature of Class<T>,T.
        // So after get(Class<T>) it is for sure the result is from type T.

        Collection<Directory> list = getDirectoryList(type);

        if (list == null || list.isEmpty())
            return null;

        return (T)list.iterator().next();
    }

    /**
     * Indicates whether an instance of the given directory type exists in this Metadata instance.
     *
     * @param type the {@link Directory} type
     * @return <code>true</code> if a {@link Directory} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsDirectoryOfType(Class<? extends Directory> type)
    {
        Collection<Directory> list = getDirectoryList(type);
        return list != null && !list.isEmpty();
    }

    /**
     * Indicates whether any errors were reported during the reading of metadata values.
     * This value will be true if Directory.hasErrors() is true for one of the contained {@link Directory} objects.
     *
     * @return whether one of the contained directories has an error
     */
    public boolean hasErrors()
    {
        for (Directory directory : getDirectories()) {
            if (directory.hasErrors())
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        int count = getDirectoryCount();
        return String.format("Metadata (%d %s)",
            count,
            count == 1
                ? "directory"
                : "directories");
    }

    @Nullable
    private <T extends Directory> Collection<Directory> getDirectoryList(@NotNull Class<T> type)
    {
        return _directoryListByClass.get(type);
    }

    @NotNull
    private <T extends Directory> Collection<Directory> getOrCreateDirectoryList(@NotNull Class<T> type)
    {
        Collection<Directory> collection = getDirectoryList(type);
        if (collection != null)
            return collection;
        collection = new ArrayList<Directory>();
        _directoryListByClass.put(type, collection);
        return collection;
    }

    private static class DirectoryIterable implements Iterable<Directory>
    {
        private final Map<Class<? extends Directory>, Collection<Directory>> _map;

        public DirectoryIterable(Map<Class<? extends Directory>, Collection<Directory>> map)
        {
            _map = map;
        }

        public Iterator<Directory> iterator()
        {
            return new DirectoryIterator(_map);
        }

        private static class DirectoryIterator implements Iterator<Directory>
        {
            @NotNull
            private final Iterator<Map.Entry<Class<? extends Directory>, Collection<Directory>>> _mapIterator;
            @Nullable
            private Iterator<Directory> _listIterator;

            public DirectoryIterator(Map<Class<? extends Directory>, Collection<Directory>> map)
            {
                _mapIterator = map.entrySet().iterator();

                if (_mapIterator.hasNext())
                    _listIterator = _mapIterator.next().getValue().iterator();
            }

            public boolean hasNext()
            {
                return _listIterator != null && (_listIterator.hasNext() || _mapIterator.hasNext());
            }

            public Directory next()
            {
                if (_listIterator == null || (!_listIterator.hasNext() && !_mapIterator.hasNext()))
                    throw new NoSuchElementException();

                while (!_listIterator.hasNext())
                    _listIterator = _mapIterator.next().getValue().iterator();

                return _listIterator.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        }
    }
}
