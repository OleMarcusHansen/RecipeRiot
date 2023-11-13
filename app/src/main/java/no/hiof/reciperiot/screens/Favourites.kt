package no.hiof.reciperiot.screens

import android.content.ContentValues.TAG
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import no.hiof.reciperiot.Screen
import no.hiof.reciperiot.data.RecipeSource
import no.hiof.reciperiot.model.Recipe
import org.json.JSONObject


@Composable
fun FavouriteMeals(navController: NavController, db: FirebaseFirestore) {


    val recipes by remember { mutableStateOf(RecipeSource().loadRecipes()) }

    RecipeList(
        recipes = recipes,
        navController = navController,
        onFavouriteToggle = {
        },
        updateRecipeFavouriteStatus = { recipe ->
            updateRecipeFavouriteStatus(recipe, db)
        }

    )
}

fun updateRecipeFavouriteStatus(recipe: Recipe, db: FirebaseFirestore) {
    val docid = recipe.id
    val updatedRecipe = mapOf("favourite" to !recipe.favourite)
    val user = com.google.firebase.ktx.Firebase.auth.currentUser

    if (user != null) {
        db.collection("FavouriteMeals")
            .document(docid)
            .set(updatedRecipe, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Recipe favourite updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating recipe favourite", e)
            }
    }
}

fun firestoreCleanup(db: FirebaseFirestore) {
    val user = com.google.firebase.ktx.Firebase.auth.currentUser
    val query = db.collection("FavouriteMeals")
        .whereEqualTo("userid", user?.uid)
        .whereEqualTo("favourite", false)
    query.get()
        .addOnSuccessListener { documents ->
            val batch = db.batch()
            for (document in documents) {
                Log.d(TAG, "$document")
                val documentRef = db.collection("FavouriteMeals").document(document.id)
                Log.d(TAG, "Deleting document with ID: ${document.id}, Data: ${document.data}")

                batch.delete(documentRef)
            }

            batch.commit()
                .addOnSuccessListener {
                    Log.d(TAG, "Documents successfully deleted")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting documents", e)
                }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error getting documents", e)
        }
}




@Composable
fun RecipeList(
    recipes: List<Recipe>,
    navController: NavController,
    onFavouriteToggle: (Recipe) -> Unit,
    updateRecipeFavouriteStatus: (Recipe) -> Unit,
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
    updateRecipeFavouriteStatus: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        if (favourite) {
                            updateRecipeFavouriteStatus(recipe)
                        } else {
                            updateRecipeFavouriteStatus(recipe)

                        }
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