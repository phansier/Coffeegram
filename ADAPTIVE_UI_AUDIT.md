# Adaptive UI Audit — Coffeegram

Audit of the shared Compose UI (`cmp-common`) and the Android host (`app`), checked against the
Android CLI `adaptive` skill (Jetpack Compose adaptive guidance, skill version 2026-08-27).

Scope: phone, foldable, tablet, desktop/freeform windows, landscape, and pointer/keyboard input.
Wear OS (`wear`) and Glance widgets are out of scope.

Constraints:

- Material 3 is allowed for UI and layouts (`material3`, `material3-adaptive`, scroll behaviors,
  window size classes, pane scaffolds driven by our own state).
- Material 3 is **not** used for navigation: no `adaptive-navigation3`, `ListDetailSceneStrategy`,
  `ThreePaneScaffoldNavigator`. Decompose (`ChildPages`, `ChildPanels`) owns navigation state.
- The Cupertino look (`AdaptiveNavigationBar`, `AdaptiveTopAppBar`) must be kept, which rules out
  Material components that render their own bar (e.g. full `NavigationSuiteScaffold`).

Status legend: ✅ done · ⏳ open

## Current state (summary)

| Area | Status |
|---|---|
| All screens in Compose | ✅ |
| Navigation library | ⚠️ Decompose (`ChildPages` + `ChildPanels`), not Navigation 3 |
| Adaptive navigation area | ✅ `AdaptiveNavigationContainer`: Cupertino bar on compact or tabletop, Material rail otherwise |
| Breakpoints | ⚠️ One width breakpoint (600dp), now evaluated once at the root; height ignored |
| List-detail (Calendar → Day) | ⚠️ `ChildPanels` DUAL/SINGLE, but no detail placeholder, fixed 50/50 split |
| Supporting pane (Map + shop list) | ✅ Side pane on wide, bottom sheet on narrow |
| Settings list-detail | ✅ Category list + detail on wide |
| Adaptive lists / grids | ❌ All `LazyColumn`s single column; calendar cells don't scale |
| App bars hide on scroll | ❌ None |
| Touch targets vs. pointer | ❌ `+`/`-` buttons are 32dp max, no input-aware sizing |
| Keyboard / mouse | ❌ No shortcuts, no hover states |
| Form-factor screenshot tests | ❌ Only 2 component-level previews; screen-level blocked by CMP resources issue |
| Orientation / resizability | ✅ No orientation lock, resizable by default |

## Prioritised work list

### P0 — Bugs / correctness on real devices

**1. ✅ Unify window-size detection; fix the 600–680dp "half wide" state**

`RootScreen` measures the full window, but `CoffeeEditScreen`, `SpecialtyScreen` and `SettingsScreen`
measure *their own* content area (window minus the 80dp nav rail) against the same 600dp threshold.

For a window width in `[600dp, 680dp)` — e.g. an unfolded foldable in portrait (`Devices.FOLDABLE` is
673dp wide), or a narrow desktop/freeform window:

- root shows the rail and passes `isWide = true` to `CoffeeEditAppBar`;
- `CoffeeEditScreen` sees < 600dp and switches `ChildPanels` to `SINGLE`;
- tapping a day shows `DayListScreen` full-screen while the app bar still shows `MonthTableAppBar`
  → **no back arrow, wrong title**; only system back returns to the calendar.
- Map/Settings fall back to their narrow layouts while the rail is shown.

Files: `screens/RootScreen.kt:58-59`, `screens/CoffeeEditProxys.kt:38-40`,
`screens/SpecialtyScreen.kt:24-25`, `screens/SettingsScreen.kt:94-100`.

Done: `WideLayoutProvider` (`screens/WideScreen.kt`) measures the root once with `BoxWithConstraints`
and exposes `LocalIsWideLayout`; all screens read it. Root measurement (rather than
`currentWindowAdaptiveInfo()` or `mediaQuery`) is deliberate: it stays correct in the store previews,
where the app is drawn inside a phone frame smaller than the preview window. `mediaQuery` is also
unusable in CMP 1.12 — `LocalUiMediaScope` is only provided on Android behind
`ComposeUiFlags.isMediaQueryIntegrationEnabled`, and throws elsewhere.

**2. ✅ Leaked coroutine scopes on configuration change**

`DefaultCoffeeEditComponent`, `DefaultMonthTableComponent`, `DefaultDayListComponent` (leaked on every
opened day) and `DefaultMapComponent` launched into never-cancelled `CoroutineScope(...)`s, so every
fold/unfold, rotation or window resize left collectors running. Done: all four use Essenty's
lifecycle-bound `coroutineScope()` (`essenty:lifecycle-coroutines`).

