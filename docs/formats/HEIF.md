# HEIF / HEIC / AVIF

HEIF uses the ISO base media file format. Read [ISO base media / MP4](MP4.md)
as well when changing shared box parsing.

## References

The Java package documentation previously linked this background resource:

- [HEIF thesis (historical repository URL)](https://dspace.cc.tut.fi/dpub/bitstream/handle/123456789/24147/heikkila.pdf?sequence=1)
- [HEIF format overview](https://nokiatech.github.io/heif/)

The historical link is retained for provenance; its availability and contents
have not been revalidated. It is not a substitute for the relevant specification.

## Implementation mapping and parity status

- Java: `Source/com/drew/metadata/heif/`.
- .NET: `MetadataExtractor/Formats/Heif/` and
  `MetadataExtractor/Formats/Iso14496/`.

This document migrates a reference, not a verified feature inventory. Do not
infer equivalent item, property, Exif, XMP, thumbnail, or transformation support
from the shared format name. Compare the same sample bytes and the applicable
specification before recording a capability gap or claiming parity.
