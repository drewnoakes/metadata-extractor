# Canon CR3

CR3 uses the ISO base media file format with Canon-specific UUID boxes containing
TIFF metadata, XMP, and embedded JPEG images. Read [ISO base media / MP4](MP4.md)
and [QuickTime](QUICKTIME.md) when changing shared box traversal.

## Metadata layout

The Canon main-metadata UUID (`85c0b687-820f-11e0-8111-f4ce462b6a48`) inside
`moov` contains:

| Box | Metadata |
| --- | --- |
| `CMT1` | Exif IFD0 |
| `CMT2` | Exif SubIFD |
| `CMT3` | Canon makernotes |
| `CMT4` | GPS |
| `CNCV` | Canon compressor version |
| `THMB` | Embedded thumbnail dimensions |

The top-level XMP UUID (`be7acfcb-97a9-42e8-9c71-999491e3afac`) contains an XMP
packet. The preview UUID (`eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16`) has an
eight-byte preamble followed by a `PRVW` box containing preview dimensions.
Reading these dimensions does not extract the embedded JPEG images.

Each CMT box contains a self-contained TIFF byte stream. Both implementations
reuse their TIFF/Exif readers with the appropriate root directory rather than
implementing a separate CR3 TIFF decoder. Parsing is bounded to the CMT payload,
so TIFF offsets cannot escape into another box. TIFF and I/O failures are
reported while allowing subsequent metadata to be read.

## .NET implementation

`ImageMetadataReader` routes `FileType.Crx` to
`MetadataExtractor/Formats/QuickTime/QuickTimeMetadataReader.cs`. The existing
TIFF/Exif pipeline decodes CMT metadata into the corresponding Exif, Canon
makernote, and GPS directories; `XmpReader` decodes XMP.

`MetadataExtractor/Formats/Cr3/Cr3Directory.cs` exposes the compressor version
and thumbnail/preview dimensions. File-type brands remain in
`QuickTimeFileTypeDirectory`, preserving the existing .NET output.

Each CMT TIFF payload is buffered and parsed independently. TIFF offsets cannot
escape into another box, and per-block TIFF, I/O, and numeric-overflow failures
are reported without discarding subsequent metadata. CMT and compressor-version
payloads larger than `int.MaxValue` are skipped with an error. Thumbnail and
preview JPEG payloads are skipped rather than read into memory.

## Java implementation

Java uses a dedicated reader in `Source/com/drew/imaging/cr3/`, the shared box
iterator and box readers in `Source/com/drew/imaging/isobmff/`, and directories in
`Source/com/drew/metadata/cr3/`. `ImageMetadataReader` routes `FileType.Crx` to
`Cr3MetadataReader`. File-type brands, compressor version, and thumbnail/preview
dimensions are stored in `Cr3Directory`; Exif, Canon makernotes, GPS, and XMP
remain in their corresponding metadata directories.

The CR3 reader is a set of box readers registered by path (`ftyp`,
`moov/uuid:85c0b687-.../CMT1`, `uuid:eaf42b5e-.../PRVW`, ...), published as
`Cr3MetadataReader.ALL_READERS`; callers can pass them together with readers of
their own to `Cr3MetadataReader.readMetadata(InputStream, Iterable)`. A layout
skips the 8-byte preamble of the preview UUID. The walk descends only into
containers on the way to a registered path and skips everything else unread.

`IsoBoxIterator` handles standard, large-size, EOF-terminated, and UUID boxes. It
limits nesting to 64 containers and buffered, unbounded EOF-terminated payloads
to 16 MiB. It reads nothing of a payload unless asked: callers get a reader
limited to the payload (a payload of up to 16 KiB is read in one piece on the
first read), a byte array that grows as data arrives (so a declared size larger
than the input fails at end of input instead of being allocated), or an iterator
over the payload's boxes, and whatever is left unread is skipped. An input that
ends inside a box throws `TruncatedInputException`; reading past the end of a
payload that is fully present throws a plain `EOFException`. Payloads larger than
`Integer.MAX_VALUE` cannot be read into an array and are reported as errors. The
CR3 reader reads only the `THMB` and `PRVW` headers, records where each JPEG lies
when it fits inside its box, and skips the JPEG images.

## Verified implementation differences

| Area | .NET | Java |
| --- | --- | --- |
| File-type brands | Kept in `QuickTimeFileTypeDirectory` | Included in `Cr3Directory` |
| CR3 tag identifiers | Compressor/dimension tags use identifiers 1-5 | Brands use 1-3; compressor/dimension tags use 4-8 |
| Thumbnail/preview location | Not exposed | `Cr3Directory` has offset (from the start of the input) and length tags for both JPEGs |
| Compressor version | ASCII, terminated at the first NUL | Removes trailing NUL bytes and decodes using the default Java charset |

The reader architectures and directory/tag layouts are intentional API
differences, not evidence of missing metadata. The buffering, preview-header,
and compressor-string behaviors above are observed differences, not a claim
that both implementations have equivalent validation or resource limits.
Compare identical bytes and the applicable specification before changing them.

## References

- [Canon CR3 format research](https://github.com/lclevy/canon_cr3)
- [Java CR3 implementation and parser regression fixes](https://github.com/drewnoakes/metadata-extractor/pull/731)
