package ru.beryukhov.coffeegram.pages

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics

val LazyListItemPosition = SemanticsPropertyKey<Int>("LazyListItemPosition")
val LazyListLengthSemantics = SemanticsPropertyKey<Int>("LazyListLength")
var SemanticsPropertyReceiver.lazyListItemPosition by LazyListItemPosition
var SemanticsPropertyReceiver.lazyListLength by LazyListLengthSemantics

fun Modifier.lazyListItemPosition(position: Int): Modifier {
    return semantics { lazyListItemPosition = position }
}

fun Modifier.lazyListLength(length: Int): Modifier {
    return semantics { lazyListLength = length }
}
