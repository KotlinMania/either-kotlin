# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/5 (40.0%)
- **Function parity:** 23/46 matched (target 25) — 50.0%
- **Class/type parity:** 2/5 matched (target 2) — 40.0%
- **Combined symbol parity:** 25/51 matched (target 27) — 49.0%
- **Average inline-code cosine:** 0.11 (function body across 2 matched files)
- **Average documentation cosine:** 0.50 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. into_either

- **Target:** `either.IntoEither`
- **Similarity:** 0.18
- **Dependents:** 1
- **Priority Score:** 1000308.2
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 2. iterator

- **Target:** `either.IterEither`
- **Similarity:** 0.03
- **Dependents:** 0
- **Priority Score:** 204209.7
- **Functions:** 21/40 matched (target 23)
- **Missing functions:** `next`, `size_hint`, `fold`, `for_each`, `count`, `last`, `nth`, `collect`, `partition`, `all`, `any`, `find`, `find_map`, `position`, `next_back`, `nth_back`, `rfold`, `rfind`, `len`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Item`

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

