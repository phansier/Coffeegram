package ru.beryukhov.coffeegram

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview

val actorsList = listOf(
    Actor("Robert Downey Jr.", R.drawable.downey),
    Actor("Chris Evans", R.drawable.evans),
    Actor("Mark Ruffalo", R.drawable.ruffalo),
    Actor("Chris Hemsworth", R.drawable.hemsworth)
)

@Preview(device = Devices.PIXEL_C, backgroundColor = 0xff191926)
@Preview(backgroundColor = 0xff191926)
@Composable
fun AAActorsPreview(modifier: Modifier = Modifier) {
    AAActors(actors = actorsList, modifier = modifier)
}

data class Actor(
    val name: String,
    @DrawableRes val photo: Int
)

@Composable
fun AAActors(actors: List<Actor>, modifier: Modifier = Modifier) {
    LazyRowFor(items = actors,
        modifier = modifier,
        itemContent = {
            AAActor(it)
        })
}

@Preview
@Composable
fun AAActorPreview() = AAActor(actor = Actor("Robert Downey Jr.", R.drawable.downey))

@Composable
fun AAActor(actor: Actor) {
    ConstraintLayout {
        val (photo, name) = createRefs()
        Image(imageResource(id = actor.photo),
            modifier = Modifier.constrainAs(photo) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 4.dp)
                end.linkTo(parent.end, margin = 4.dp)
            }
                .width(80.dp)
                .height(80.dp)
        )
        Text(text = actor.name,
            color = Color.White,
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
