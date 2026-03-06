package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.pages.boxShadow

@Composable
@Preview
private fun SmallMarker() = MapMarker(expanded = false)

@Composable
@Preview
private fun ExpandedMarker() = MapMarker(expanded = true)

@Composable
fun MapMarker(
    modifier: Modifier = Modifier,
    name: String = "Title",
    descr: String = "Subtitle",
    highlighted: Boolean = false,
    expanded: Boolean = false,
) {
    val borderRadius = if (expanded) 12.dp else 6.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .boxShadow(
                blurRadius = 3.dp,
                offset = DpOffset(x = 0.dp, y = 2.dp),
                shape = RoundedCornerShape(borderRadius),
                color = Color(0f, 0f, 0f, 0.15f)
            )
            .boxShadow(
                blurRadius = 9.dp,
                offset = DpOffset(x = 0.dp, y = 6.dp),
                shape = RoundedCornerShape(borderRadius),
                color = Color(0f, 0f, 0f, 0.04f)
            )
            .background(
                color = if (highlighted) Color(0xFFE8E5E3) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 6.dp)
            )
            .padding(start = 4.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
            .widthIn(min = 0.dp, max = (LocalConfiguration.current.screenWidthDp / if (highlighted) 1 else 2).dp)

    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_splash),
            contentDescription = "image description",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(start = 0.dp, top = 1.dp, end = 2.dp, bottom = 1.dp)
                .width(18.dp)
                .height(18.dp)

        )
        Column(
            verticalArrangement = Arrangement.spacedBy(if (highlighted) 4.dp else -4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            val textColor = MaterialTheme.colorScheme.onPrimaryContainer
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(350),
                    color = textColor,
                ),
                maxLines = if (highlighted) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
            if (expanded) {
                Text(
                    text = descr,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(350),
                        color = textColor,
                    ),
                    maxLines = if (highlighted) 3 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
