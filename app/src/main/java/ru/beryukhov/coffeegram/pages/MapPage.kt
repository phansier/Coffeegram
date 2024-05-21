@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.R

@Preview
@Composable
internal fun MapPagePreview() {
    Column {
        MapPage()
    }
}

@Composable
fun ColumnScope.MapPage(modifier: Modifier = Modifier) {

}

@Composable
fun MapAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.map_long)) },
        modifier = modifier
    )
}
