package no.hiof.reciperiot.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.Screen
import no.hiof.reciperiot.ViewModels.RecipeViewModel
import no.hiof.reciperiot.model.Recipe
import org.json.JSONObject



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteMeals(navController: NavController, db: FirebaseFirestore, RecipeViewModel: RecipeViewModel = viewModel()) {

    Column {
        TextField(
            value = RecipeViewModel.searchText,
            onValueChange = { newText ->
                RecipeViewModel.searchText = newText
            },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))

        RecipeList(
            recipes = RecipeViewModel.recipes.filter { it.title.contains(RecipeViewModel.searchText, true) },
            navController = navController,
            onFavouriteToggle = {},
            updateRecipeFavouriteStatus = { recipe, fav ->
                RecipeViewModel.updateRecipeFavouriteStatus(recipe, db, fav)
            }
        )
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    navController: NavController,
    onFavouriteToggle: (Recipe) -> Unit,
    updateRecipeFavouriteStatus: (Recipe, Boolean) -> Unit,
    modifier: Modifier = Modifier) {



    LazyColumn(userScrollEnabled = true, modifier = modifier) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe,
                onRecipeClick = { selectedRecipe ->
                    navController.navigate("${Screen.RecipePage.route}/${selectedRecipe}")
                },
                onFavouriteToggle = onFavouriteToggle,
                updateRecipeFavouriteStatus = updateRecipeFavouriteStatus
            )
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (Recipe) -> Unit,
    updateRecipeFavouriteStatus: (Recipe, Boolean) -> Unit,
    modifier: Modifier = Modifier) {

    var favourite by rememberSaveable { mutableStateOf(recipe.favourite) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onRecipeClick(recipe.id) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.width(200.dp)){
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(recipe.cookingTime)
                var calories = "N/A"
                try {
                    calories = JSONObject(recipe.recipe_nutrition).getString("calories")
                }
                catch (e: Exception){
                    print(e)
                }
                Text("Calories: $calories")
            }
            Column(modifier = Modifier.width(with(LocalDensity.current) { 256.toDp() }),
                horizontalAlignment = Alignment.End){
                AsyncImage(model = recipe.imageURL, contentDescription = "Image of the recipe")
                IconToggleButton(
                    checked = favourite,
                    onCheckedChange = {
                        favourite = !favourite
                        onFavouriteToggle(recipe.copy(favourite = favourite))

                        updateRecipeFavouriteStatus(recipe, favourite)

                        Log.d("RecipeCard", "Favourite toggled for recipe: ${recipe.title}, favourite: $favourite")
                    }) {
                    Icon(
                        imageVector = if (favourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = modifier.graphicsLayer {
                            scaleX = 1.3f
                            scaleY = 1.3f
                        },
                    )
                }
            }
        }
    }
}