# Matroska / WebM

Matroska and WebM use EBML encoding. Shared support includes EBML header and
segment fields, track properties, video colour information, and structured tags.

## Tag structure

Each Tags update has a `MkvTagsDirectory`, each target group a `MkvTagDirectory`,
and each SimpleTag a `MkvSimpleTagDirectory`. Directory parent links preserve
targets and nesting. Repeated tag names, language variants, and binary values
remain separate directories. Targets may occur before or after values.

Media packets are not decoded. Chapter and attachment contents are not extracted,
but tag target identifiers referring to them are retained.

## Parsing and limits

Both implementations traverse forward-only using 64-bit element sizes, including
unknown-sized Segments and Clusters. Parsing errors are recorded on metadata
directories while retaining metadata already extracted.

Individual text and binary metadata values have different source-verified
resource ceilings at the review baselines (.NET `658dc035`, Java `55c8e2cc`):
.NET's `MkvReader.Parser` limits them to 16 MiB, whereas Java's
`DataParser.getByteCount` rejects only sizes outside `0..Integer.MAX_VALUE`.
This is a resource-policy discrepancy for the hardening agent to assess, not an
intentional API difference or a runtime exploitability finding. No large input
was allocated to demonstrate it. Select a justified common policy and add
small synthetic boundary tests before changing behavior.

Neither per-value ceiling is a whole-document memory bound: many small values
or directories still require separate resource analysis.

## Implementation mapping and parity status

- Java: `Source/com/drew/metadata/mkv/`.
- .NET: `MetadataExtractor/Formats/Mkv/`.
- File type probing is documented separately in [EBML detection](EBML.md).

The shared behavior above is source-reviewed. It does not establish identical
handling of every element, malformed file, default, or descriptor. Java numeric
representations and .NET unsigned types need not be identical to preserve values;
compare their semantics rather than API names or formatted output alone.

## References

- [EBML specification, RFC 8794](https://www.rfc-editor.org/rfc/rfc8794)
- [Matroska element definitions](https://www.matroska.org/technical/elements.html)
