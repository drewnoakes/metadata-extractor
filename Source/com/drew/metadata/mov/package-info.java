/**
 * Contains classes for the extraction and modelling of MOV file format metadata.
 *
 * This QuickTime reader package is handler based.  Each QuickTime file is composed in a hierarchical structure that contains atoms.
 * Atoms all have the same initial structure, but the rest will change depending upon the atom type.  An atom can either be a
 * standalone (leaf) atom or a container.
 *
 * QuickTimeReader will recursively go through all of the atoms using the handlers to know which container/atom should be read.
 * A new handler is created when the current container has an immediate ‘handler’ atom.  This means that the way the
 * containing atoms are read may depend upon that information.  This is also very important because some atoms have
 * the same name and the only way to determine how to read this data is with the handler.
 *
 */
package com.drew.metadata.mov;
