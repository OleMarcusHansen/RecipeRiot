package no.hiof.reciperiot.screens
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

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

// Saves names and checked states to fireStore
fun saveIngredientstoDb(db: FirebaseFirestore, ingredientList: List<Pair<String, Boolean>>) {
    val docRef = db.collection("ingredients").document("userIngredients")
    val data = hashMapOf<String, Any>()
    for ((name, checked) in ingredientList) {
        data[name] = checked
    }
    docRef.set(data)
        .addOnSuccessListener {docRef ->
            Log.d(TAG,"DocumentSnapcshot added!")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(snackbarHost : SnackbarHostState, db: FirebaseFirestore, modifier: Modifier = Modifier) {
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

    //Til snackbar
    val scope = rememberCoroutineScope()

    val saveIngredients = {
        val ingredientsToSave = ingredientsList.map { (name, checkedState) ->
            name to checkedState.value
        }
        saveIngredientstoDb(db, ingredientsToSave)

        scope.launch{
            snackbarHost.showSnackbar("Saved ingredients!")
        }
    }

    // Counter for å genere rader for lazyColumn
    val rowCount = 1

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {items(count = rowCount) {item ->

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

                    scope.launch{
                        snackbarHost.showSnackbar("Ingredient added")
                    }
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


        FloatingActionButton(onClick = { saveIngredients()
        }) {
            Text(text = "Save ingredients")
        }


    }

    }

}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        IngredientsScreen(snackbarHost = SnackbarHostState())
    }
}

*/