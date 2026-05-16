# Fonts

Baseline snapshot of font files in this directory **before** any pyftmerge work.
Use this to redownload originals or sanity-check a merged file against the pre-merge state.

Pre-merge state recorded on 2026-05-16. Restore originals via `git show <commit>:<path>` if needed.

## Files in use (referenced from `Theme.kt`)

| File | Family | Style | Version | Size (B) | Glyphs | Latin¹ | Cyrillic² | Greek³ | Kind | Role |
|---|---|---|---|---:|---:|---:|---:|---:|---|---|
| `Fraunces.ttf` | Fraunces | Regular | 1.000;[b76b70a41] | 357 632 | 624 | 381 | 0 | 0 | VF | display serif (headlines, titles) |
| `Fraunces-Italic.ttf` | Fraunces | Italic | 1.000;[b76b70a41] | 407 304 | 624 | 381 | 0 | 0 | VF | display serif italic |
| `PlusJakartaSans.ttf` | Plus Jakarta Sans | Regular | 2.071;gftools[0.9.30] | 173 760 | 721 | 379 | 0 | 3 | VF | body / UI sans |
| `DMMono-Regular.ttf` | DM Mono | Regular | 1.000 (ttfautohint v1.8.2.53-6de2) | 48 852 | 381 | 307 | 0 | 1 | static | small body / numeric mono |

¹ codepoints `U+0000..U+024F`  ² `U+0400..U+052F`  ³ `U+0370..U+03FF` and `U+1F00..U+1FFF`
`VF` = variable font. `static` = single-instance.

## Donor files staged for merging (Cyrillic/Greek coverage)

| File | Family | Style | Size (B) | Glyphs | Latin | Cyrillic | Greek | Kind | Pairs with |
|---|---|---|---:|---:|---:|---:|---:|---|---|
| `PTSerif-Regular.ttf` | PT Serif | Regular | 215 516 | 717 | 299 | 220 | 4 | static | Fraunces (Cyrillic) |
| `PTSerif-Italic.ttf` | PT Serif | Italic | 232 264 | 720 | 299 | 220 | 4 | static | Fraunces-Italic (Cyrillic) |
| `GFSDidot-Regular.ttf` | GFS Didot | Regular | 175 388 | 1 104 | 332 | 4 | 322 | static | Fraunces (Greek) |
| `Manrope-Regular.ttf` | Manrope | Regular | 96 832 | 678 | 316 | 104 | 75 | static | Plus Jakarta Sans (Cyrillic + Greek) |
| `JetBrainsMono-Regular.ttf` | JetBrains Mono | Regular | 114 904 | 976 | 343 | 98 | 77 | static | DM Mono (Cyrillic + Greek) |

## Sources & licenses

All seven families are redistributed under the SIL Open Font License 1.1 (OFL). Merging
with `pyftmerge` and shipping the merged binary in this app is permitted; per OFL the
copyright/license notices must be preserved (kept inside each `.ttf`'s `name` table).
Do **not** rename the resulting family to imply it's an unmodified original.

- **Fraunces** — Undercase Type (Phaedra Charles, David Jonathan Ross). https://fonts.google.com/specimen/Fraunces
- **Plus Jakarta Sans** — Tokotype. https://fonts.google.com/specimen/Plus+Jakarta+Sans
- **DM Mono** — Colophon Foundry / Google. https://fonts.google.com/specimen/DM+Mono
- **PT Serif** — ParaType. https://fonts.google.com/specimen/PT+Serif
- **GFS Didot** — Greek Font Society. https://fonts.google.com/specimen/GFS+Didot
- **Manrope** — Mikhail Sharanda. https://fonts.google.com/specimen/Manrope
- **JetBrains Mono** — JetBrains. https://www.jetbrains.com/lp/mono/

## Known caveats for the merge

1. `Fraunces.ttf`, `Fraunces-Italic.ttf`, and `PlusJakartaSans.ttf` are **variable fonts**
   (have an `fvar` table). `pyftmerge` does not cleanly merge VF + static — expect
   errors or odd output. If you hit them, grab the static `*-Regular.ttf` from the
   `static/` folder of the Google Fonts download and use that as the base.
2. The shipped `PlusJakartaSans.ttf` covers zero Cyrillic and only 3 Greek
   codepoints. The upstream Plus Jakarta Sans v2 GF release **does** cover Cyrillic;
   this copy is the Latin-only subset. Either re-download the full version, or rely on
   the Manrope donor for both Cyrillic and Greek when merging.
3. `pyftmerge` preserves the GPOS/GSUB tables of the **first** font, so donor-specific
   kerning/ligatures for Cyrillic/Greek are dropped. Usually invisible at body sizes.
4. First font wins on overlapping codepoints — always list the brand font first.

## Reproducing the merges

From inside this directory, with `fonttools` available (`pip install fonttools`):

```bash
# Display serif: Latin (brand) + Cyrillic + Greek
pyftmerge Fraunces.ttf PTSerif-Regular.ttf GFSDidot-Regular.ttf
mv merged.ttf Fraunces.ttf

pyftmerge Fraunces-Italic.ttf PTSerif-Italic.ttf GFSDidot-Regular.ttf
mv merged.ttf Fraunces-Italic.ttf

# UI sans: brand + Cyrillic + Greek (via single donor)
pyftmerge PlusJakartaSans.ttf Manrope-Regular.ttf
mv merged.ttf PlusJakartaSans.ttf

# Mono: brand + Cyrillic + Greek
pyftmerge DMMono-Regular.ttf JetBrainsMono-Regular.ttf
mv merged.ttf DMMono-Regular.ttf
```

After each merge, sanity-check coverage with:

```bash
python3 -c "from fontTools.ttLib import TTFont; \
  f=TTFont('Fraunces.ttf'); cps=set(f.getBestCmap()); \
  print('Cyr:', any(0x0400<=c<=0x04FF for c in cps), \
        'Greek:', any(0x0370<=c<=0x03FF for c in cps))"
```
