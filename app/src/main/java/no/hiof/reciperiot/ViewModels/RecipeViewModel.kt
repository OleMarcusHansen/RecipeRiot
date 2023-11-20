package no.hiof.reciperiot.ViewModels

import androidx.lifecycle.ViewModel
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
