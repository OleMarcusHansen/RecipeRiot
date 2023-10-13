package no.hiof.reciperiot.ui.theme.data

import no.hiof.reciperiot.R
import no.hiof.reciperiot.ui.theme.model.Recipe

class RecipeSource() {
    fun loadRecipes(): List<Recipe> {
        return listOf<Recipe>(
            Recipe(1,"mat", R.drawable.food, "45min", true)
        )
    }
}