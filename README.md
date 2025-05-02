# Coffeegram
## Kotlin & Compose Multiplatform project

[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-1.8.0-green)](https://developer.android.com/jetpack/compose)
[![Compose Version](https://img.shields.io/badge/Compose%20Multiplatform-1.8.0--rc01-green)](https://github.com/JetBrains/compose-multiplatform)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.1.20-green.svg)](https://kotlinlang.org)
<br>
[![Android CI](https://github.com/phansier/Coffeegram/actions/workflows/android.yml/badge.svg)](https://github.com/phansier/Coffeegram/actions/workflows/android.yml)
[![Hits-of-Code](https://hitsofcode.com/github/phansier/Coffeegram?branch=develop)](https://hitsofcode.com/github/phansier/Coffeegram/view?branch=develop)

<br>

## Android-specific features

<a href='https://play.google.com/store/apps/details?id=ru.beryukhov.coffeegram&utm_source=github'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'  width="200"/></a>


- Material3 Dynamic(Harmonized, following device's theme)/Day/Night themes (switch enabled)

- Glance AppWidgets

- Compose for Wearable


- Jetpack Datastore

- Room Multiplatform as a database

- Animated splash for Android 12
- [Android 13 Monochrome icon](docs-monochrome/MONOCHROME-ICON.md)

<img src="docs-monochrome/after-clipping.png" alt="drawing" width="80"/>


<img src="images/month_table.png" alt="drawing" width="200"/><img src="images/coffee_list.png" alt="drawing" width="200"/><img src="images/settings.png" alt="drawing" width="200"/><img src="images/settings_dynamic.png" alt="drawing" width="200"/>
<br>
<img src="images/widgets.png" alt="drawing" width="200"/><img src="images/wear.png" alt="drawing" width="200"/>


---
## iOS

<img src="images/ios.png" alt="drawing" width="300"/><img src="images/ios_dark.png" alt="drawing" width="300"/>

Native iOS look & feel by [Compose Cupertino](https://github.com/alexzhirkevich/compose-cupertino/tree/master)


### Run on iOS
[Using KMP plugin](https://kotlinlang.org/docs/multiplatform-plugin-releases.html)


## Desktop

<img src="images/desktop.png" alt="drawing" width="300"/>


### Run on Desktop jvm
`./gradlew run`

## Run on Desktop - Hot Reload!
`./gradlew runHot`

## WASM
<img src="images/wasm.png" alt="drawing" width="300"/>

### Run WASM
`./gradlew :cmp-common:wasmJsBrowserDevelopmentRun`
or
`make runWasm`

---



## More about initial app creation:

[Medium EN](https://proandroiddev.com/change-my-mind-or-android-development-transformation-to-jetpack-compose-coroutines-e719a342cc52)

[Habr RU](https://habr.com/ru/company/kaspersky/blog/513364/)

[Youtube (AppsFest) RU](https://youtu.be/CuCV-SGUuCQ/)
