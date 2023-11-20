package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.ViewModels.RecipeViewModel
import no.hiof.reciperiot.data.RecipeRepository
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun RecipePage1(navController: NavController, recipeId: String, db: FirebaseFirestore, RecipeViewModel: RecipeViewModel = viewModel()) {
    val recipeRepository = remember { RecipeRepository() }
    val recipe = recipeRepository.loadRecipes().firstOrNull { it.id == recipeId }

    LazyColumn {
        if (recipe != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = recipe.title,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(300.dp),
                            fontSize = 30.sp
                        )
                        var favourite by remember { mutableStateOf(recipe.favourite) }
                        IconToggleButton(
                            checked = favourite,
                            onCheckedChange = {
                                favourite = !favourite

                                RecipeViewModel.updateRecipeFavouriteStatus(recipe, db, favourite)
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
                    }

                    AsyncImage(
                        model = recipe.imageURL,
                        contentDescription = "Image of the recipe",
                        modifier = Modifier.height(400.dp)
                    )
                    Text(
                        text = "Cooktime: ${recipe.cookingTime}",
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
                        text = "Ingredients:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        fontSize = 26.sp
                    )
                    var ingredients: JSONArray = JSONArray("[\"N/A\"]")
                    try{
                        ingredients = JSONArray(recipe.recipe_ingredients)
                    }
                    catch (e: Exception){
                        print(e)
                    }
                    for (i in 0 until ingredients.length()){
                        Text(
                            text=ingredients[i].toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            fontSize = 20.sp
                        )
                    }

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
                    text = "du har kommet til feil side as, siden fikk ikke recipe," +
                            "enten ligger den ikke i db eller så er det en annen feil"+
                    "mest sannsynlig er dette en formateringsfeil av jsonfilen fra chatgpt",
                    color = Color.Black,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Text(
                    text = "recipeid: ${recipe?.id}",
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


