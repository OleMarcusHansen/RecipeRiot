package no.hiof.reciperiot.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.hiof.reciperiot.R
import no.hiof.reciperiot.Screen
import no.hiof.reciperiot.ui.theme.model.Recipe
import org.json.JSONObject

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val time = remember { mutableStateOf("") }
    val paprika = remember { mutableStateOf(false) }

    val recipes = remember { mutableStateOf(emptyList<Recipe>()) }

    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.home_options), fontSize = 20.sp)
        TimeInput(text = stringResource(R.string.home_options_time), state = time)
        Text(stringResource(R.string.home_ingredients), fontSize = 20.sp)
        Ingredient(text = "Paprika", state = paprika)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                /*ChatGPT*/
                val newRecipes = generateGPT()
                recipes.value = newRecipes
            }) {
                Text("Generer oppskrift")
            }
        }
        RecipeList(recipes = recipes)
    }
}

@Composable
fun TimeInput(text : String, state : MutableState<String>){
    Row(){
        Text(text)
        BasicTextField(value = state.value,
            onValueChange = {newText -> state.value = newText},
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, Color.Gray),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun Ingredient(text : String, state : MutableState<Boolean>){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        Text(text)
        Checkbox(
            checked = state.value,
            onCheckedChange = { newValue ->
                state.value = newValue
            },
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun RecipeList(recipes : MutableState<List<Recipe>>){
    if (recipes.value.isNotEmpty()){
        Column {
            recipes.value.forEach {recipe ->
                recipeCard(recipe = recipe)
            }
        }
    }
}

fun generateGPT() : List<Recipe>{
    val response = """{
          "recipe_name": "Turkey Ham and Cheese Panini",
          "recipe_time": "20 minutes",
          "recipe_instructions": [
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.",
            "2. Take 2 slices of bread and lay them out on a clean surface.",
            "3. Place a slice of turkey ham on each of the bread slices.",
            "4. Add a few slices of cheese on top of the turkey ham.",
            "5. Thinly slice some onions and place them on the cheese.",
            "6. Add a few pickles for some extra flavor.",
            "7. Top each sandwich with another slice of bread to form a sandwich.",
            "8. If you have a panini press, place the sandwiches inside and cook for about 4-5 minutes until the bread is toasted and the cheese is melted. If you're using a stovetop grill pan, place the sandwiches on the hot pan and press them down with a heavy object (like a cast-iron skillet) to get that signature panini press effect. Cook for 2-3 minutes on each side until the bread is toasted and the cheese is melted.",
            "9. Carefully remove the panini from the press or grill pan and let them cool slightly before cutting in half.",
            "10. Serve with a side of Doritos or enjoy your Turkey Ham and Cheese Panini by itself!"
          ],
          "recipe_nutrition": {
            "calories": "Approximately 400-500 calories per serving",
            "carbohydrates": "Approximately 35-45g per serving",
            "protein": "Approximately 15-20g per serving",
            "fat": "Approximately 20-25g per serving",
            "fiber": "Approximately 2-4g per serving"
          }
    }"""
    val generatedjson = JSONObject(response)

    val recipes = listOf(Recipe(2, generatedjson.getString("recipe_name"), R.drawable.food, generatedjson.getString("recipe_time"), false))
    return recipes
}