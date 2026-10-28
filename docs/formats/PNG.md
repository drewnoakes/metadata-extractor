# PNG

PNG metadata is stored in ancillary chunks. The `iCCP`, `zTXt` and compressed
`iTXt` chunks hold zlib streams whose inflated length is not declared anywhere
in the file. Deflate can expand data by more than 1000:1, so a chunk's
compressed length does not bound the memory needed to inflate it.

## Java decompression limit

Java inflates at most 16 MiB in total from the `iCCP`, `zTXt` and `iTXt` chunks
of a single PNG. The budget is shared, so repeated compressed chunks cannot each
inflate up to the limit. A chunk that exceeds the remaining budget produces an
error instead of its text or profile data: in the PNG directory for `zTXt` and
`iTXt`, or in the ICC directory for `iCCP`. Other chunks, including uncompressed
`tEXt`, are still read. The [ICC profile limits](ICC.md) apply as well.

This is a library resource policy, not a limit from the PNG specification.
libpng's default `PNG_USER_CHUNK_MALLOC_MAX` bounds the decompressed size of a
single ancillary chunk to 8,000,000 bytes.

## Implementation mapping and parity status

- Java: `Source/com/drew/imaging/png/PngMetadataReader.java`.
- .NET: `MetadataExtractor/Formats/Png/PngMetadataReader.cs`.

.NET has no equivalent limit: `TryDeflate` copies the whole inflated `zTXt` or
`iTXt` stream into memory, and `iCCP` is bounded only by the ICC profile limits.
This is a confirmed capability gap, source-reviewed against .NET `cb71a8ad`. No
same-input runtime comparison was performed.

## References

- [PNG specification](https://www.w3.org/TR/png/)
- [libpng: defending against decompression bombs](https://libpng.sourceforge.io/decompression_bombs.html)
- [Issue #379: PNG decompression bombs](https://github.com/drewnoakes/metadata-extractor/issues/379)
