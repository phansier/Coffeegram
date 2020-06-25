package ru.beryukhov.coffeegram.pages

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope.weight
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.KeyboardArrowLeft
import androidx.ui.material.icons.filled.KeyboardArrowRight
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.dp
import ru.beryukhov.coffeegram.view.SampleTable

@Composable
fun TablePage() {
    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = "Month",
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )

        }
    },
        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.KeyboardArrowLeft) } },
        actions = { IconButton(onClick = {}) { Icon(Icons.Default.KeyboardArrowRight) } }
    )

    Column(modifier = Modifier.weight(1f), horizontalGravity = Alignment.End) {
        SampleTable(modifier = Modifier.weight(1f))
        Text("2020", modifier = Modifier.padding(16.dp))
    }
}