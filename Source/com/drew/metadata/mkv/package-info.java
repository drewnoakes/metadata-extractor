/**
 * Matroska and WebM metadata. Tag structure is represented by a
 * {@link com.drew.metadata.mkv.MkvTagsDirectory} for each Tags update,
 * {@link com.drew.metadata.mkv.MkvTagDirectory} target groups, and one
 * {@link com.drew.metadata.mkv.MkvSimpleTagDirectory} per SimpleTag.
 * Directory parent links preserve nesting; repeated tag names and values
 * remain separate directories. Targets may occur before or after values.
 */
package com.drew.metadata.mkv;
