# MetadataExtractor Java library

Read `README.md` and `CONTRIBUTING.md` before changing this implementation. Follow
the existing Java code, tests, supported runtime and build configuration rather
than copying .NET-specific commands or conventions.

## Related repositories and shared guidance

The language implementations and sample corpus normally live in sibling checkouts,
not as submodules of one another:

| Checkout | Repository | Agent guidance |
| --- | --- | --- |
| `metadata-extractor` | [Java implementation](https://github.com/drewnoakes/metadata-extractor) | `.github/copilot-instructions.md` |
| `metadata-extractor-dotnet` | [C#/.NET implementation](https://github.com/drewnoakes/metadata-extractor-dotnet) | `.github/copilot-instructions.md` |
| `metadata-extractor-images` | [Shared sample corpus](https://github.com/drewnoakes/metadata-extractor-images) | `AGENTS.md` |

- Choose checkout/worktree paths explicitly. A sibling's default checkout is not
  necessarily the revision being investigated. Check its status and guidance before
  using it; do not switch, reset, clean, or overwrite another session's work.
- Before changing a format reader, tag directory, or descriptor, inspect the
  corresponding .NET code and tests for prior art, fixes and edge cases. .NET
  format implementations are in `MetadataExtractor/Formats/`, TIFF parsing is in
  `MetadataExtractor/Formats/Tiff/`, and tests are in `MetadataExtractor.Tests/`.
- Compare the same sample bytes when investigating cross-language differences.
  Neither implementation is automatically the oracle. Verify against the format
  specification and preserve each language's API and intentional behavior.
  Identify related fixes/test cases worth porting without mechanically copying
  whole files or expanding the requested scope.
- Keep sibling discovery, corpus safety, and regression-comparison guidance aligned
  across the three repositories. The images repo's `AGENTS.md` owns detailed corpus
  procedures; link to it rather than maintaining divergent copies. Keep build
  commands, test frameworks, API compatibility rules and code style specific to
  each language repository. Cross-repo discovery is not permission to commit or
  publish changes in another repository.

## Sample media corpus

- Use [drewnoakes/metadata-extractor-images](https://github.com/drewnoakes/metadata-extractor-images)
  for real-world samples and regression checks alongside this repo's test fixtures.
  Prefer an existing sibling checkout; clone a missing one only with approval.
- The corpus is multi-gigabyte (over 2 GB), so cloning or copying it can consume
  substantial disk space and bandwidth. Reuse existing inputs and avoid duplicating
  the whole corpus per worktree or validation run; prefer focused subsets or
  isolated input-only hard links.
- Read its `AGENTS.md`, `README.md` and the linked
  [working-with-test-images guide](https://github.com/drewnoakes/metadata-extractor/wiki/Working-with-test-images)
  before running corpus generators. Start with the affected format folder and
  representative unrelated controls.
- The Java corpus generator is
  `Source/com/drew/tools/ProcessAllImagesInFolderUtility.java`. Its `--text` mode
  writes under each input directory's `metadata/java` and may remove previous
  outputs. It has no independent output-root option: use fresh isolated input trees
  for baseline and changed runs, never the dirty corpus checkout. Hard-link inputs
  only, on the same volume, and never modify linked media.
- Cross-language orchestration lives in the images repo under
  `src/dotnet/MetadataExtractor.MediaLibraryProcessor/`. Inspect `Program.cs`,
  `Java/JavaRunner.cs`, `DotNet/DotNetRunner.cs`, and `DiffRunner.cs` before use;
  paths, enabled runners and runtime assumptions can be local or stale.
- `metadata/diff` compares Java with .NET, not baseline with changed Java. Compare
  fresh runs with the same input set, Java executable, dependencies and runtime
  settings. Preserve raw outputs and inspect directory errors, exceptions, missing
  output and value/hierarchy changes; process exit status alone is insufficient.
- Keep manifests, logs and comparison reports in session artifacts. Do not
  regenerate tracked snapshots merely to remove historical output noise, and
  preserve existing staged, unstaged, untracked, deleted and submodule changes.
