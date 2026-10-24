/**
 * Contains classes for the extraction and modelling of ICC (International Color Consortium) profile metadata.
 * <p>
 * ICC parsing accepts declared profile sizes up to 16 MiB, including compressed PNG profiles.
 * Profiles may contain at most 4096 tag entries to bound cache and directory overhead,
 * including entries with empty or very small payloads.
 * Tags that reference identical data share a single byte array. To bound memory use for overlapping
 * tag ranges, the total size of distinct tag payloads copied cannot exceed the declared profile size.
 * Profiles that exceed these limits produce errors in the ICC directory; other metadata can still be read.
 */
package com.drew.metadata.icc;
