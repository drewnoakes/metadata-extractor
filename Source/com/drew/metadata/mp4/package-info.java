/**
 * Contains classes for the extraction and modelling of MP4 file format metadata.
 *
 * This MP4 reader package is handler based.  Each MP4 file is composed in a hierarchical structure that contains boxes.
 * Atoms all have the same initial structure, but the rest will change depending upon the box type.  A box can either be a
 * standalone (leaf) box or a container.
 *
 * Mp4Reader will recursively go through all of the atoms using the handlers to know which container/atom should be read.
 * A new handler is created when the current container has an immediate ‘handler’ atom.  This means that the way the
 * containing atoms are read may depend upon that information.  This is also very important because some atoms have
 * the same name and the only way to determine how to read this data is with the handler.
 *
 * Documentation: ISO/IEC 14496-12:2015
 *                http://standards.iso.org/ittf/PubliclyAvailableStandards/c068960_ISO_IEC_14496-12_2015.zip
 */
package com.drew.metadata.mp4;
