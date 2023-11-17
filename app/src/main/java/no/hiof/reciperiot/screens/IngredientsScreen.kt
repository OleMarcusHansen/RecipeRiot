package no.hiof.reciperiot.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import no.hiof.reciperiot.Screen
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

/* Commented out for extrating to viewmodel
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
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "DocumentSnapcshot added!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

 */

// Used in HomeViewModel to get ingredients from firestore
fun getIngredients(db: FirebaseFirestore, callback: (Map<String, Any>?) -> Unit) {
    val user = Firebase.auth.currentUser
    // TODO: Ensure logged in
    val docRef = user?.let { db.collection("ingredients").document(it.uid) }
    docRef?.get()?.addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            callback(document.data)
        } else {
            val emptyData = emptyMap<String, Any>()
            docRef.set(emptyData)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IngredientRow(
    name: String,
    checkedState: MutableState<Boolean>,
    ingredientViewModel: IngredientsViewModel = viewModel(),
    onCheckedChange: (Boolean) -> Unit)
{

    val haptics = LocalHapticFeedback.current
    var expandedMenu by remember { mutableStateOf(false)}
    var menuRowId by rememberSaveable { mutableStateOf(name) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = name,
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        menuRowId = name
                        expandedMenu = true
                        haptics.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                    }
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { newValue ->
                onCheckedChange(newValue)
            },
            modifier = Modifier.size(24.dp)
        )

        DropdownMenu(expanded = expandedMenu, onDismissRequest = {expandedMenu = false}) {
            // When clicking delete, delete ingrident from firebase
            DropdownMenuItem(text = { Text(text = "Delete") }, onClick = {
                menuRowId.let { ingredientName ->
                    ingredientViewModel.deleteIngredient(ingredientName)
                }
            })

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    db: FirebaseFirestore,
    ingredientScreenViewModel: IngredientsViewModel = viewModel()
) {


    //Fetch data from Firestore
    ingredientScreenViewModel.getIngredientsToIngredientScreen() { data ->
        if (data != null) {
            val firestoreIngredients =
                data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
            ingredientScreenViewModel.ingredientsList = firestoreIngredients
        } else {
            println("No data or error")
        }
    }

    //Til snackbar
    val scope = rememberCoroutineScope()

    val saveIngredients = {
        val ingredientsToSave =
            ingredientScreenViewModel.ingredientsList.map { (name, checkedState) ->
                name to checkedState.value
            }
        ingredientScreenViewModel.saveIngredientsToDb(ingredientsToSave)

    }

    val reRoutetoHomeScreen = {

        scope.launch {
            navController.navigate(Screen.Home.route)
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

                    // Add ingredient button, adds ingredient to list
                    Button(onClick = {
                        if (ingredientScreenViewModel.newIngredient != "") {
                            ingredientScreenViewModel.ingredientsList =
                                ingredientScreenViewModel.ingredientsList + Pair(
                                    ingredientScreenViewModel.newIngredient,
                                    mutableStateOf(true)
                                )

                            // save new ingredient to firestore
                            saveIngredients()

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


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(
                    onClick = { saveIngredients()
                              reRoutetoHomeScreen()},
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
