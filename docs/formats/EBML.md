# EBML detection

EBML is a container encoding, not a synonym for Matroska. The supported Matroska
and WebM schemas are selected using the EBML header's `DocType`.

## Shared behavior

Both implementations probe at most 65,536 bytes of the header. A `DocType` of
`matroska` selects Matroska and `webm` selects WebM. An unsupported or unresolved
document type remains generic EBML; detection does not imply that its schema
can be extracted. See [Matroska / WebM](MKV.md) for extraction behavior.

## Implementation mapping

- Java: `Source/com/drew/imaging/ebml/EbmlTypeChecker.java`; the stream overload
  requires mark/reset support and restores the input position after probing.
- .NET: `MetadataExtractor/Formats/Ebml/EbmlTypeChecker.cs`; the checker restores
  the stream position after probing.

These stream APIs differ intentionally; this is not a metadata capability gap.
The shared behavior above is source-reviewed, not a claim of exhaustive parity
for all malformed inputs.

## References

- [EBML specification, RFC 8794](https://www.rfc-editor.org/rfc/rfc8794)
- [Matroska element definitions](https://www.matroska.org/technical/elements.html)
