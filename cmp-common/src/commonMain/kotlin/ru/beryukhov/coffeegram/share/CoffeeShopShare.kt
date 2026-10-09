package ru.beryukhov.coffeegram.share

import ru.beryukhov.coffeegram.repository.CoffeeShop

internal const val COFFEEGRAM_WEB_URL = "https://coffeegram.pages.dev"

internal fun CoffeeShop.shareLink(): String =
    if (id.isBlank()) "$COFFEEGRAM_WEB_URL/map" else "$COFFEEGRAM_WEB_URL/map/$id"

internal fun CoffeeShop.shareText(): String = "$name - Coffeegram\n${shareLink()}"
