# Coffeegram
## Android & Multiplatform Compose based project #KMP

[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-1.7.8-yellow)](https://developer.android.com/jetpack/compose)
[![Compose Version](https://img.shields.io/badge/Compose%20Multiplatform-1.7.3-yellow)](https://github.com/JetBrains/compose-multiplatform)
[![WearCompose Version](https://img.shields.io/badge/Wear%20Compose-1.4.1-yellow)](https://developer.android.com/jetpack/androidx/releases/wear-compose)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg)](https://kotlinlang.org)
[![Android CI](https://github.com/phansier/Coffeegram/actions/workflows/android.yml/badge.svg)](https://github.com/phansier/Coffeegram/actions/workflows/android.yml)
[![Jetc.dev](https://img.shields.io/badge/jetc.dev-25-blue)](https://jetc.dev/issues/025.html)
[![Hits-of-Code](https://hitsofcode.com/github/phansier/Coffeegram?branch=develop)](https://hitsofcode.com/github/phansier/Coffeegram/view?branch=develop)

<br>
## Android app in Jetpack Compose and MVI

<a href='https://play.google.com/store/apps/details?id=ru.beryukhov.coffeegram&utm_source=github'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'  width="200"/></a>

- Animated splash for Android 12
- [Android 13 Monochrome icon](docs-monochrome/MONOCHROME-ICON.md)

<img src="docs-monochrome/after-clipping.png" alt="drawing" width="80"/>

- Material3 Dynamic(Harmonized, following device's theme)/Day/Night themes (switch enabled)

- Glance AppWidgets

- Compose for Wearable


- Jetpack Datastore

- Room Multiplatform as an database

<img src="images/month_table.png" alt="drawing" width="200"/><img src="images/coffee_list.png" alt="drawing" width="200"/><img src="images/settings.png" alt="drawing" width="200"/><img src="images/settings_dynamic.png" alt="drawing" width="200"/>
<br>
<img src="images/widgets.png" alt="drawing" width="200"/><img src="images/wear.png" alt="drawing" width="200"/>


---
## Multiplatform Compose
Android + Desktop + iOS + [WASM](https://coffegram.beriukhov.tech/) multiplatform app using [Compose Multiplatform](https://github.com/JetBrains/compose-jb) together with StateFlow and MVI

<img src="images/ios.png" alt="drawing" width="300"/><img src="images/ios_dark.png" alt="drawing" width="300"/>

Native iOS look & feel by [Compose Cupertino](https://github.com/alexzhirkevich/compose-cupertino/tree/master)

<img src="images/desktop.png" alt="drawing" width="300"/><img src="images/wasm.png" alt="drawing" width="300"/>


## Run on Desktop jvm
`./gradlew run`

## Run on Desktop - Hot Reload!
`./gradlew runHot`

## Run on iOS
[Using KMP plugin](https://kotlinlang.org/docs/multiplatform-plugin-releases.html)

---

# Next Steps
- More SwiftUI & UiKit integration samples 
- Compose Multiplatform + Native UI integration into iOS
- Compose optimisations



## More about initial app creation:

[Medium EN](https://proandroiddev.com/change-my-mind-or-android-development-transformation-to-jetpack-compose-coroutines-e719a342cc52)

[Habr RU](https://habr.com/ru/company/kaspersky/blog/513364/)

[Youtube (AppsFest) RU](https://youtu.be/CuCV-SGUuCQ/)
