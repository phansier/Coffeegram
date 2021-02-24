package ru.beryukhov.coffeegram.aa

import android.os.Build
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import ru.beryukhov.coffeegram.R

@Preview(device = Devices.PIXEL_C)
@Preview
@Composable
fun FilmPage() {
    rememberScrollState(0)
    LazyColumn(
        modifier = Modifier.background(color = ColorFromRes(R.color.aa_background)).fillMaxHeight()
    ) {
        // use `item` for separate elements like headers
        // and `items` for lists of identical elements
        item {
            //todo make text corresponding margins
            ConstraintLayout(Modifier) {
                val (imageView2, tvAge, tvTitle, tvTag, ivStars,
                    tvReviews, view, imageView7, tvBack, storyline, storylineText,
                    cast, list_actors_preview) = createRefs()
                Box(modifier = Modifier.constrainAs(imageView2) {
                    height = Dimension.value(298.dp)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.origh512),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(id = R.drawable.mask),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(text = "13+",
                    color = ColorFromRes(R.color.aa_white_text),
                    modifier = Modifier.constrainAs(tvAge) {
                        bottom.linkTo(tvTitle.top, margin = 12.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )
                Text(text = "Avengers: End Game",
                    color = ColorFromRes(R.color.aa_whitable_text),
                    fontSize = 36.sp,
                    modifier = Modifier.constrainAs(tvTitle) {
                        linkTo(
                            start = parent.start, end = parent.end,
                            startMargin = 16.dp, endMargin = 16.dp, bias = 0f
                        )
                        top.linkTo(parent.top, margin = 252.dp)
                    }
                )
                Text(text = "Action, Adventure, Fantasy",
                    color = ColorFromRes(R.color.aa_contrast_text),
                    modifier = Modifier.constrainAs(tvTag) {
                        linkTo(
                            start = parent.start, end = parent.end,
                            startMargin = 16.dp, endMargin = 16.dp, bias = 0f
                        )
                        top.linkTo(tvTitle.bottom, margin = 4.dp)
                    }
                )
                StarsPreview(
                    modifier = Modifier.constrainAs(ivStars) {
                        top.linkTo(tvTag.bottom, margin = 8.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )
                Text(
                    text = "123 Review",
                    color = ColorFromRes(R.color.aa_disabled_text),
                    modifier = Modifier.constrainAs(tvReviews) {
                        linkTo(
                            start = ivStars.end, end = parent.end,
                            startMargin = 4.dp, endMargin = 16.dp, bias = 0f
                        )
                        centerVerticallyTo(ivStars)
                    }
                )
                Box(modifier = Modifier.constrainAs(view) {
                    linkTo(
                        start = parent.start, end = parent.end
                    )
                    top.linkTo(parent.top, margin = 44.dp)
                }
                    .height(36.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "",
                    modifier = Modifier.constrainAs(imageView7) {
                        linkTo(
                            top = view.top,
                            bottom = view.bottom
                        )
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )
                Text(
                    text = "Back",
                    color = ColorFromRes(R.color.aa_whitable_text),
                    fontSize = 16.sp,
                    modifier = Modifier.constrainAs(tvBack) {
                        linkTo(
                            top = view.top,
                            bottom = view.bottom
                        )
                        linkTo(
                            start = imageView7.end, end = parent.end,
                            startMargin = 4.dp, endMargin = 16.dp, bias = 0f
                        )
                    }
                )
                Text(text = "Storyline",
                    color = ColorFromRes(R.color.aa_whitable_text),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(storyline) {
                        linkTo(
                            start = parent.start, end = parent.end,
                            startMargin = 16.dp, endMargin = 16.dp, bias = 0f
                        )
                        top.linkTo(tvReviews.bottom, margin = 24.dp)
                    }
                )
                val storylineString =
                    "After the devastating events of Avengers: Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos' actions and restore balance to the universe."
                Text(text = storylineString,
                    color = ColorFromRes(R.color.aa_whitable_text),
                    fontSize = 14.sp,
                    modifier = Modifier.constrainAs(storylineText) {
                        //todo make text corresponding margins
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        top.linkTo(storyline.bottom, margin = 4.dp)
                        width = fillToConstraints
                    }
                )
                Text(text = "Cast",
                    color = ColorFromRes(R.color.aa_whitable_text),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(cast) {
                        linkTo(
                            start = parent.start, end = parent.end,
                            startMargin = 16.dp, endMargin = 16.dp, bias = 0f
                        )
                        top.linkTo(storylineText.bottom, margin = 20.dp)
                    }
                )
                ActorsPreview(modifier = Modifier.constrainAs(list_actors_preview) {
                    linkTo(
                        start = parent.start, end = parent.end,
                        startMargin = 12.dp, endMargin = 12.dp, bias = 0f
                    )
                    top.linkTo(cast.bottom, margin = 6.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = fillToConstraints
                })
            }
        }
    }
}

@Composable
fun ColorFromRes(@ColorRes color: Int): Color {
    val context = LocalContext.current
    val res = context.resources
    val theme = context.theme
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Color(res.getColor(color, theme))
    } else {
        Color(res.getColor(color))
    }
}
