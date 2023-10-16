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
import androidx.navigation.NavController

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
                val newRecipes = listOf(Recipe(2, "gpt-generert mat", R.drawable.food, "12min", false))
                recipes.value = newRecipes
            }) {
                Text("Generer oppskrift")
            }
        }
        //RecipeList(recipes = recipes)
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
/*
@Composable
fun RecipeList(recipes : MutableState<List<Recipe>>){
    if (recipes.value.isNotEmpty()){
        Column {
            recipes.value.forEach {recipe ->
                RecipeCard(recipe = recipe, navController = navController)
                })
            }
        }
    }
}
 */
