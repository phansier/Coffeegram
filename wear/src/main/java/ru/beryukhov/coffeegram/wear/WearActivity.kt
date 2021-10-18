package ru.beryukhov.coffeegram.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text

class WearActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeegramTheme {
                PagesContent()
            }
        }
    }
}

@Preview
@Composable
fun PagesContent() {
    Scaffold() {
        Text(text = stringResource(R.string.hello_world))
    }
}
