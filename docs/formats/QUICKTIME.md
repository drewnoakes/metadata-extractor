# QuickTime

QuickTime files contain a hierarchy of atoms. Atoms share a size/type header,
but their payloads depend on the atom type. An atom may be a leaf or a container.
Handler context matters: atoms with the same name may need different decoding
depending on their enclosing media or metadata handler.

## Implementation mapping

- Java: `Source/com/drew/imaging/quicktime/` traverses atoms using handlers in
  `Source/com/drew/metadata/mov/`, including the `media` and `metadata` packages.
  The handler-based implementation selects how nested atoms are interpreted.
- .NET: `MetadataExtractor/Formats/QuickTime/` uses callbacks for traversal and
  metadata extraction. This reader also handles MP4 metadata.

The Java handler architecture is not a requirement for the .NET implementation.
Shared container terminology does not imply equivalent supported tags or media
properties. Compare those individually against the specification and identical
inputs before claiming a capability gap.

## References

- [QuickTime file format specification](https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/)
- [Media data structures](https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html)
- [Metadata atoms](https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/Metadata/Metadata.html)

[ISO base media / MP4](MP4.md) describes the related box format and the different
Java/.NET package layouts.
