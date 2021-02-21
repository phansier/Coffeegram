package ru.beryukhov.coffeegram.date_time

import androidx.compose.ui.text.intl.Locale
import java.text.DateFormatSymbols

actual fun dateFormatSymbolsShortWeekdays (locale: Locale) = DateFormatSymbols(java.util.Locale(locale.language, locale.region)).shortWeekdays
