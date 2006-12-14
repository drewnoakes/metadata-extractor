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
 *
 * Created by dnoakes on 26-Nov-2002 11:21:43 using IntelliJ IDEA.
 */
package com.drew.metadata;

/**
 * Interface through which all classes responsible for decoding a particular type of metadata may be called.
 * Note that the data source is not specified on this interface.  Instead it is suggested that implementations
 * take their data within a constructor.  Constructors might be overloaded to allow for different sources, such as
 * files, streams and byte arrays.  As such, instances of implementations of this interface would be single-use and
 * not threadsafe.
 */
public interface MetadataReader
{
    // TODO is this overload needed?  it is only used by unit tests...
    /**
     * Create a new Metadata instance and return it, populated with whatever directories this MetadataReader
     * implementation is responsible for.
     * @return The populated Metadata object.
     * @deprecated Use the overload that takes a Metadata object instead
     */
    public Metadata extract();

    /**
     * Extract metadata from the source and merge it into an existing Metadata object.
     * @return The updated Metadata object.
     */
    public Metadata extract(Metadata metadata);
}
