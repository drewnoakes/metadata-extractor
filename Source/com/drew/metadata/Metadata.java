/*
 * Copyright 2002-2014 Drew Noakes
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A top-level object that holds the metadata values extracted from an image.
 * <p/>
 * Metadata objects may contain zero or more {@link Directory} objects.  Each directory may contain zero or more tags
 * with corresponding values.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public final class Metadata
{
    @NotNull
    private final Map<Class<? extends Directory>,Directory> _directoryByClass = new HashMap<Class<? extends Directory>, Directory>();
    
    /**
     * List of Directory objects set against this object.  Keeping a list handy makes
     * creation of an Iterator and counting tags simple.
     */
    @NotNull
    private final Collection<Directory> _directoryList = new ArrayList<Directory>();

    /**
     * Returns an objects for iterating over Directory objects in the order in which they were added.
     *
     * @return an iterable collection of directories
     */
    @NotNull
    public Iterable<Directory> getDirectories()
    {
        return _directoryList;
    }

    /**
     * Returns a count of unique directories in this metadata collection.
     *
     * @return the number of unique directory types set for this metadata collection
     */
    public int getDirectoryCount()
    {
        return _directoryList.size();
    }

    /**
     * Returns a {@link Directory} of specified type.  If this {@link Metadata} object already contains
     * such a directory, it is returned.  Otherwise a new instance of this directory will be created and stored within
     * this {@link Metadata} object.
     *
     * @param type the type of the Directory implementation required.
     * @return a directory of the specified type.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Directory> T getOrCreateDirectory(@NotNull Class<T> type)
    {
        // We suppress the warning here as the code asserts a map signature of Class<T>,T.
        // So after get(Class<T>) it is for sure the result is from type T.

        // check if we've already issued this type of directory
        if (_directoryByClass.containsKey(type))
            return (T)_directoryByClass.get(type);

        T directory;
        try {
            directory = type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate provided Directory type: " + type.toString());
        }
        // store the directory
        _directoryByClass.put(type, directory);
        _directoryList.add(directory);

        return directory;
    }

    /**
     * If this {@link Metadata} object contains a {@link Directory} of the specified type, it is returned.
     * Otherwise <code>null</code> is returned.
     *
     * @param type the Directory type
     * @param <T> the Directory type
     * @return a Directory of type T if it exists in this {@link Metadata} object, otherwise <code>null</code>.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Directory> T getDirectory(@NotNull Class<T> type)
    {
        // We suppress the warning here as the code asserts a map signature of Class<T>,T.
        // So after get(Class<T>) it is for sure the result is from type T.

        return (T)_directoryByClass.get(type);
    }

    /**
     * Indicates whether a given directory type has been created in this metadata
     * repository.  Directories are created by calling {@link Metadata#getOrCreateDirectory(Class)}.
     *
     * @param type the {@link Directory} type
     * @return true if the {@link Directory} has been created
     */
    public boolean containsDirectory(Class<? extends Directory> type)
    {
        return _directoryByClass.containsKey(type);
    }

    /**
     * Indicates whether any errors were reported during the reading of metadata values.
     * This value will be true if Directory.hasErrors() is true for one of the contained {@link Directory} objects.
     *
     * @return whether one of the contained directories has an error
     */
    public boolean hasErrors()
    {
        for (Directory directory : _directoryList) {
            if (directory.hasErrors())
                return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Directory directory : getDirectories()) {
            stringBuilder.append(directory);
            stringBuilder.append('\n');
            for (Tag tag : directory.getTags()) {
                stringBuilder.append(tag);
                stringBuilder.append('\n');
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
