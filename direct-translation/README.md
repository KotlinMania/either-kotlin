# Generated Serde replacements

The current adopted output is in `serde-production-01/`: the complete original
`serde_untagged.rs` and `serde_untagged_optional.rs` modules, plus the selected
Serde derive for Either from the complete `lib.rs`. The three generated Kotlin
files are copied byte-for-byte into production. `adoption.json` records paths
and hashes. `before/` retains the old hand implementations for ordinary diff.
Other `lib.rs` methods are not claimed generated or repaired.

The generator uses existing proc-macro2/Syn ASTs and serde-derive attribute
models. It never reads the old Kotlin code to determine output. Generic payload
codecs are explicit parameters; the existing dynamic `wrapSerialize` entry point
remains a compatibility adapter, not an equivalent Rust trait-bound system.

Validation:

- `serde-modules-07/validation.json`: five unchanged generated outputs compile;
  25 Rust/Kotlin protocol observations agree, including renamed/reordered fixtures.
- `serde-production-check-01/validation.json`: those 25 observations also agree
  against the actual production Either KLIB; two legacy Serialize checks pass.
- Normal offline JVM and macOS production compilation passes.
- Full JVM/macOS tests, including migrated JSON roundtrips, are blocked on local
  publication of Serde JSON 0.2.0 and its IndexMap 0.1.5 prerequisite. They have
  not passed yet. Other targets remain unverified.

The generator design and reproducible checks live in `../../rust-kotlin`:
`docs/either-serde-modules.md`, `tools/check_serde_modules.py`, and
`tools/check_serde_adoption.py`. Earlier candidate directories retain historical
attempts on disk. Intermediate executables, compiler outputs and Cargo build
caches are deliberately excluded from this checkpoint.

Known formatting limitation: the module emitter leaves whitespace on blank lines
and an extra final blank line. Raw and adopted files deliberately remain identical;
`git diff --check` flags these lines. Fix this in the emitter in a later increment,
not by hand-editing the adopted output. This does not affect the compile/protocol results.

## Local publication dependency repair

The Rust manifests distinguish library and development dependencies:

| Consumer | Dependency | Rust scope |
| --- | --- | --- |
| Either | serde_json | development/tests only |
| Itertools | Either | library |
| IndexMap | Itertools | development/tests only |
| Serde JSON | IndexMap | optional library dependency for `preserve_order` |

The Kotlin Serde JSON port currently always uses IndexMap, so removing that
edge without implementing feature selection would change the implementation.
Either's JSON tests use the source-declared dependency and must remain intact.

A forced `kotlinUpgradeYarnLock` dependency on main JS/Wasm compilation/resource
tasks caused publication to resolve `jsTestNpmAggregated`. Remove those artificial
main-task edges; retain the lock setup for tests. The ordinary offline
`publishToMavenLocal` then succeeds while Serde JSON 0.2.0 is still unavailable.
The JS test dry run continues to require Serde JSON, verifying tests were not
silently disabled. Published POMs contain no test-only dependencies.

Either's current public Kotlin API exposes Serde types. Its existing Serde
dependency therefore belongs in the commonMain **API** bundle, not the private
implementation bundle. An Itertools consumer compile exposed this metadata bug;
consumers should not need a direct Serde dependency to compensate. This corrects
publication metadata, not the documented conditional-trait adaptation.

Evidence is retained in
`../../automation-artifacts/2026-10-25-serde-dependency-cycle/`.
