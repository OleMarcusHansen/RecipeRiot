package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.ViewModels.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, db: FirebaseFirestore, RecipeViewModel: RecipeViewModel = viewModel()) {

    Column {
        TextField(
            value = RecipeViewModel.searchText,
            onValueChange = { newText ->
                RecipeViewModel.searchText = newText
            },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))

        RecipeList(
            recipes = RecipeViewModel.history.filter { it.title.contains(RecipeViewModel.searchText, true) },
            navController = navController,
            onFavouriteToggle = {},
            updateRecipeFavouriteStatus = { recipe, fav ->
                RecipeViewModel.updateRecipeFavouriteStatus(recipe, db, fav)
            }
        )
    }
}
