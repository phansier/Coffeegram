package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.layout.RowScope.gravity
import androidx.ui.material.Button
import androidx.ui.material.OutlinedButton
import androidx.ui.material.TextButton
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
            DefaultPreview()
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

data class CoffeeType(
    @DrawableRes
    val image: Int,
    val name: String,
    val count: Int = 0
)

@Composable
fun CoffeeTypeItem(type: CoffeeType) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            imageResource(type.image), modifier = Modifier
                .preferredHeightIn(maxHeight = 48.dp)
                .preferredWidthIn(maxWidth = 48.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(24.dp))
                .gravity(Alignment.CenterVertically),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.preferredWidth(16.dp))
        Text(
            type.name, style = typography.body1,
            modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
        )
        Row(modifier = Modifier.gravity(Alignment.CenterVertically)) {
            Spacer(Modifier.preferredWidth(16.dp))
            val textButtonModifier = Modifier.gravity(Alignment.CenterVertically)
                .preferredSizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(onClick = { }, padding = InnerPadding(0.dp), modifier = textButtonModifier) {
                Text("-")
            }
            Text(
                type.count.toString(), style = typography.body2,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { },
                padding = InnerPadding(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoffeegramTheme {
        //CoffeeList()
        CoffeeTypeItem(CoffeeType(R.drawable.header, "Cappucino"))
    }
}