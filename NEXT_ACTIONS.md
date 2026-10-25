# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/5 (100.0%)
- **Function parity:** 91/146 matched (target 205) — 62.3%
- **Class/type parity:** 3/8 matched (target 15) — 37.5%
- **Combined symbol parity:** 94/154 matched (target 220) — 61.0%
- **Average inline-code cosine:** 0.07 (function body across 5 matched files)
- **Average documentation cosine:** 0.46 (doc text across 5 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 5 files with <0.60 function similarity
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

### 2. lib

- **Target:** `either.Either`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 390309.4
- **Functions:** 64/100 matched (target 132)
- **Missing functions:** `cloned`, `copied`, `from`, `poll`, `read`, `read_exact`, `read_to_end`, `read_to_string`, `fill_buf`, `consume`, `read_until`, `read_line`, `write`, `write_all`, `write_fmt`, `flush`, `as_ref`, `as_ref`, `as_mut`, `as_mut`, `deref_mut`, `source`, `description`, `cause`, `fmt`, `write_str`, `write_char`, `write_fmt`, `deref`, `seek`, `_unsized_ref_propagation`, `check_array_ref`, `check_array_mut`, `propagate_array_ref`, `propagate_array_mut`, `_unsized_std_propagation`
- **Types:** 1/3 matched (target 9)
- **Missing types:** `Output`, `Target`
- **Tests:** 5/7 matched

### 3. iterator

- **Target:** `either.IterEither`
- **Similarity:** 0.03
- **Dependents:** 0
- **Priority Score:** 204209.7
- **Functions:** 21/40 matched (target 23)
- **Missing functions:** `next`, `size_hint`, `fold`, `for_each`, `count`, `last`, `nth`, `collect`, `partition`, `all`, `any`, `find`, `find_map`, `position`, `next_back`, `nth_back`, `rfold`, `rfind`, `len`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Item`

### 4. serde_untagged_optional

- **Target:** `either.SerdeUntaggedOptional`
- **Similarity:** 0.04
- **Dependents:** 0
- **Priority Score:** 10309.6
- **Functions:** 2/2 matched (target 25)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 2)
- **Missing types:** `Either`

### 5. serde_untagged

- **Target:** `either.SerdeUntagged`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 10309.4
- **Functions:** 2/2 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 2)
- **Missing types:** `Either`

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

