package no.hiof.reciperiot.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe

class RecipeSource1() {
    private val recipes: MutableList<Recipe> = mutableListOf(
        Recipe("r","mat", R.drawable.food, "test",
            "45min", true, "dsf", "agI84BahTTXBHvltC1dfNndLk0n2"),
        Recipe("r", "pizza", R.drawable.food, "test",
            "30min", false,
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n" +
                    "4. Add a few slices of cheese on top of the turkey ham.\n\n" +
                    "5. Thinly slice some onions and place them on the cheese.\n\n" +
                    "6. Add a few pickles for some extra flavor.\n\n" +
                    "7. Top each sandwich with another slice of bread to form a sandwich.\n\n", "agI84BahTTXBHvltC1dfNndLk0n2"),
        Recipe("r", "hamburger", R.drawable.hamburger, "test",
            "2000min", true, "bare lag den bror", "agI84BahTTXBHvltC1dfNndLk0n2")
    )
    fun loadRecipes(): List<Recipe> {
        return recipes.toList()
    }

    fun updateRecipe(recipeId: String, favourite: Boolean) {
        recipes.find { it.id == recipeId }?.favourite = favourite
    }
}

class RecipeSource() {
    val user = Firebase.auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = firestore.collection("FavouriteMeals")
    private val recipes: MutableList<Recipe> = mutableListOf()

    init {
        collectionReference
            .whereEqualTo("userid", user?.uid)
            .whereEqualTo("favourite", true)
            .addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("FirestoreError", "Error fetching data: ${exception.message}")
                return@addSnapshotListener
            }

            recipes.clear()
            snapshot?.documents?.forEach { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${recipes.size}")
                Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${recipes}")
                Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${documentSnapshot.reference.id}")



                recipe?.let { recipes.add(it) }
            }
        }
    }


    fun loadRecipes(): List<Recipe> {
        return recipes
    }

    fun updateRecipe(recipeId: String, favourite: Boolean) {
        val recipeRef = collectionReference.document(recipeId)
        recipeRef.update("favourite", favourite)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Recipe updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error updating recipe: ${e.message}")
            }
    }


}

