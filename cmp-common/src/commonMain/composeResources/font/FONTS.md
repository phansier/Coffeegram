# Fonts

The four `.ttf` files in this directory are **merged builds**: each combines a brand
font (Fraunces / Plus Jakarta Sans / DM Mono) with a script-coverage donor for
Cyrillic and Greek. Compose Resources auto-generates `Res.font.<name>` for every file
here, so donor files are removed after merging — see [Reproducing the merges](#reproducing-the-merges).

## Files in use (referenced from `Theme.kt`)

| File | Family | Style | Weight | Size (B) | Glyphs | Latin¹ | Cyrillic² | Greek³ | Role |
|---|---|---|---:|---:|---:|---:|---:|---:|---|
| `Fraunces.ttf` | Fraunces | Regular | 900 | 455 072 | 1 475 | 388 | 132 | 322 | display serif (headlines, titles) |
| `Fraunces-Italic.ttf` | Fraunces | Italic | 900 | 479 528 | 1 475 | 388 | 132 | 322 | display serif italic |
| `PlusJakartaSans.ttf` | Plus Jakarta Sans | Regular | 400 | 191 076 | 908 | 380 | 104 | 75 | body / UI sans |
| `DMMono-Regular.ttf` | DM Mono | Regular | 400 | 163 728 | 978 | 343 | 98 | 77 | small body / numeric mono |

¹ codepoints `U+0000..U+024F`  ² `U+0400..U+052F`  ³ `U+0370..U+03FF` and `U+1F00..U+1FFF`

To restore a pre-merge original, fetch from git: `git log -- <path>` then
`git cat-file -p <blob>` (filenames matching pre-merge state may have been deleted, so
use `git rev-list --all --objects | grep <name>` to find the blob hash).

## Donors used in current merges

| File | Family | Style | Glyphs | Latin | Cyrillic | Greek | Weight | Paired with |
|---|---|---|---:|---:|---:|---:|---:|---|
| `Spectral-ExtraBold.ttf` | Spectral | ExtraBold | 878 | 381 | 128 | 3 | 800 | Fraunces |
| `Spectral-ExtraBoldItalic.ttf` | Spectral | ExtraBold Italic | 878 | 381 | 128 | 3 | 800 | Fraunces-Italic |
| `GFSDidot-Regular.ttf` | GFS Didot | Regular | 1104 | 332 | 4 | 322 | 400 | Fraunces (Greek)¹ |
| `Manrope-Regular.ttf` | Manrope | Regular | 678 | 316 | 104 | 75 | 400 | Plus Jakarta Sans |
| `JetBrainsMono-Regular.ttf` | JetBrains Mono | Regular | 976 | 343 | 98 | 77 | 400 | DM Mono |

Donor files are removed from this directory after merging (Compose Resources auto-packages
every `.ttf` here). Redownload from Google Fonts when re-merging.

¹ GFS Didot only ships in Regular (weight 400). Fraunces is pinned at weight 900 (Black)
so Greek letters render lighter than Latin/Cyrillic. Acceptable trade-off for the `el`
locale; to fix later, swap to a heavier Greek serif (e.g. GFS Bodoni Black or a Noto
Serif Greek heavy weight) and re-merge.

## Sources & licenses

All seven families are redistributed under the SIL Open Font License 1.1 (OFL). Merging
with `pyftmerge` and shipping the merged binary in this app is permitted; per OFL the
copyright/license notices must be preserved (kept inside each `.ttf`'s `name` table).
Do **not** rename the resulting family to imply it's an unmodified original.

- **Fraunces** — Undercase Type (Phaedra Charles, David Jonathan Ross). https://fonts.google.com/specimen/Fraunces
- **Plus Jakarta Sans** — Tokotype. https://fonts.google.com/specimen/Plus+Jakarta+Sans
- **DM Mono** — Colophon Foundry / Google. https://fonts.google.com/specimen/DM+Mono
- **Spectral** — Production Type. https://fonts.google.com/specimen/Spectral
- **GFS Didot** — Greek Font Society. https://fonts.google.com/specimen/GFS+Didot
- **Manrope** — Mikhail Sharanda. https://fonts.google.com/specimen/Manrope
- **JetBrains Mono** — JetBrains. https://www.jetbrains.com/lp/mono/

## Known caveats

1. **Variable fonts must be flattened first.** The original `Fraunces`, `Fraunces-Italic`,
   and `PlusJakartaSans` upstream files are variable fonts; `pyftmerge` errors on VFs
   (`VarStore has no attribute mergeMap`). Use `fontTools.varLib.instancer.instantiateVariableFont`
   to bake them at the desired axis values first.
2. **UPEM mismatches must be reconciled.** `pyftmerge` requires identical
   `head.unitsPerEm` across all inputs. Fraunces is 2000 UPEM; PT Serif / GFS Didot /
   Spectral / Plus Jakarta Sans are 1000 UPEM; Manrope is 2000 UPEM. Use
   `fontTools.ttLib.scaleUpem.scale_upem` on the donor before merging.
3. **Weight matching matters.** Fraunces is pinned at `wght=900` (Black), so its
   Cyrillic donor must also be heavy or the scripts render at visibly different
   weights. Spectral ExtraBold (800) is the closest available match. The Greek donor
   GFS Didot is only Regular (400) — Greek currently renders lighter than Latin/Cyrillic;
   acceptable for the small `el` locale audience, swap to a heavier Greek serif later
   if it becomes a problem.
4. **First font wins on overlapping codepoints** — always list the brand font first.
   `pyftmerge` keeps the GPOS/GSUB tables of the first font only, so donor-specific
   kerning/ligatures for Cyrillic/Greek are dropped (usually invisible at body sizes).
5. **`U+2126` (Ohm sign) and `U+2206` (Increment)** were dropped from the Fraunces merges
   because both Fraunces and GFS Didot define glyphs named `Omega`/`Delta`. The real
   Greek letters Ω (`U+03A9`) and Δ (`U+0394`) are unaffected.

## Reproducing the merges

From inside this directory, with `fonttools` installed (`pip install fonttools`).
Donor files are needed in the working directory but should be deleted after merging.

```bash
PY=python3   # adjust if fonttools isn't on system python
PYFT=pyftmerge

# 1) Flatten Fraunces / PJS VFs to static instances at their current axis defaults.
$PY -c "
from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont
for fn, axes in [
    ('Fraunces.ttf',       {'opsz': 9, 'wght': 900, 'SOFT': 0, 'WONK': 1}),
    ('Fraunces-Italic.ttf',{'opsz': 9, 'wght': 900, 'SOFT': 0, 'WONK': 1}),
    ('PlusJakartaSans.ttf',{'wght': 400}),
]:
    instantiateVariableFont(TTFont(fn), axes).save(fn.replace('.ttf','-static.ttf'))
"

# 2) Rescale donors so they share UPEM with their base.
$PY -c "
from fontTools.ttLib import TTFont, scaleUpem
for fn, upem in [
    ('Spectral-ExtraBold.ttf',       2000),
    ('Spectral-ExtraBoldItalic.ttf', 2000),
    ('GFSDidot-Regular.ttf',         2000),
    ('Manrope-Regular.ttf',          1000),
]:
    f = TTFont(fn); scaleUpem.scale_upem(f, upem); f.save(fn.replace('.ttf', f'-u{upem}.ttf'))
"

# 3) Merge. Brand font always first.
$PYFT Fraunces-static.ttf        Spectral-ExtraBold-u2000.ttf       GFSDidot-Regular-u2000.ttf && mv merged.ttf Fraunces.ttf
$PYFT Fraunces-Italic-static.ttf Spectral-ExtraBoldItalic-u2000.ttf GFSDidot-Regular-u2000.ttf && mv merged.ttf Fraunces-Italic.ttf
$PYFT PlusJakartaSans-static.ttf Manrope-Regular-u1000.ttf                                     && mv merged.ttf PlusJakartaSans.ttf
$PYFT DMMono-Regular.ttf         JetBrainsMono-Regular.ttf                                     && mv merged.ttf DMMono-Regular.ttf

# 4) Clean up.
rm -f *-static.ttf *-u1000.ttf *-u2000.ttf \
      Spectral-ExtraBold.ttf Spectral-ExtraBoldItalic.ttf \
      GFSDidot-Regular.ttf Manrope-Regular.ttf JetBrainsMono-Regular.ttf
```

Sanity-check coverage after each merge:

```bash
python3 -c "from fontTools.ttLib import TTFont; \
  f=TTFont('Fraunces.ttf'); cps=set(f.getBestCmap()); \
  print('Cyr:', any(0x0400<=c<=0x04FF for c in cps), \
        'Greek:', any(0x0370<=c<=0x03FF for c in cps))"
```
