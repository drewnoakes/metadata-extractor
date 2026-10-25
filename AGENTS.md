# Format documentation

Before changing a reader, directory, descriptor, or format-specific test, read
the relevant `docs/formats/<FORMAT>.md` and its same-named counterpart in the
.NET implementation. Check related container and metadata format documents too.
Read [.github/copilot-instructions.md](.github/copilot-instructions.md) for
Java, sibling-checkout, and corpus guidance.

Keep format-specific behavior, limits, references, and implementation details in
the relevant `docs/formats/<FORMAT>.md`, not the top-level README. The README is a
quick overview of general usage and supported formats. Java `package-info.java`
should retain package declarations, annotations, licensing, useful API summaries,
and Javadoc entry points linking to the format document, not duplicate detailed
prose. Use the same path and uppercase format filename in Java and .NET,
independent of their source package layouts.

Keep verified format facts and shared behavior identical in both documents.
Clearly separate Java-only and .NET-only behavior, intentional API differences,
confirmed capability gaps, and questions not yet verified. Do not copy a claim
of support from one implementation to the other without checking code and tests.
Update both documents when verified behavior changes; aim to eliminate genuine
capability gaps rather than conceal them in documentation.

## Format documentation index

| Format | Documentation |
| --- | --- |
| Canon CR3 | [CR3.md](docs/formats/CR3.md) |
| EBML detection | [EBML.md](docs/formats/EBML.md) |
| HEIF / HEIC / AVIF references | [HEIF.md](docs/formats/HEIF.md) |
| ICC profiles | [ICC.md](docs/formats/ICC.md) |
| ISO base media / MP4 | [MP4.md](docs/formats/MP4.md) |
| Matroska / WebM | [MKV.md](docs/formats/MKV.md) |
| QuickTime | [QUICKTIME.md](docs/formats/QUICKTIME.md) |

This is an initial migration of substantive package documentation, not an
exhaustive support matrix. Formats with only short package summaries do not need
empty documents. Review code, tests, and specifications before adding claims.

## Focused custom agents

- [Format parity](.github/agents/format-parity.agent.md): investigate and fix a
  bounded cross-language discrepancy using specifications and identical inputs.
- [Parser value validation](.github/agents/parser-value-validation.agent.md):
  systematically trace input-controlled values and harden parser error handling
  and resource bounds within an explicitly selected scope.

Both agents prepare changes and PR content locally. Obtain explicit per-action
user approval before any push, PR creation, comment, or other shared write.

# Test assets

Do not add binary files or image fixtures to this repository, including under
`Tests/Data`. All new image and media samples belong in the optional
[metadata-extractor-images repository](https://github.com/drewnoakes/metadata-extractor-images),
where they can be used for regression testing across implementations without
increasing this repository's Git history or GitHub Actions clone size.

Keep unit tests self-contained using small, synthetic byte arrays or buffers
constructed in code. Do not embed whole image files as encoded strings or byte
arrays, and do not make unit tests or GitHub Actions clone or download the image
corpus. Use the separate corpus for image-based regression testing.

When adding samples to the image corpus, include a historical reference in the
commit message or accompanying documentation: link to the relevant pull request,
issue, or commit, identify the sample filenames, and explain what behavior or
regression they exercise. Use full URLs so references remain unambiguous across
repositories.
