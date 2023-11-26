package no.hiof.reciperiot.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.hiof.reciperiot.R
import no.hiof.reciperiot.Screen
import no.hiof.reciperiot.ViewModels.IngredientsViewModel

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
    ingredientsViewModel: IngredientsViewModel = viewModel()
) {

    //Fetch data from Firestore
    ingredientsViewModel.updateIngredientsList()

    //Til snackbar
    val scope = rememberCoroutineScope()

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
                        value = ingredientsViewModel.newIngredient,
                        onValueChange = { ingredientsViewModel.newIngredient = it },
                        label = { Text(stringResource(R.string.add_an_ingredient)) }
                    )

                    // Add ingredient button, adds ingredient to list
                    Button(onClick = {
                        if (ingredientsViewModel.newIngredient != "") {
                            ingredientsViewModel.ingredientsList =
                                ingredientsViewModel.ingredientsList + Pair(
                                    ingredientsViewModel.newIngredient,
                                    mutableStateOf(true)
                                )

                            // save new ingredient to firestore
                            ingredientsViewModel.saveIngredientsToDb()

                            ingredientsViewModel.newIngredient = ""

                            scope.launch {
                                snackbarHost.showSnackbar("Ingredient added")
                            }
                        }
                    }) {
                        Text(text = "Add")
                    }
                }


                ingredientsViewModel.ingredientsList.forEach { (name, checkedState) ->
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
                    onClick = { ingredientsViewModel.saveIngredientsToDb()
                              reRoutetoHomeScreen()},
                    modifier = modifier
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,

                    ) {
                    //Icon(Icons.Filled.Add, "Floating action button")
                    Text(stringResource(R.string.save), modifier = Modifier.padding(16.dp))
                }
            }

        }


    }

}