### P1 — Core adaptive layout

**3. ✅ Adaptive navigation container**

Done: `AdaptiveNavigationContainer` (`screens/AdaptiveNavigation.kt`) replaces the two `RootScreen`
branches with one tree (switching bar ↔ rail no longer resets the pager):

- compact width **or tabletop posture** (`currentWindowAdaptiveInfo().windowPosture`, from
  `material3-adaptive:adaptive`) → Cupertino `AdaptiveNavigationBar` in the `Scaffold` bottom bar;
- otherwise → Material `NavigationRail`, with the rail's start inset consumed for the content;
- nav icons use `contentDescription = null` (labels are present).

`NavigationSuiteScaffold` was considered and not used: it renders a Material bar, losing the
Cupertino look, and `NavigationSuiteScaffoldLayout` with a custom suite adds a dependency for no gain
over the container. Deferred to item 8: a `visible` flag for hiding the navigation on scroll.
Possible follow-up: `WideNavigationRail` (expanded, labels beside icons) for ≥ 840dp windows.

**4. Consider window height (compact-height landscape phones)**

A phone in landscape is ~800×360dp: it gets the rail + dual-pane calendar, but the top app bar (64dp)
leaves ~280dp for a 6-row month grid. Measure height next to width in
`WideLayoutProvider` (or use `WindowSizeClass` height breakpoints from `material3-adaptive`) and:

- switch app bars to a small/collapsing variant (see item 8);
- ~~drop the `Text(year)` footer in `MonthTableScreen`~~ ✅ removed (the year is the app bar eyebrow).

**5. Calendar list-detail: detail placeholder and proportional panes**

In `CoffeeEditScreen` wide mode, when no day is selected the month table takes 100% width, and
selecting a day makes it jump to 50%. Always reserve the detail pane and show a placeholder
("Pick a day to log drinks") in `CoffeeEditScreen` when `state.details == null`.
Also consider pre-selecting *today* in DUAL mode, and a ~40/60 or fixed-width (≈360–400dp) detail
pane on expanded widths instead of `weight(1f)` / `weight(1f)`.

Existing behaviour that already matches the guidance: back arrow is hidden in DUAL mode, and
`ChildPanels` handles back and web history.

Material option (layout only): `ListDetailPaneScaffold(directive, value, listPane, detailPane)` driven
by a `ThreePaneScaffoldValue` computed from `ChildPanels` state — gives standard pane widths, spacing
and pane-transition animations without a Material navigator. Only worth it if the hand-written `Row`
grows beyond placeholder + widths.

**6. Map supporting pane: raise the side-pane threshold**

`SpecialtyScreen` shows a fixed 320dp `SidePane` from 600dp. At 600–840dp with a rail that leaves the
map ~200–440dp wide. Show the side pane from the *expanded* width class (≥ 840dp) and keep the bottom
sheet on medium, or make the pane width a fraction (e.g. `0.4f`, clamped 280–400dp).

### P2 — Content adapts to space

**7. Screenshot tests per form factor (enabler for everything above)**

Only `CoffeeTypeItem` and `MonthTable` are screenshot-tested (`app/src/screenshotTest/kotlin/Screenshots.kt`),
at default phone size. Add a `@FormFactorPreviews` multi-preview (Phone / Foldable / Tablet / Desktop,
plus a landscape phone) and cover `RootScreen` for each tab.

