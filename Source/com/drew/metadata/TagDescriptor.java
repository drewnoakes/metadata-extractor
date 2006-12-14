/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 */
package com.drew.metadata;

import java.io.Serializable;

/**
 * Abstract base class for all tag descriptor classes.  Implementations are responsible for
 * providing the human-readable string represenation of tag values stored in a directory.
 * The directory is provided to the tag descriptor via its constructor.
 */
public abstract class TagDescriptor implements Serializable
{
    protected final Directory _directory;

    public TagDescriptor(Directory directory)
    {
        _directory = directory;
    }

    /**
     * Returns a descriptive value of the the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the Exif segment.  If no substitution is
     * available, the value provided by getString(int) will be returned.
     * <p>
     * This and getString(int) are the only 'get' methods that won't throw an
     * exception.
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    public abstract String getDescription(int tagType) throws MetadataException;
}
