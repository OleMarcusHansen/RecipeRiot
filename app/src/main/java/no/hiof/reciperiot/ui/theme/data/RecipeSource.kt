package no.hiof.reciperiot.ui.theme.data

import no.hiof.reciperiot.R
import no.hiof.reciperiot.ui.theme.model.Recipe

class RecipeSource() {
    fun loadRecipes(): List<Recipe> {
        return listOf<Recipe>(
            Recipe(1,"mat", R.drawable.food, "45min", true, "dsf"),
            Recipe(2, "pizza", R.drawable.food, "30min", false,
                    "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n" +
                    "4. Add a few slices of cheese on top of the turkey ham.\n\n" +
                    "5. Thinly slice some onions and place them on the cheese.\n\n" +
                    "6. Add a few pickles for some extra flavor.\n\n" +
                    "7. Top each sandwich with another slice of bread to form a sandwich.\n\n" +
                    "8. If you have a panini press, place the sandwiches inside and cook for about 4-5 minutes until the bread is toasted and the cheese is melted. If you're using a stovetop grill pan, place the sandwiches on the hot pan and press them down with a heavy object (like a cast-iron skillet) to get that signature panini press effect. Cook for 2-3 minutes on each side until the bread is toasted and the cheese is melted.\n\n" +
                    "9. Carefully remove the panini from the press or grill pan and let them cool slightly before cutting in half.\n\n" +
                    "10. Serve with a side of Doritos or enjoy your Turkey Ham and Cheese Panini by itself!"),
            Recipe(3, "hamburger", R.drawable.hamburger, "2000min", true, "bare lag den bror")
        )
    }
}