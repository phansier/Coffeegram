# Swift Export status

## 22.10.2026 (Kotlin 2.5.0-Beta2)
kotlin = "2.5.0-Beta2"
jetbrainsCompose = "1.12.2"
serialization = "1.12.0-RC"

✅ **Swift Export works end to end**: `make buildIos` ends with `** BUILD SUCCEEDED **`, and the app
launches on the iOS simulator (also from the Android Studio KMP plugin) showing the Compose UI via
`import CmpCommon` → `MainViewController()`.

Note: `make compileSwiftExport` only covers the Kotlin bridge compilation — the real check is
`make buildIos` (`embedSwiftExportForXcode`).

| Task | Beta1 | Beta2 |
|---|---|---|
| `compileSwiftExportMainKotlinIosSimulatorArm64` | ✅ | ✅ |
| `iosSimulatorArm64DebugBuildSPMPackage` | ❌ KT-85660 | ✅ (ui-graphics `HIDDEN`) |
| `linkSwiftExportBinaryDebugStaticIosSimulatorArm64` | ❌ KT-89699 | ✅ |
| `embedSwiftExportForXcode` + Xcode app link | — | ✅ |

### Fixed upstream
- [KT-89699](https://youtrack.jetbrains.com/issue/KT-89699) — the `Density.roundToPx` reverse-bridge
  crash is gone; `linkSwiftExportBinaryDebugStaticIosSimulatorArm64` produces
  `libSwiftExportBinary.a`.

### Migrated to `export { swift { } }`
`swiftExport { }` is deprecated (error in Kotlin 2.5.20, removed in 2.6.0,
https://kotl.in/swift-export-dsl-migration). Current config in `cmp-common/build.gradle.kts`:
```kotlin
export {
    swift {
        moduleName = "CmpCommon"
        rootPackage = "ru.beryukhov.coffeegram"
        xcodeIntegration {
            configure(libs.decompose.core, SwiftExportVisibility.HIDDEN)
            configure(libs.essenty, SwiftExportVisibility.HIDDEN)
            configure(libs.koin.core, SwiftExportVisibility.HIDDEN)
            configure("org.jetbrains.compose.ui:ui-graphics", SwiftExportVisibility.HIDDEN)
        }
    }
}
```
- `flattenPackage` → `rootPackage`.
- `xcodeIntegration()` registers `embedSwiftExportForXcode` and its pipeline; the legacy DSL activated
  it implicitly.
- The new DSL fully exports (`EXPOSED`) every direct `api` dependency by default. Decompose's
  generated bridges then fail to compile (`Argument type mismatch`, `Overload resolution ambiguity`
  on `PanelsNavigator.navigate`), so the `api` dependencies are `HIDDEN`: only empty stubs for the
  types `CmpCommon` refers to. Swift only needs `MainViewController()`.

### KT-85660 avoided via `HIDDEN` (unresolved upstream)
`iosSimulatorArm64DebugBuildSPMPackage` failed on the generated Swift for Compose UI graphics:
```
OrgJetbrainsComposeUiUiGraphics.swift:4467:21: error: type member must not be named 'Type',
since it would conflict with the 'foo.Type' expression
```
[KT-85660](https://youtrack.jetbrains.com/issue/KT-85660) (`PathSegment.Type`).

Marking the transitive `org.jetbrains.compose.ui:ui-graphics` as `HIDDEN` drops `PathSegment`
entirely (generated `OrgJetbrainsComposeUiUiGraphics.swift`: 6677 → 136 lines). This replaced an
earlier text-patch workaround (a `doLast` on `*GenerateSPMPackage` renaming the enum to `Type_`).

### Xcode project changes (`cmp-iosApp/iosApp.xcodeproj`)
- Removed `-framework cmp_common` from `OTHER_LDFLAGS` (Debug and Release) — it failed with
  `ld: framework 'cmp_common' not found`; Swift Export links `libCmpCommon.a` itself via autolink.
- "Build cmp_common KMP Framework" phase no longer skips Gradle when
  `OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES`. The Android Studio KMP plugin sets it and pre-builds the
  old `cmp_common.framework` instead, so its builds failed with
  `Unable to resolve module dependency: 'CmpCommon'`.
- Removed the "Sync Compose Resources" phase: `embedSwiftExportForXcode` already runs
  `syncSwiftExportBinaryComposeResourcesForIos`, which copies `compose-resources` into the app.
- The remaining script phase has "Based on dependency analysis" unchecked (`alwaysOutOfDate = 1`);
  Gradle handles up-to-date checks.

### Remaining notes
- `ARCHS=arm64` is required (no `iosX64` target): a generic simulator build also requests x86_64,
  which fails `validateArchitecturesForEmbedSwiftExportForXcode` (`ios_x64` not configured) and
  `syncSwiftExportBinaryComposeResourcesForIos` (`Unknown iOS simulator arch: 'x86_64'`).
- `make buildIos` exhausts the default Gradle daemon heap (2.6 GiB reported) during the link step;
  the build continues, but the daemon is stopped afterwards.
- Harmless warnings: `libicu` object built for iOS-simulator 18.5 while the app targets 18.0;
  `-[UIApplication statusBarStyle]` deprecation assert at runtime on iOS 27 (not from our code).
- Only the first screen was checked at runtime; other tabs not exercised yet.

## 06.10.2026 (Kotlin 2.5.0-Beta1)
kotlin = "2.5.0-Beta1"
jetbrainsCompose = "1.12.1"
serialization = "1.12.0-RC"

KT-87875 (`ColorSpace.fromXyz` vtable crash) and KT-87791 (kotlinx-serialization `@_spi`
redeclarations) are gone. Android `make buildApp` still passes (KSP 2.3.12).

### Current blockers (unresolved, upstream, both in Compose UI modules)
1. `iosSimulatorArm64DebugBuildSPMPackage` — generated Swift for Compose UI graphics:
   ```
   OrgJetbrainsComposeUiUiGraphics.swift: error: type member must not be named 'Type',
   since it would conflict with the 'foo.Type' expression
   ```
   Caused by the nested enum `PathSegment.Type`.
   [KT-85660](https://youtrack.jetbrains.com/issue/KT-85660) — exact match (points at
   `PathSegment.kt`). Status: Submitted, affects 2.5.0-Beta1, no fix version.
2. `linkSwiftExportBinaryDebugStaticIosSimulatorArm64` — Kotlin/Native backend crash on reverse
   bridges for interface member extensions (Compose `Density`: `fun Dp.roundToPx(): Int`):
   ```
   IllegalStateException: Cannot bind FUN name:androidx_compose_ui_unit_Density_roundToPx__..._reverse
   (self:Density, receiver:Dp) returnType:kotlin.Int to 'roundToPx'
   ```
   [KT-89699](https://youtrack.jetbrains.com/issue/KT-89699) — exact match (same `Density`
   example). Status: Submitted, affects 2.5.0-Beta1, no fix version. Not avoided by
   `kotlin.native.cacheKind=none` / `kotlin.incremental.native=false`.
   Run separately (`make buildIos` stops at blocker 1 first). Needs a bigger daemon heap than the
   default, otherwise the daemon dies with "JVM garbage collector is thrashing":
   `-Dorg.gradle.jvmargs="-Xmx8g -XX:MaxMetaspaceSize=1g"`.

## 06.10.2026 (Kotlin 2.4.20)
kotlin = "2.4.20"
jetbrainsCompose = "1.12.1"
serialization = "1.12.0-RC"

Reproduce: `make compileSwiftExport` (Kotlin bridge compilation) or `make buildIos` (full Xcode build).

### Fixed in our code: generic-typed parameters
`compileSwiftExportMainKotlinIosSimulatorArm64` now passes. The KT-85981 / KT-82103 bridge bug
only hits *parameters* of exported declarations whose type is a generic with concrete arguments
(return types and properties are fine, `@Composable` functions are not exported). Removed them:
- `MonthTableScreenState.filledDayItemsMap: PersistentMap<Int, Picture>` → `FilledDayItems`
  wrapper with an `internal` constructor
- `ThemeStore(Storage<ThemeState>)` → non-generic `ThemeStorage : Storage<ThemeState>`
- `ThemeDataStorePrefStorage(DataStore<Preferences>)` → `internal` constructor,
  created via `Scope.themeDataStorePrefStorage()`

The Skiko `Array<FontFeature>` error no longer appears.

### Blockers (upstream, fixed in Kotlin 2.5.0-Beta1)
`make buildIos` fails further down, in modules Swift Export pulls in transitively because their
types are reachable from `cmp-common`'s public API:

1. `linkSwiftExportBinaryDebugStaticIosSimulatorArm64` — Kotlin/Native backend crash while
   generating reverse bridges for Compose UI graphics (`OrgJetbrainsComposeUiUiGraphics.kt`):
   ```
   IllegalArgumentException: FUN name:fromXyz ... (<this>:ColorSpace, x, y, z): FloatArray
   is not found in vtable of CLASS name:ColorSpace modality:ABSTRACT
   ```
   [KT-87875](https://youtrack.jetbrains.com/issue/KT-87875) — reverse bridges crash when a class
   mixes a final overload with open/abstract ones (`ColorSpace.fromXyz(x, y, z)` vs abstract
   `fromXyz(FloatArray)`). Fixed, available in 2.5.0-Beta1.
2. `iosSimulatorArm64DebugBuildSPMPackage` — generated Swift for kotlinx-serialization-core does
   not compile: each `@ExperimentalSerializationApi` interface method with a default body gets
   emitted twice (an `@_spi` `fatalError` stub plus the real `_direct` call):
   ```
   invalid redeclaration of 'decodeSequentially()'
   invalid redeclaration of 'shouldEncodeElementDefault(descriptor:index:)'
   invalid redeclaration of 'encodeNotNullMark()'
   ```
   [KT-87791](https://youtrack.jetbrains.com/issue/KT-87791) (exact match, same
   `decodeSequentially()` example; duplicate [KT-88225](https://youtrack.jetbrains.com/issue/KT-88225)
   notes it as a 2.4.20 regression). Fixed in build 2.5.0-dev-1133, available in 2.5.0-Beta1.

Both reproduce unchanged with Compose 1.12.1 and kotlinx-serialization 1.12.0-RC (also with
Compose 1.12.0 / serialization 1.11.0).

Both are in Swift Export's generated glue for dependencies, not fixable by rewriting our code.
Resolved by moving to Kotlin 2.5.0-Beta1, see above.

## 24.07.2026
kotlin = "2.4.20-Beta2"
jetbrainsCompose = "1.11.1"

### Fixed [KT-86463](https://youtrack.jetbrains.com/issue/KT-86463/)

### Blocker (worked around in our code on 06.10.2026)
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
