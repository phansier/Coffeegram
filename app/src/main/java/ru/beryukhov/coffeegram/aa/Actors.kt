package ru.beryukhov.coffeegram.aa

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ConstraintLayout
import ru.beryukhov.coffeegram.R

val actorsList = listOf(
    Actor("Robert Downey Jr.", R.drawable.downey),
    Actor("Chris Evans", R.drawable.evans),
    Actor("Mark Ruffalo", R.drawable.ruffalo),
    Actor("Chris Hemsworth", R.drawable.hemsworth)
)

@Preview(device = Devices.PIXEL_C, backgroundColor = 0xff191926)
@Preview(backgroundColor = 0xff191926)
@Composable
fun ActorsPreview(modifier: Modifier = Modifier) {
    Actors(actors = actorsList, modifier = modifier)
}

data class Actor(
    val name: String,
    @DrawableRes val photo: Int
)

@Composable
fun Actors(actors: List<Actor>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier.fillMaxWidth()) {
        itemsIndexed(items = actors,
            itemContent = { _, item ->
                ActorItem(item)
            })
    }
}

@Preview
@Composable
fun ActorItemPreview() = ActorItem(actor = Actor("Robert Downey Jr.", R.drawable.downey))

@Composable
fun ActorItem(actor: Actor) {
    ConstraintLayout(Modifier) {
        val (photo, name) = createRefs()
        Image(painter = painterResource(id = actor.photo),
            contentDescription = "Actor's photo",
            modifier = Modifier.constrainAs(photo) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 4.dp)
                end.linkTo(parent.end, margin = 4.dp)
            }
                .width(80.dp)
                .height(80.dp)
        )
        Text(text = actor.name,
            color = ColorFromRes(color = R.color.aa_white_text),
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start, margin = 4.dp)
                end.linkTo(parent.end, margin = 4.dp)
                bottom.linkTo(parent.bottom)
                top.linkTo(photo.bottom, margin = 6.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}
