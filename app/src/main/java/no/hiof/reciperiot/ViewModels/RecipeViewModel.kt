package no.hiof.reciperiot.ViewModels

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.data.RecipeRepository
import no.hiof.reciperiot.model.Recipe

class RecipeViewModel() : ViewModel() {
    private val recipeRepository = RecipeRepository()

    fun loadRecipe(recipeId: String): Recipe?{
        return recipeRepository.loadRecipes().firstOrNull { it.id == recipeId }
    }

    fun updateRecipeFavouriteStatus(recipe: Recipe, fav: Boolean) {
        recipeRepository.updateRecipeFavouriteStatus(recipe, fav)
    }
}
