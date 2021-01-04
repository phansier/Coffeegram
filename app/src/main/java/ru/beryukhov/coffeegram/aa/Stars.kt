package ru.beryukhov.coffeegram.aa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.R

//@Preview
@ExperimentalStdlibApi
@Composable
fun StarsPreview(modifier: Modifier = Modifier) = Stars(4, modifier)

//@OptIn(ExperimentalStdlibApi::class)
@ExperimentalStdlibApi
@Composable
fun Stars(rate: Int, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier) {
        items(items = buildList(5) {
            addAll((1..rate).map { true })
            addAll((rate + 1..5).map { false })
        },
            itemContent = {
                Image(
                    vectorResource(id = if (it) R.drawable.ic_star_enabled else R.drawable.ic_star_disabled),
                    Modifier.padding(end = 4.dp).height(12.dp).width(14.dp)
                )
            })
    }
}