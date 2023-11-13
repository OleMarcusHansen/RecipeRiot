package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.data.RecipeSource
import org.json.JSONObject

@Composable
fun RecipePage1(navController: NavController, recipeId: String, db: FirebaseFirestore) {
    val recipeSource = remember { RecipeSource() }
    val recipe = recipeSource.loadRecipes().firstOrNull { it.id == recipeId }
    var favourite by rememberSaveable { mutableStateOf(recipe?.favourite ?: false) }

    LazyColumn {
        if (recipe != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    /*Image(
                        painter = painterResource(id = recipe.imageResourceId),
                        //painter = painterResource(id = R.drawable.food),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )*/
                    AsyncImage(model = recipe.imageURL, contentDescription = "Image of the recipe")
                    IconToggleButton(
                        checked = favourite,
                        onCheckedChange = {
                            favourite = !favourite
                            if (favourite) {
                                updateRecipeFavouriteStatus(recipe, db, favourite)
                            } else {
                                updateRecipeFavouriteStatus(recipe, db, favourite)
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = if (favourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
                        text = "${recipe.favourite}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "recipeid: ${recipe.id}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Nutrition:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 26.sp
                    )
                    var calories = "N/A"
                    var protein = "N/A"
                    var carbohydrates = "N/A"
                    var fat = "N/A"
                    try{
                        val nutrition = JSONObject(recipe.recipe_nutrition)
                        calories = nutrition.getString("calories")
                        protein = nutrition.getString("protein")
                        carbohydrates = nutrition.getString("carbohydrates")
                        fat = nutrition.getString("fat")
                    }
                    catch (e: Exception){
                        print(e)
                    }
                    Text(
                        text = "Calories: $calories",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Protein: $protein",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Carbohydrates: $carbohydrates",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Fat: $fat",
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
                    Text(
                        text = recipe.userid,
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


