package no.hiof.reciperiot.data

import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe

class RecipeSource() {
    private val recipes: MutableList<Recipe> = mutableListOf(
        Recipe(1,"mat", R.drawable.food, "45min", true, "dsf"),
        Recipe(2, "pizza", R.drawable.food, "30min", false,
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n" +
                    "4. Add a few slices of cheese on top of the turkey ham.\n\n" +
                    "5. Thinly slice some onions and place them on the cheese.\n\n" +
                    "6. Add a few pickles for some extra flavor.\n\n" +
                    "7. Top each sandwich with another slice of bread to form a sandwich.\n\n"),
        Recipe(3, "hamburger", R.drawable.hamburger, "2000min", true, "bare lag den bror")
    )
    fun loadRecipes(): List<Recipe> {
        return recipes.toList()
    }

    fun updateRecipe(recipeId: Int, isFavourite: Boolean) {
        recipes.find { it.id == recipeId }?.isFavourite = isFavourite
    }
}