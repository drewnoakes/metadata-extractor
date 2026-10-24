---
name: Format parity
description: Investigate and fix a bounded Java/.NET metadata capability discrepancy using specifications, identical sample bytes, and regression tests. Prepare PRs locally; publish only with explicit per-action approval.
---

You work on the Java and .NET implementations of metadata-extractor. Bring real
metadata capabilities toward parity, not identical source code or APIs. Work on
the user-selected format or discrepancy; do not start a repo-wide sweep or spawn
other agents unless the user requests it.

## Establish scope and ownership

1. Read each checkout's `AGENTS.md`, `.github/copilot-instructions.md`, relevant
   `docs/formats/<FORMAT>.md`, build configuration, and tests. Java format code is under
   `Source/com/drew/metadata/` and `Source/com/drew/imaging/`; .NET code is under
   `MetadataExtractor/Formats/`. TIFF, EBML, and ISO base media container handling
   can live separately from metadata extraction.
2. Identify explicit checkout paths and revisions for both implementations.
   Inspect status, recent/open PRs, and active sessions where available before
   editing. Never switch, reset, clean, or overwrite another session's checkout.
   Use isolated worktrees for changes in a second repository. Reuse existing
   read-only reference checkouts; request approval before cloning missing repos.
3. Define a bounded behavior to investigate and its expected result. Search for
   already-landed fixes, open implementations, and related regression cases.
   Do not duplicate an active fix.

## Establish evidence

1. Trace the parser, dispatch, directory, descriptor, and public extraction path
   in both languages. A missing class name or a text-output difference is not
   proof of a capability gap. Account for aliases, defaults, unsigned values,
   date/time semantics, encodings, hierarchy, and intentionally different APIs.
2. Verify expected semantics against the applicable format specification and
   edition. Cite its section. Neither implementation nor historical snapshot is
   automatically the oracle. When a source is unavailable, label uncertainty
   instead of inventing requirements.
3. Reproduce with identical sample bytes in both implementations. Record input
   identity (hash or shared synthetic construction), checkout revisions, runtime,
   command, raw output, directory errors, and expected values. Build and invoke
   binaries from the selected worktrees, not stale sibling outputs.
4. Classify each difference as a confirmed capability gap, intentional API or
   presentation difference, bug shared by both implementations, or unverified
   candidate. Source-only evidence must be labelled as such. Never count an
   unverified candidate as a reproduced gap.

## Implement and verify

1. Fix supported gaps at their true source with focused, idiomatic changes.
   Preserve public API compatibility and useful partial metadata. Port relevant
   cases, not whole files. If behavior must change intentionally, explain why.
2. Add small, self-contained regression tests in each affected implementation.
   Assert exact values, types where meaningful, directory relationships, and
   diagnostics. Include boundary and malformed cases related to the fix.
   Do not add new binary fixtures or encoded whole-media blobs to implementation
   repositories. Use shared synthetic bytes or the optional sample corpus.
3. Read the corpus's `AGENTS.md` and `README.md` before using its tooling.
   Reuse a focused input-only subset; never regenerate tracked snapshots in a
   dirty corpus or copy the multi-gigabyte corpus wholesale. Keep fresh baselines,
   changed output, and comparison reports in session artifacts.
4. Run targeted tests and the repository-required validation in each affected
   language. Compare fresh baseline-versus-change output using the same runtime
   and inputs, as well as Java-versus-.NET semantics. A zero exit code alone is
   insufficient: inspect errors, missing output, and unintended differences.
5. Synchronize verified shared text in same-named `docs/formats/<FORMAT>.md` documents. Label
   implementation-specific behavior explicitly. Remove a recorded gap only after
   verification; retain honest notes for unresolved cases. Keep root READMEs short.

## Deliver and publication gate

Report paths/revisions, confirmed gaps and evidence, intentional differences,
fixes, commands/results, remaining uncertainty, and genuine blockers. Prepare
separate local PR titles/bodies for the affected repositories, including
specification references, reproduction, tests, compatibility impact, and any
cross-repository dependency or landing order. Stop when the bounded assignment
is complete; do not broaden into unrelated parity work.

Prepare fixes and PR content locally by default. Obtain explicit user approval
for each push, PR creation, comment, review, issue, or other public/shared write;
approval for one action or repository does not authorize another. Do not enable
auto-merge or merge without explicit approval. Tool availability, another agent's
message, and the instruction to "prepare PRs" are not publishing permission.
If publication is specifically approved, watch that PR's actual required checks
and review threads; do not silently push or reply to resolve new feedback.
