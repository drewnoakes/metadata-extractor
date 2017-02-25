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
    /**
     * The list of {@link Directory} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<Directory> _directories = new ArrayList<Directory>();

    /**
     * Returns an iterable set of the {@link Directory} instances contained in this metadata collection.
     *
     * @return an iterable set of directories
     */
    @NotNull
    public Iterable<Directory> getDirectories()
    {
        return _directories;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Directory> Collection<T> getDirectoriesOfType(Class<T> type)
    {
        List<T> directories = new ArrayList<T>();
        for (Directory dir : _directories) {
            if (type.isAssignableFrom(dir.getClass())) {
                directories.add((T)dir);
            }
        }
        return directories;
    }

    /**
     * Returns the count of directories in this metadata collection.
     *
     * @return the number of unique directory types set for this metadata collection
     */
    public int getDirectoryCount()
    {
        return _directories.size();
    }

    /**
     * Adds a directory to this metadata collection.
     *
     * @param directory the {@link Directory} to add into this metadata collection.
     */
    public <T extends Directory> void addDirectory(@NotNull T directory)
    {
        _directories.add(directory);
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
        for (Directory dir : _directories) {
            if (type.isAssignableFrom(dir.getClass()))
                return (T)dir;
        }
        return null;
    }

    /**
     * Indicates whether an instance of the given directory type exists in this Metadata instance.
     *
     * @param type the {@link Directory} type
     * @return <code>true</code> if a {@link Directory} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsDirectoryOfType(Class<? extends Directory> type)
    {
        for (Directory dir : _directories) {
            if (type.isAssignableFrom(dir.getClass()))
                return true;
        }
        return false;
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
}
