package no.hiof.reciperiot.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.R
import no.hiof.reciperiot.data.RecipeSource
import no.hiof.reciperiot.model.Recipe

@Composable
fun RecipePage1(navController: NavController, recipeId: Int, db: FirebaseFirestore) {
    val recipeSource = remember { RecipeSource() }
    val recipe = recipeSource.loadRecipes().firstOrNull { it.id == recipeId }
    var isFavourite by rememberSaveable { mutableStateOf(recipe?.isFavourite ?: false) }

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
                        //painter = painterResource(id = R.drawable.food),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    IconToggleButton(
                        checked = isFavourite,
                        onCheckedChange = {
                            isFavourite = !isFavourite
                            if (isFavourite) {
                                handleFirestoreAdd(recipe, db)
                            } else {
                                handleFirestoreRemove(recipe, db)
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = 1.3f
                                    scaleY = 1.3f
                                },
                        )
                    }

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
                        text = "${recipe.isFavourite}",
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
                        text = "Instruksjoner:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 26.sp
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


