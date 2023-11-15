package no.hiof.reciperiot.screens
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import no.hiof.reciperiot.ViewModels.IngredientsViewModel



/* Commented out for extrating to viewmodel
fun deleteIngredientFromDb(db: FirebaseFirestore, ingredientName: String) {
    val user = Firebase.auth.currentUser
    val docRef = user?.let { db.collection("ingredients").document(it.uid) }

    // Delete the specific field from the document
    docRef?.update(ingredientName, FieldValue.delete())
        ?.addOnSuccessListener {
            Log.d(TAG, "Ingredient deleted successfully")
        }
        ?.addOnFailureListener { e ->
            Log.w(TAG, "Error deleting ingredient", e)
        }

}
*/

// Saves names and checked states to fireStore
fun saveIngredientstoDb(db: FirebaseFirestore, ingredientList: List<Pair<String, Boolean>>) {
    val user = Firebase.auth.currentUser
    //TODO: ensure logged in
    val docRef = user?.let { db.collection("ingredients").document(it.uid) }
    val data = hashMapOf<String, Any>()
    for ((name, checked) in ingredientList) {
        data[name] = checked
    }
    //TODO: ensure logged in
    if (docRef != null) {
        docRef.set(data)
            .addOnSuccessListener {docRef ->
                Log.d(TAG,"DocumentSnapcshot added!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

fun getIngredients(db: FirebaseFirestore, callback: (Map<String, Any>?) -> Unit) {
    val user = Firebase.auth.currentUser
    // TODO: Ensure logged in
    val docRef = user?.let { db.collection("ingredients").document(it.uid) }
    docRef?.get()?.addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            callback(document.data)
        } else {
            val emptyData = emptyMap<String, Any>()
            docRef?.set(emptyData)
                ?.addOnSuccessListener {
                    callback(emptyData)
                }
                ?.addOnFailureListener { exception ->
                    Log.d(TAG, "Error: Could not create doc", exception)
                    callback(null)
                }
        }
    }?.addOnFailureListener { exception ->
        Log.d(TAG, "get failed with ", exception)
        callback(null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(modifier: Modifier = Modifier, snackbarHost : SnackbarHostState, db: FirebaseFirestore, ingredientScreenViewModel: IngredientsViewModel = viewModel()) {


    //Fetch data from Firestore
    getIngredients(db) { data ->
        if (data != null) {
            val firestoreIngredients = data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
            ingredientScreenViewModel.ingredientsList = firestoreIngredients
        } else {
            println("No data or error")
        }
    }

    //Til snackbar
    val scope = rememberCoroutineScope()

    val saveIngredients = {
        val ingredientsToSave = ingredientScreenViewModel.ingredientsList.map { (name, checkedState) ->
            name to checkedState.value
        }
        saveIngredientstoDb(db, ingredientsToSave)

        scope.launch{
            snackbarHost.showSnackbar("Saved ingredients!")
        }

    }

    // Counter for å genere rader for lazyColumn
    val rowCount = 1

    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            items(count = rowCount) { item ->

                // Input field for adding new ingredients
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = ingredientScreenViewModel.newIngredient,
                        onValueChange = { ingredientScreenViewModel.newIngredient = it },
                        label = { Text("Add an ingredient") }
                    )
                    Button(onClick = {
                        if (ingredientScreenViewModel.newIngredient.isNotBlank()) {
                            ingredientScreenViewModel.ingredientsList = ingredientScreenViewModel.ingredientsList.toMutableList()
                                .plus(ingredientScreenViewModel.newIngredient to mutableStateOf(true))
                            ingredientScreenViewModel.newIngredient = ""

                            scope.launch {
                                snackbarHost.showSnackbar("Ingredient added")
                            }
                        }
                    }) {
                        Text(text = "Add")
                    }
                }


                ingredientScreenViewModel.ingredientsList.forEach { (name, checkedState) ->
                    ingredientScreenViewModel.IngredientRow(

                        name = name,
                        checkedState = checkedState,
                        onCheckedChange = { newValue ->
                            checkedState.value = newValue
                        }
                    )
                }
            }
        }


        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center) {

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
                ) {
                FloatingActionButton(
                    onClick = { saveIngredients() },
                    modifier = modifier
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,

                    ) {
                    Icon(Icons.Filled.Add, "Floating action button")
                }
            }

        }


    }

}


// Preview does not work
@Preview(showBackground = true)
@Composable
fun IngredientScreenPreview() {
    AppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val db = Firebase.firestore
        IngredientsScreen(snackbarHost = snackbarHostState, db = db)
    }
}