Blocker: screen-level previews use CMP `stringResource`, which Compose Preview Screenshot Testing does
not support ([b/402137754](https://issuetracker.google.com/issues/402137754)). Options: previews that
receive already-resolved strings, or a desktop (JVM) screenshot runner (e.g. Roborazzi) for
`cmp-common`. Worth doing *before* items 1, 3 and 5 so regressions are visible; ranked here only
because of the blocker.

**8. Hide app bars (and nav bar) on scroll**

Top bars live in the root `Scaffold`'s `topBar` inside a separate `ChildPages` pager, so no screen can
drive them from its own scroll. To fix:

- move each tab's app bar next to its content (per-tab layout instead of the root `topBar` pager);
- use `TopAppBarDefaults.enterAlwaysScrollBehavior()` + `Modifier.nestedScroll(...)` for
  `DayListScreen`, Settings and All-time stats, passed to `AdaptiveTopAppBar` through its Material
  adaptation; check what the Cupertino adaptation supports and fall back to a small
  `NestedScrollConnection`-driven offset there;
- add a `visible` flag to `AdaptiveNavigationContainer` (item 3) fed from the same scroll state to
  hide the bottom bar on scroll down.

Most valuable on compact-height landscape (item 4).

**9. Adaptive columns for vertical lists**

- `DayListScreen` (`LazyColumn` of coffee types): on desktop/large widths the detail pane can be
  600dp+ wide with a single row per drink. Switch to `LazyVerticalGrid(GridCells.Adaptive(300.dp))`.
- `CoffeeShopList` in the bottom sheet: keep as a column; in the side pane keep a column (pane is narrow).
- Settings detail (`Column` of radio/switch rows): candidate for the experimental `Grid`/`FlexBox`
  on expanded widths, low value — skip unless the settings list grows.

**10. Scale the month calendar to the available space**

`MonthTable` uses two `LazyVerticalGrid(GridCells.Fixed(7))` (header and days) with fixed 32dp icons,
so on tablets/desktop the calendar occupies the top third and cells never grow vertically. The month is
a fixed 7×(5–6) grid, which is exactly what the experimental Compose `Grid` API is for: size cells from
`constraints` so the month fills the pane, scale the coffee icon with cell size, and put the weekday
header in the same grid so columns always align.

⚠️ `Grid` is experimental (Compose 1.11+, `@OptIn(ExperimentalGridApi::class)`) — confirm it's
acceptable and that the CMP 1.12 artifacts expose it on all targets before adopting.

**11. Stats: width-based layout and wider charts**

- `AllTimeCoffeeChart` picks side-by-side charts by `maxWidth > maxHeight` (aspect ratio). A tall
  tablet in portrait gets stacked charts at 800dp+ width; a squat window gets cramped side-by-side
  ones. Switch on width class instead.
- On expanded widths, show Weekly and All-time together (two columns / `Grid`) instead of tabs.
- Cap chart width (`widthIn(max = …)`) so the weekly column chart isn't stretched across 1500dp.

### P3 — Input & polish

**12. Input-aware touch targets**

`CoffeeTypeItem` `+`/`-` buttons are clamped to `sizeIn(maxWidth = 32.dp, maxHeight = 32.dp)` —
below the 48dp minimum for touch. Use 48dp targets for coarse (touch) pointers and compact 32dp for
fine (mouse/trackpad). `mediaQuery { pointerPrecision }` is Android-only and flag-gated in CMP 1.12
(see item 1), so wrap it in an `expect`/`actual` (Android: `mediaQuery`; desktop/web: fine; iOS: coarse). Same check for `DayCell` on small phones
and `TopBarIconButton` (38dp visual; verify the inner touch target stays ≥ 48dp).

**13. Keyboard and mouse support (desktop, ChromeOS, tablets with keyboards)**

None today (no `onKeyEvent`, hover, or pointer icons). Suggested:

- `←`/`→` (or `PageUp`/`PageDown`) change month; arrow keys move day focus in the calendar;
  `Enter` opens the day; `+`/`-` adjust the focused drink; `Esc` closes the day pane.
- Hover highlight on `DayCell`, coffee-shop rows and settings categories; hand pointer icon on clickables.
- Visible focus indication on custom clickables (`SettingsCategoryList` rows use bare `Text.clickable`).

**14. Larger layout classes (≥ 1200dp)**

`StorePreview` already renders 1200 and 1500dp tablets. On these widths consider a max content width
for Settings detail and Day list, and showing the calendar + day + weekly stats as three panes.

**15. Navigation 3 (deferred)**

The skill's multi-pane guidance assumes Navigation 3 with the Material `SceneStrategy`s
(`ListDetailSceneStrategy`, `SupportingPaneSceneStrategy` from `material3:adaptive-navigation3`).
Those are Material navigation, which is out of scope by constraint. Coffeegram uses Decompose, which already provides list-detail via
`ChildPanels`, web-history integration and KMP support across Android/iOS/desktop/web. Migrating is
**not recommended now**; revisit only if Navigation 3's multiplatform artifacts reach parity with
`childPagesWebNavigation`/`childPanelsWebNavigation`. The items above are written to work with Decompose.

## Suggested order of execution

1. ~~Item 1 + 2 (bugs, small)~~ ✅
2. Item 7 (screenshot baseline, or accept manual verification if the blocker isn't solved)
3. ~~Item 3~~ ✅ → 4 → 5 → 6 (navigation area and panes)
4. Item 8 → 9 → 10 → 11 (content)
5. Item 12 → 13 → 14 (input & polish)
