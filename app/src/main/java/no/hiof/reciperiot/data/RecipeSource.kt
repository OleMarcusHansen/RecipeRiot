package no.hiof.reciperiot.data

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe

class RecipeSource1() {
    private val recipes: MutableList<Recipe> = mutableListOf(
        Recipe(1,"mat", R.drawable.food,
            "45min", true, "dsf", "Ls53mVW30tXLzCBJRjxXChAhYQm2"),
        Recipe(2, "pizza", R.drawable.food,
            "30min", false,
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n" +
                    "4. Add a few slices of cheese on top of the turkey ham.\n\n" +
                    "5. Thinly slice some onions and place them on the cheese.\n\n" +
                    "6. Add a few pickles for some extra flavor.\n\n" +
                    "7. Top each sandwich with another slice of bread to form a sandwich.\n\n", "Ls53mVW30tXLzCBJRjxXChAhYQm2"),
        Recipe(3, "hamburger", R.drawable.hamburger,
            "2000min", true, "bare lag den bror", "Ls53mVW30tXLzCBJRjxXChAhYQm2")
    )
    fun loadRecipes(): List<Recipe> {
        return recipes.toList()
    }

    fun updateRecipe(recipeId: Int, isFavourite: Boolean) {
        recipes.find { it.id == recipeId }?.isFavourite = isFavourite
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


                recipe?.let { recipes.add(it) }
            }
        }
    }


    fun loadRecipes(): List<Recipe> {
        return recipes
    }

    fun updateRecipe(recipeId: String, isFavourite: Boolean) {
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

