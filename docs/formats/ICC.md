# ICC profiles

ICC profiles describe colour characteristics and contain a header and tag table.
Profile sizes, tag counts, offsets, and lengths are untrusted input. A valid
range in the underlying file is not necessarily a valid range in the declared
profile. Compressed profiles also require bounds before or during decompression.

## Shared parsing limits

Both implementations accept declared profile sizes from 132 bytes through 16 MiB
and at most 4096 tag entries, including entries with empty or tiny payloads.
Tag-table and payload ranges are checked against the declared profile size.
Tags referencing identical data ranges share a byte array. Total distinct copied
tag payload bytes cannot exceed the declared profile size, bounding copies from
overlapping ranges. Profiles exceeding these limits produce errors in the ICC
directory; other metadata can still be read. The 16 MiB profile limit also
applies to compressed PNG profiles.

These are library resource policies, not maximum sizes prescribed by the ICC
specification. A profile's limits do not establish a whole-file memory bound.

## Implementation mapping and parity status

- Java: `Source/com/drew/metadata/icc/IccReader.java`.
- .NET: `MetadataExtractor/Formats/Icc/IccReader.cs`.

The former source-confirmed resource-policy gap was addressed by the merged Java
[parser-hardening work](https://github.com/drewnoakes/metadata-extractor/pull/758).
The shared limits were reviewed against Java `55c8e2cc` and .NET `658dc035`.
Do not treat that historical gap as outstanding or duplicate the landed fix.

No new same-input runtime comparison was performed for this documentation change;
do not infer parity for JPEG assembly, PNG decompression, dates, or descriptors.

## References

- [ICC specifications](https://www.color.org/icc_specs2.xalter)
- [ICC profile tag reference](https://exiftool.org/TagNames/ICC_Profile.html)
