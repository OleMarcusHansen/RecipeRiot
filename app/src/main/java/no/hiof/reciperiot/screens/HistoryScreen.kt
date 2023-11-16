package no.hiof.reciperiot.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.ViewModels.RecipeViewModel

@Composable
fun HistoryScreen(navController: NavController, db: FirebaseFirestore, RecipeViewModel: RecipeViewModel = viewModel()) {
    RecipeList(
        recipes = RecipeViewModel.recipes,
        navController = navController,
        onFavouriteToggle = {
        },
        updateRecipeFavouriteStatus = { recipe, fav ->
            RecipeViewModel.updateRecipeFavouriteStatus(recipe, db, fav)
        }

    )
}
