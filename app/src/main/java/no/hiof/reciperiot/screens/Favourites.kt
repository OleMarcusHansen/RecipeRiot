package no.hiof.reciperiot.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import no.hiof.reciperiot.R
import no.hiof.reciperiot.Screen
import no.hiof.reciperiot.data.RecipeSource
import no.hiof.reciperiot.model.Recipe



@Composable
fun FavouriteMeals(navController: NavController, db: FirebaseFirestore) {


    val recipes by remember { mutableStateOf(RecipeSource().loadRecipes()) }

    RecipeList(
        recipes = recipes,
        navController = navController,
        onFavouriteToggle = {
        },
        onAddToFavorites = { recipe ->
            handleFirestoreAdd(recipe, db)
        },
        onRemoveFromFavorites = { recipe ->
            handleFirestoreRemove(recipe, db)
        }
    )
}
private fun updateRecipeId(recipe: Recipe, documentId: String, db: FirebaseFirestore) {
    val updatedRecipe = mapOf("id" to documentId)
    val user = com.google.firebase.ktx.Firebase.auth.currentUser

    if (user != null) {
        db.collection("FavouriteMeals")
            .document(documentId)
            .set(updatedRecipe, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Recipe ID updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating recipe ID", e)
            }
    }
}
fun handleFirestoreAdd(recipe: Recipe, db: FirebaseFirestore) {
    val user = com.google.firebase.ktx.Firebase.auth.currentUser

    val recipeadd = mapOf(
        "id" to "",
        "title" to recipe.title,
        "imageResourceId" to recipe.imageResourceId,
        "cookingTime" to recipe.cookingTime,
        "isFavourite" to recipe.isFavourite,
        "recipe_instructions" to recipe.recipe_instructions,
        "userid" to recipe.userid
    )

    db.collection("FavouriteMeals")
        .add(recipeadd)
        .addOnSuccessListener { documentReference ->
            val updatedRecipe = recipe.copy(id = documentReference.id)
            updateRecipeId(updatedRecipe, documentReference.id, db)
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
}

fun handleFirestoreRemove(recipe: Recipe, db: FirebaseFirestore) {
    val user = com.google.firebase.ktx.Firebase.auth.currentUser
    Log.d(TAG, "Before get()")
    db.collection("FavouriteMeals")
        .whereEqualTo("userid", user?.uid)
        .whereEqualTo("id", recipe.id)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                // Get the document ID
                val documentId = document.id

                // Delete the document based on the document ID
                db.collection("FavouriteMeals")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully deleted with ID: $documentId")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting document with ID: $documentId", e)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error getting documents", e)
        }
    Log.d(TAG, "After get()")
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    navController: NavController,
    onFavouriteToggle: (Recipe) -> Unit,
    onAddToFavorites: (Recipe) -> Unit,
    onRemoveFromFavorites: (Recipe) -> Unit,
    modifier: Modifier = Modifier) {
    LazyColumn(userScrollEnabled = true, modifier = modifier) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe,
                onRecipeClick = { selectedRecipe ->
                    navController.navigate("${Screen.RecipePage.route}/${selectedRecipe}")
                },
                onFavouriteToggle = onFavouriteToggle,
                onAddToFavorites = onAddToFavorites,
                onRemoveFromFavorites = onRemoveFromFavorites
            )
        }
    }
}
@Composable
fun RecipeCard(
    recipe: Recipe,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (Recipe) -> Unit,
    onAddToFavorites: (Recipe) -> Unit,
    onRemoveFromFavorites: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavourite by rememberSaveable { mutableStateOf(recipe.isFavourite) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onRecipeClick(recipe.id) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                //painter = painterResource(id = recipe.imageResourceId),
                painter = painterResource(id = R.drawable.food),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconToggleButton(
                    checked = isFavourite,
                    onCheckedChange = {
                        isFavourite = !isFavourite
                        onFavouriteToggle(recipe.copy(isFavourite = isFavourite))
                        if (isFavourite) {
                            onAddToFavorites(recipe)
                        } else {
                            onRemoveFromFavorites(recipe)
                        }
                        Log.d("RecipeCard", "Favourite toggled for recipe: ${recipe.title}, isFavourite: $isFavourite")
                    }) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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