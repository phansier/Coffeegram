package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.setContent
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import ru.beryukhov.coffeegram.ui.CoffeegramTheme
import ru.beryukhov.coffeegram.ui.typography

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeegramTheme {
                CoffeeList()
            }
        }
    }
}

@Composable
fun CoffeeList() {
    val image = imageResource(R.drawable.header)

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        val imageModifier = Modifier
            .preferredHeightIn(maxHeight = 180.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp))

        Image(
            image, modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.preferredHeight(16.dp))
        Text(
            "A day wandering through the sandhills " +
                    "in Shark Fin Cove, and a few of the " +
                    "sights I saw",
            style = typography.h6,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text("Davenport, California", style = typography.body2)
        Text("December 2018", style = typography.body2)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoffeegramTheme {
        CoffeeList()
    }
}