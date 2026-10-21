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
