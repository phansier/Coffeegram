# Swift Export status

## 24.07.2026
kotlin = "2.4.20-Beta2"
jetbrainsCompose = "1.11.1"

## Fixed [KT-86463](https://youtrack.jetbrains.com/issue/KT-86463/)

## Current blocker (unresolved, upstream)
`compileSwiftExportMainKotlinIosSimulatorArm64` fails with argument type mismatches wherever a
generic type is used with a concrete type argument in an exported public declaration:

```
PersistentMap<Any?, Any?> vs PersistentMap<Int, Picture>   (MonthTableScreenState.filledDayItemsMap)
Storage<Any> vs Storage<ThemeState>                        (ThemeStore constructor)
DataStore<Any?> vs DataStore<Preferences>                  (createDataStore)
Array<Any?>? vs Array<FontFeature>?                         (Skiko, third-party)
```

Swift Export's generated bridge code casts the value to the generic type's erased upper bound
(`Any?`) instead of the concrete type argument, then passes that into a call expecting the
concrete type — a compile error in the generated glue, not in our code.

This is a confirmed, open JetBrains bug:
- [KT-85981](https://youtrack.jetbrains.com/issue/KT-85981) — exact match, generic property with
  specified type generates uncompilable bridges. Status: Submitted, no workaround, no fix version.
- [KT-82103](https://youtrack.jetbrains.com/issue/KT-82103) — same root cause for `Lazy<T>`.
  Severity: Major, first reported on Kotlin 2.3.0-Beta2, still present in our current
  2.4.20-Beta2.

The 4th error (`Array<FontFeature>` in Skiko) is inside JetBrains' own Compose/Skia bindings,
transitively bridged by Swift Export — not fixable from this repo even if the first three were
worked around.
