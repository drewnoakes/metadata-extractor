/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 *
 * Created by Drew Noakes on 28-Apr-2002 17:40:00.
 */
package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A top-level object to hold various types of metadata (such as Exif or IPTC) relating to one entity (such as a file
 * or stream).
 * <p/>
 * Metadata objects may contain zero or more directories.  Each directory may contain zero or more tags with
 * corresponding values.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public final class Metadata implements Serializable
{
    @NotNull
    private final HashMap<Class, Directory> _directoryByClass = new HashMap<Class, Directory>();

    /**
     * List of Directory objects set against this object.  Keeping a list handy makes
     * creation of an Iterator and counting tags simple.
     */
    @NotNull
    private final ArrayList<Directory> _directoryList = new ArrayList<Directory>();

// OTHER METHODS

    /**
     * Creates an Iterator over the tag types set against this image, preserving the order
     * in which they were set.  Should the same tag have been set more than once, it's first
     * position is maintained, even though the final value is used.
     *
     * @return an Iterator of tag types set for this image
     * @deprecated Use getDirectories() instead
     */
    @Deprecated
    @NotNull
    public Iterator<Directory> getDirectoryIterator()
    {
        return _directoryList.iterator();
    }

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
     * Returns a <code>Directory</code> of specified type.  If this <code>Metadata</code> object already contains
     * such a directory, it is returned.  Otherwise a new instance of this directory will be created and stored within
     * this Metadata object.
     *
     * @param type the type of the Directory implementation required.
     * @return a directory of the specified type.
     */
    @NotNull
    public Directory getDirectory(@NotNull Class<? extends Directory> type)
    {
        // TODO make one method getDirectory and another getOrCreateDirectory as this method is confusingly named

        // check if we've already issued this type of directory
        if (_directoryByClass.containsKey(type))
            return _directoryByClass.get(type);

        Directory directory;
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
     * Indicates whether a given directory type has been created in this metadata
     * repository.  Directories are created by calling getDirectory(Class).
     *
     * @param type the Directory type
     * @return true if the metadata directory has been created
     */
    public boolean containsDirectory(Class<? extends Directory> type)
    {
        return _directoryByClass.containsKey(type);
    }
}
