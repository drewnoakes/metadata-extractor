/*
 * Metadata.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 28 April 2002, 17:40
 * Modified 04 Aug 2002
 * - Adjusted javadoc
 * - Added
 * Modified 29 Oct 2002 (v1.2)
 * - Stored IFD directories in separate tag-spaces
 * - iterator() now returns an Iterator over a list of TagValue objects
 * - More get*Description() methods to detail GPS tags, among others
 * - Put spaces between words of tag name for presentation reasons (they had no
 *   significance in compound form)
 */
package com.drew.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A top-level object to hold various types of metadata (such as Exif or IPTC) relating to one entity (such as a file
 * or stream).
 *
 * Metadata objects may contain zero or more directories.  Each directory may contain zero or more tags with
 * corresponding values.
 */
public final class Metadata implements Serializable
{
    /**
     *
     */
    private final HashMap<Class, Directory> _directoryByClass;

    /**
     * List of Directory objects set against this object.  Keeping a list handy makes
     * creation of an Iterator and counting tags simple.
     */
    private final ArrayList<Directory> _directoryList;

    /**
     * Creates a new instance of Metadata.
     */
    public Metadata()
    {
        _directoryByClass = new HashMap<Class, Directory>();
        _directoryList = new ArrayList<Directory>();
    }

// OTHER METHODS

    /**
     * Creates an Iterator over the tag types set against this image, preserving the order
     * in which they were set.  Should the same tag have been set more than once, it's first
     * position is maintained, even though the final value is used.
     * @return an Iterator of tag types set for this image
     * @deprecated Use getDirectories() instead
     */
    @Deprecated
    public Iterator<Directory> getDirectoryIterator()
    {
        return _directoryList.iterator();
    }

    /**
     * Returns an objects for iterating over Directory objects in the order in which they were added.
     * @return an iterable collection of directories
     */
    public Iterable<Directory> getDirectories()
    {
        return _directoryList;
    }

    /**
     * Returns a count of unique directories in this metadata collection.
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
     * @param type the type of the Directory implementation required.
     * @return a directory of the specified type.
     */
    public Directory getDirectory(Class<? extends Directory> type)
    {
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
     * @param type the Directory type
     * @return true if the metadata directory has been created
     */
    public boolean containsDirectory(Class<? extends Directory> type)
    {
        return _directoryByClass.containsKey(type);
    }
}
