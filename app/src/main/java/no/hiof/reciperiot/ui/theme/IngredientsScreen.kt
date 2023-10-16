package no.hiof.reciperiot.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IngredientRow(name: String, checkedState: MutableState<Boolean>,
                  onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { newValue ->
                onCheckedChange(newValue)
            },
            modifier = Modifier.size(24.dp)
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(modifier: Modifier = Modifier) {
    var newIngredient by remember { mutableStateOf("") }
    var ingredientsList by remember {
        mutableStateOf(
            listOf(
                "Løk" to mutableStateOf(false),
                "Poteter" to mutableStateOf(false),
                "Mel" to mutableStateOf(false),
                "Melk" to mutableStateOf(false),
                "Kjøttdeig" to mutableStateOf(false)
            )
        )
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Input field for adding new ingredients
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = newIngredient,
                onValueChange = { newIngredient = it },
                label = { Text("Add an ingredient") }
            )
            Button(onClick = {
                if (newIngredient.isNotBlank()) {
                    ingredientsList = ingredientsList.toMutableList().plus(newIngredient to mutableStateOf(true))
                    newIngredient = ""
                }
            }) {
                Text(text = "Add")
            }
        }

        ingredientsList.forEach { (name, checkedState) ->
            IngredientRow(
                name = name,
                checkedState = checkedState,
                onCheckedChange = { newValue ->
                    checkedState.value = newValue
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipeRiotTheme {
        IngredientsScreen()
    }
}
