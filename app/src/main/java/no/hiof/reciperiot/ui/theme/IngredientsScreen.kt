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
@Composable
fun IngredientsScreen(modifier: Modifier = Modifier) {
    var displayedText by remember { mutableStateOf("") }

    // Create a list of ingredients with their individual states
    val ingredients = listOf(
        "Løk" to remember { mutableStateOf(false) },
        "Poteter" to remember { mutableStateOf(false) },
        "Mel" to remember { mutableStateOf(false) },
        "Melk" to remember { mutableStateOf(false) },
        "Kjøttdeig" to remember { mutableStateOf(false) }
    )

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ingredients.forEach { (name, checkedState) ->
            IngredientRow(
                name = name,
                checkedState = checkedState,
                onCheckedChange = { newValue ->
                    checkedState.value = newValue
                }
            )
        }

        Button(onClick = {
            displayedText = "Ingredienser added!"
        }) {
            Text(text = "Legg til ingredienser")
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
