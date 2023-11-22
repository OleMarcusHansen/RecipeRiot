package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.hiof.reciperiot.R
import no.hiof.reciperiot.ViewModels.FavouriteViewModel
import no.hiof.reciperiot.composables.RecipeList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteMeals(navController: NavController, favouriteViewModel: FavouriteViewModel = viewModel()) {
    Column {
        TextField(
            value = favouriteViewModel.searchText,
            onValueChange = { newText ->
                favouriteViewModel.searchText = newText
            },
            label = { Text(stringResource(R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))

        RecipeList(
            recipes = favouriteViewModel.favourites.filter { it.title.contains(favouriteViewModel.searchText, true) },
            navController = navController,
            onFavouriteToggle = {},
            updateRecipeFavouriteStatus = { recipe, fav ->
                favouriteViewModel.updateRecipeFavouriteStatus(recipe, fav)
            }
        )
    }
}