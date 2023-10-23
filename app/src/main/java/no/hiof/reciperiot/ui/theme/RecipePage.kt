package no.hiof.reciperiot.ui.theme

import android.webkit.WebSettings.TextSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.hiof.reciperiot.R
import no.hiof.reciperiot.ui.theme.data.RecipeSource
import no.hiof.reciperiot.ui.theme.model.Recipe

@Composable
fun RecipePage() {
    Text(text = "dette er min rett",
        color = Color.Black,
        fontSize = 28.sp
    )

}

@Composable
fun RecipeDetails(recipe: Recipe) {
    Text(text = recipe.title)


}

@Composable
fun RecipePage1(navController: NavController, recipeId: Int) {
    // Hent resten av variablene fra RecipeSource basert på recipeId
    val recipe = RecipeSource().loadRecipes().firstOrNull { it.id == recipeId }

    LazyColumn {
        if (recipe != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = recipe.imageResourceId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Text(
                        text = recipe.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 30.sp
                    )

                    Text(
                        text = recipe.cookingTime,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "recipeid: $recipeId",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = recipe.recipe_instructions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "du har kommet til feil side as",
                    color = Color.Black,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}


