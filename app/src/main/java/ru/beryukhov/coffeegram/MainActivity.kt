package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.*
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.gestures.ScrollableState
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.layout.ColumnScope.weight
import androidx.ui.layout.RowScope.gravity
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
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
fun CoffeeList(coffeeTypes: List<CoffeeType>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        coffeeTypes.forEach { type ->
            CoffeeTypeItem(type)
            //HomeScreenDivider()
        }
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
            val count = state { type.count }
            Spacer(Modifier.preferredWidth(16.dp))
            val textButtonModifier = Modifier.gravity(Alignment.CenterVertically)
                .preferredSizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(
                onClick = { count.value-- },
                padding = InnerPadding(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "${count.value}", style = typography.body2,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { count.value++ },
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
        Scaffold() {
            Column() {
                var selectedItem by state { 0 }
                when (selectedItem) {
                    0 -> {
                        Column(modifier = Modifier.weight(1f)){}
                    }
                    1 -> {
                        VerticalScroller(modifier = Modifier.weight(1f)) {
                            CoffeeList(
                                listOf(
                                    CoffeeType(R.drawable.header, "Cappucino"),
                                    CoffeeType(R.drawable.header, "Latte")
                                ) * 6
                            )
                        }
                    }
                }


                val items =
                    listOf("Calendar" to Icons.Filled.DateRange, "Info" to Icons.Filled.Info)

                BottomNavigation {
                    items.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = { Icon(item.second) },
                            text = { Text(item.first) },
                            selected = selectedItem == index,
                            onSelected = { selectedItem = index }
                        )
                    }
                }
            }
        }
    }
}

private operator fun <E> List<E>.times(i: Int): List<E> {
    val result = mutableListOf<E>()
    for (j in 0..i) {
        result.addAll(this)
    }
    return result
}
