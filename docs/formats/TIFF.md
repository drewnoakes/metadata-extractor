# TIFF

TIFF IFD entries store values longer than four bytes (eight in BigTIFF) at an
offset elsewhere in the data. Nothing stops several entries from pointing at the
same bytes, and each value is copied out separately, so without a limit the
memory used grows with the number of entries times the size of the shared value
rather than with the size of the data. The same structure is used by Exif in
JPEG, PNG, HEIF and WebP, and by TIFF-based raw formats.

## Java tag value limit

Within one `TiffReader.processTiff` call, the values of tags that are not IFD
pointers may total at most four times the length of the TIFF data. Makernote and
other IFDs that handlers read during the call share this limit. Tags that
contain IFDs, such as makernotes, are counted as well as the values inside them.
The first value that does not fit adds an error to the current directory. It and
any later values that do not fit are skipped, while IFD traversal and smaller
values continue. Each Exif segment, and each TIFF embedded in another format
(for example Exif in Photoshop resources or a Panasonic `JpgFromRaw` preview),
is a separate call with its own limit.

This is a library resource policy, not a limit from the TIFF specification. The
largest total among the TIFF data read from this repository's `Tests/Data`
samples is 1.34 times the data length.

## Implementation mapping and parity status

- Java: `Source/com/drew/imaging/tiff/TiffReader.java`.
- .NET: `MetadataExtractor/Formats/Tiff/TiffReader.cs`.

.NET has no equivalent limit: `ProcessIfd` checks each value against the reader
length only, so repeated references to the same bytes are each copied. This is a
confirmed capability gap, source-reviewed against .NET `cb71a8ad`. No same-input
runtime comparison was performed.
