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
