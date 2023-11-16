package no.hiof.reciperiot.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import no.hiof.reciperiot.R
import no.hiof.reciperiot.ViewModels.RecipeViewModel
import no.hiof.reciperiot.ViewModels.HomeViewModel
import okhttp3.OkHttpClient

@Composable
fun HomeScreen(navController: NavController, snackbarHost : SnackbarHostState, client: OkHttpClient, modifier: Modifier = Modifier, db: FirebaseFirestore, homeViewModel: HomeViewModel = viewModel()) {
    homeViewModel.getIngredientsFromFirestore(db)

    // Til snackbar
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(modifier = modifier.padding(horizontal = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)){
            Text(stringResource(R.string.home_options), fontSize = 20.sp)
            TimeInput(text = stringResource(R.string.home_options_time), state = homeViewModel.time)
            Text(stringResource(R.string.home_ingredients), fontSize = 20.sp)
            LazyVerticalGrid(columns = GridCells.Adaptive(90.dp),
                content = {
                    items(homeViewModel.ingredients.size) {index ->
                        Text(homeViewModel.ingredients[index])
                    }
                }
            )
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                scope.launch {
                    homeViewModel.buttonEnabled.value = false
                    snackbarHost.showSnackbar("Genererer oppskrift")
                }
                /*ChatGPT*/
                scope.launch {
                    val newRecipes = homeViewModel.generateGPT(client, homeViewModel.ingredients, homeViewModel.time.value)
                    //homeViewModel.recipes.value = newRecipes
                    homeViewModel.handleFirestoreAdd(newRecipes[0], db)
                    homeViewModel.buttonEnabled.value = true
                    snackbarHost.showSnackbar("Oppskrift generert")
                }
            },
                enabled = homeViewModel.buttonEnabled.value) {
                Text(stringResource(R.string.home_generate))
            }
        }
        RecipeList(
            recipes = homeViewModel.recipes.value,
            navController = navController,
            onFavouriteToggle = {},
            updateRecipeFavouriteStatus = {recipe, fav ->
                RecipeViewModel().updateRecipeFavouriteStatus(recipe, db, fav)
                Log.e("FirestoreError", "Error fetching data: ${homeViewModel.recipes.value}")
            }
        )
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