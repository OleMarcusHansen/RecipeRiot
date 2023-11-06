package no.hiof.reciperiot.data

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe

class RecipeSource1() {
    private val recipes: MutableList<Recipe> = mutableListOf(
        Recipe(1,"mat", R.drawable.food,
            "45min", true, "dsf"),
        Recipe(2, "pizza", R.drawable.food,
            "30min", false,
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n" +
                    "4. Add a few slices of cheese on top of the turkey ham.\n\n" +
                    "5. Thinly slice some onions and place them on the cheese.\n\n" +
                    "6. Add a few pickles for some extra flavor.\n\n" +
                    "7. Top each sandwich with another slice of bread to form a sandwich.\n\n"),
        Recipe(3, "hamburger", R.drawable.hamburger,
            "2000min", true, "bare lag den bror")
    )
    fun loadRecipes(): List<Recipe> {
        return recipes.toList()
    }

    fun updateRecipe(recipeId: Int, isFavourite: Boolean) {
        recipes.find { it.id == recipeId }?.isFavourite = isFavourite
    }
}

class RecipeSource() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = firestore.collection("FavouriteMeals")
    private val recipes: MutableList<Recipe> = mutableListOf()

    init {
        // Attach a listener to fetch recipes from Firestore
        collectionReference.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("FirestoreError", "Error fetching data: ${exception.message}")
                return@addSnapshotListener
            }

            recipes.clear()
            Log.d("FirestoreData", "Data fetched successfully. Num77777ber of recipes: ${recipes.size}")

            snapshot?.documents?.forEach { documentSnapshot ->
                Log.d("FirestoreData", "Data fetched successfully. Num66667ber of recipes: ${recipes.size}")
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                Log.d("FirestoreData", "Data fetched successfully. Num55555ber of recipes: ${recipes.size}")

                recipe?.let { recipes.add(it) }
            }

            Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${recipes.size}")
            Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${recipes}")
        }
    }


    fun loadRecipes(): List<Recipe> {
        return recipes
    }

    fun updateRecipe(recipeId: String, isFavourite: Boolean) {
        // Update the favorite status in Firestore
        val recipeRef = collectionReference.document(recipeId)
        recipeRef.update("isFavourite", isFavourite)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Recipe updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error updating recipe: ${e.message}")
            }
    }
}

