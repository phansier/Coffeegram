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
| Breakpoints | ✅ Width (600dp) and compact height (480dp), evaluated once at the root |
| List-detail (Calendar → Day) | ✅ `ChildPanels` DUAL/SINGLE with detail placeholder, detail pane ≤ 400dp |
| Supporting pane (Map + shop list) | ✅ Side pane on expanded or short-wide windows (40%, 280–400dp), bottom sheet otherwise |
| Settings list-detail | ✅ Category list + detail on wide |
| Adaptive lists / grids | ✅ Calendar fills its pane (`Grid`); lists stay single-column by design |
| App bars hide on scroll | ✅ Material theme: Day list, Settings, All-time stats, shop list (nav bar stays) |
| Touch targets vs. pointer | ✅ `+`/`-` buttons 48dp on touch, 32dp with a fine pointer |
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
and exposes `LocalWindowLayout` (`isWide`, `isExpandedWidth`, `isCompactHeight`); all screens read it. Root measurement (rather than
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

**4. ✅ Consider window height (compact-height landscape phones)**

A phone in landscape is ~800×360dp: it gets the rail + dual-pane calendar, and the top app bar (64dp)
leaves ~280dp for a 6-row month grid (regular cells need ~400dp). Done:

- `LocalWindowLayout.isCompactHeight` (root height < 480dp);
- `MonthTable(compact = true)` switches day cells to a single row (20dp icon beside the day number),
  so a 6-row month fits without scrolling;
- the `Text(year)` footer in `MonthTableScreen` is removed (the year is in the app bar).

Not possible here: a shorter app bar. `AdaptiveTopAppBar`'s Material adaptation exposes only colors,
centering and scroll behavior, so reclaiming app-bar height is left to hide-on-scroll (item 8).

**5. ✅ Calendar list-detail: detail placeholder and proportional panes**

In `CoffeeEditScreen` wide mode, when no day was selected the month table took 100% width, and
selecting a day made it jump to 50%. Done (`DualPaneCoffeeEdit` in `screens/CoffeeEditProxys.kt`):

- the detail pane is always reserved; with no day selected it shows `DayListPlaceholder`
  ("Pick a day to log your drinks", all 5 locales);
- the detail pane is half the content width, capped at 400dp, so on expanded widths the calendar
  gets the extra space instead of the drink list.

Not done: pre-selecting *today* in DUAL mode — it would change the app bar title and the web URL
(`/calendar/day/…`) without user action; the placeholder covers the empty state instead.

Existing behaviour that already matches the guidance: back arrow is hidden in DUAL mode, and
`ChildPanels` handles back and web history.

Material option (layout only): `ListDetailPaneScaffold(directive, value, listPane, detailPane)` driven
by a `ThreePaneScaffoldValue` computed from `ChildPanels` state — gives standard pane widths, spacing
and pane-transition animations without a Material navigator. Only worth it if the hand-written `Row`
grows beyond placeholder + widths.

**6. ✅ Map supporting pane: raise the side-pane threshold**

`SpecialtyScreen` showed a fixed 320dp `SidePane` from 600dp; at 600–840dp with a rail that left the
map ~200–440dp wide. Done:

- side pane when the window is expanded (≥ 840dp) **or** wide and short (landscape phones, where a
  bottom sheet in ~280dp of height would leave almost no map); bottom sheet otherwise, including
  medium-width portrait (tablet portrait, unfolded foldable);
- side pane width is 40% of the content, clamped to 280–400dp.

### P2 — Content adapts to space

**7. Screenshot tests per form factor (enabler for everything above)** — skipped for now

Only `CoffeeTypeItem` and `MonthTable` are screenshot-tested (`app/src/screenshotTest/kotlin/Screenshots.kt`),
at default phone size. Add a `@FormFactorPreviews` multi-preview (Phone / Foldable / Tablet / Desktop,
plus a landscape phone) and cover `RootScreen` for each tab.

Blocker: screen-level previews use CMP `stringResource`, which Compose Preview Screenshot Testing does
not support ([b/402137754](https://issuetracker.google.com/issues/402137754)). Options: previews that
receive already-resolved strings, or a desktop (JVM) screenshot runner (e.g. Roborazzi) for
`cmp-common`. Worth doing *before* items 1, 3 and 5 so regressions are visible; ranked here only
because of the blocker.

**8. ✅ Hide app bars on scroll** (nav bar: skipped)

App bars (done, `screens/TopBarScroll.kt`):

- `RootScreen` creates one `TopAppBarDefaults.enterAlwaysScrollBehavior()` per page and shares it
  with both pagers: the app bar pager passes it to `AdaptiveTopAppBar`'s Material adaptation, the
  content pager provides it via `LocalTopBarScrollBehavior`;
- only scrolling content opts in with `Modifier.hideTopBarOnScroll()`: `DayListScreen`, Settings,
  All-time stats and the coffee-shop list (side pane and bottom sheet; map gestures don't dispatch
  nested scroll). It resets the bar's height and scrolled tint when that content leaves (back to the
  month, Weekly tab), so the bar can't stay hidden or tinted over non-scrolling content;
- scroll deltas reach the bar only while the attached content can actually scroll (or the bar is
  partly hidden), so short content like Settings never hides the bar; the scrolled tint is disabled;
- Cupertino keeps the bar pinned: its adaptation has no scroll behavior (iOS bars don't hide), and
  the behavior is not created there — without a Material bar reporting its height the connection
  would swallow scroll deltas.

Skipped: hiding the bottom navigation bar on scroll. Tried (bar slid out while the selected page's
top bar was more than half collapsed) and reverted — the bottom bar stays visible.

Most valuable on compact-height landscape (item 4).

**9. ✅ Adaptive columns for vertical lists** (not needed)

Resolved by earlier items, no code change:

- `DayListScreen` is at most ~600dp wide in SINGLE mode (compact width) and at most 400dp as the
  DUAL-mode detail pane (item 5), so `GridCells.Adaptive(300.dp)` would always yield one column;
- `CoffeeShopList` stays a column in both the bottom sheet and the side pane (≤ 400dp, item 6);
- Settings detail stays a `Column` — revisit only if the settings list grows.

**10. ✅ Scale the month calendar to the available space**

`MonthTable` used two `LazyVerticalGrid(GridCells.Fixed(7))` (header and days) with fixed 32dp icons,
so on tablets/desktop the calendar occupied the top third. Done with the experimental Compose `Grid`
(`@OptIn(ExperimentalGridApi::class)`, available in `commonMain` of CMP 1.12 `foundation-layout`):

- one grid: 7 × `1.fr` columns, an `Auto` weekday-header row and 5–6 week rows of
  `MinMax(56dp | 32dp compact, 1.fr)`, so the month fills the pane and header/day columns always align;
- days are placed with `gridItem(row, column)` from the first weekday offset;
- the coffee icon scales with the cell (24–64dp), computed once per grid from its constraints;
- compact-height cells (item 4) are kept.

The `TablePreview` screenshot reference is outdated by design and needs re-recording
(`./gradlew :app:updateDebugScreenshotTest`) after review.

**11. ✅ Stats: width-based layout and wider charts**

Done (`screens/CoffeeCharts.kt`):

- expanded width (≥ 840dp): no tabs — one scrolling overview with Weekly and Over-time side by side
  and Distribution below;
- otherwise tabs as before; All-time charts sit side by side from the 600dp width breakpoint
  (`LocalWindowLayout.isWide`) instead of the old `maxWidth > maxHeight` aspect-ratio check;
- every chart is capped at 720dp and centered;
- charts are split into `WeeklyChartSection` / `OverTimeChartSection` / `DistributionChartSection`
  sharing one `ChartSection` title layout.

### P3 — Input & polish

**12. ✅ Input-aware touch targets**

`CoffeeTypeItem` `+`/`-` buttons were clamped to 32dp — below the 48dp touch minimum, and sitting
next to the counter so Compose's automatic touch-target expansion can't reliably cover them. Done:

- `hasFinePointer()` (`app_ui/PointerPrecision.kt`, expect/actual): Android → `mediaQuery {
  pointerPrecision == Fine }`, guarded by `ComposeUiFlags.isMediaQueryIntegrationEnabled` (switched on
  in `Application.onCreate`; previews and tests fall back to touch); desktop → fine; iOS → touch;
  web → CSS `(pointer: fine)`;
- the buttons are 48dp for touch and stay 32dp for mouse/trackpad.

Checked, no change needed: `TopBarIconButton` (38dp visual) and calendar `DayCell`s rely on Compose's
automatic expansion of pointer targets to 48dp; day cells are ≥ 56dp tall outside compact height.

The `Preview_0` screenshot reference (`CoffeeTypeItem`) shows the old 32dp buttons and needs
re-recording after review.

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
2. ~~Item 7~~ skipped (manual verification instead)
3. ~~Item 3 → 4 → 5 → 6~~ ✅ (navigation area and panes)
4. ~~Item 8 → 9 → 10 → 11~~ ✅ (content)
5. ~~Item 12~~ ✅ → 13 → 14 (input & polish)
