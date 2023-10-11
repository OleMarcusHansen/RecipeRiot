package no.hiof.reciperiot.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.hiof.reciperiot.R

@Composable
fun FavouriteMeals() {
    Card {
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                // painterResource(successInfo.successInfoImageId)
                painterResource(R.drawable.food),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // will display in the middle of the image
            Text("Some Text In the middle")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Favouritepreview() {
    RecipeRiotTheme {
        FavouriteMeals()
    }
}