/*
 * Created by dnoakes on 12-Nov-2002 22:18:13 using IntelliJ IDEA.
 */
package com.drew.metadata;

import java.io.Serializable;

/**
 *
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
