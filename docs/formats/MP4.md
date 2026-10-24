# ISO base media / MP4

MP4 uses a hierarchy of boxes. Boxes share a size/type header; their remaining
layout depends on the box type. A box may contain data or child boxes. Metadata
interpretation can depend on the enclosing handler, so a matching four-character
code alone is not enough to identify a payload's meaning.

## Implementation mapping

- Java: `Source/com/drew/imaging/mp4/Mp4Reader.java` traverses boxes using handlers
  in `Source/com/drew/metadata/mp4/`. The handler-based implementation chooses
  which boxes and containers to read; handler context controls media parsing.
- .NET: MP4 metadata is read by
  `MetadataExtractor/Formats/QuickTime/QuickTimeMetadataReader.cs`.
  `MetadataExtractor/Formats/Iso14496/` contains box infrastructure used by the
  HEIF reader. It is not a separate equivalent of Java's MP4 metadata package.

This is an intentional implementation-layout difference, not evidence that a
particular metadata feature is missing. Supported tags, media properties, box
boundary handling, and resource limits must be compared individually.
No exhaustive MP4 feature-parity check is implied by this documentation migration.

## References

- ISO/IEC 14496-12:2015, as cited by the original Java package documentation:
  [historical download](http://standards.iso.org/ittf/PubliclyAvailableStandards/c068960_ISO_IEC_14496-12_2015.zip)
  and [publicly available standards index](https://standards.iso.org/ittf/PubliclyAvailableStandards/index.html).
  Check which edition applies to the feature under investigation.
- [QuickTime media data structures](https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html)
  provide related background, not a replacement for MP4's specification.
