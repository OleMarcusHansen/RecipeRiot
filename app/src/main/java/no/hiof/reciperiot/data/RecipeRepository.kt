package no.hiof.reciperiot.data

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe

class RecipeRepository {
    val user = Firebase.auth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = firestore.collection("FavouriteMeals")
    private val recipes: MutableList<Recipe> = mutableListOf()


    fun getRecipes(test: Boolean){
        collectionReference
            .whereEqualTo("userid", user?.uid)
            .whereEqualTo("favourite", test)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("FirestoreError", "Error fetching data: ${exception.message}")
                    return@addSnapshotListener
                }

                recipes.clear()
                snapshot?.documents?.forEach { documentSnapshot ->
                    val recipe = documentSnapshot.toObject(Recipe::class.java)

                    Log.d("FirestoreData", "Data fetched successfully. Number of recipes: ${recipes.size}")
                    Log.d("FirestoreData", "recipe data: ${recipes}")
                    Log.d("FirestoreData", "recipe id: ${documentSnapshot.reference.id}")

                    recipe?.let { recipes.add(it) }
                }
            }
    }

    fun loadRecipes(): List<Recipe> {
        getRecipes(true)
        return recipes
    }
    fun loadHistory(): List<Recipe> {
        getRecipes(false)
        return recipes
    }

    fun handleFirestoreAdd(recipe: Recipe) {
        val user = com.google.firebase.ktx.Firebase.auth.currentUser

        val recipeadd = mapOf(
            "id" to "",
            "title" to recipe.title,
            "imageResourceId" to recipe.imageResourceId,
            "imageURL" to recipe.imageURL,
            "cookingTime" to recipe.cookingTime,
            "favourite" to recipe.favourite,
            "recipe_instructions" to recipe.recipe_instructions,
            "recipe_nutrition" to recipe.recipe_nutrition,
            "recipe_ingredients" to recipe.recipe_ingredients,
            "userid" to recipe.userid
        )

        firestore.collection("FavouriteMeals")
            .add(recipeadd)
            .addOnSuccessListener { documentReference ->
                val updatedRecipe = recipe.copy(id = documentReference.id)
                updateRecipeId(updatedRecipe, documentReference.id, firestore)

                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    private fun updateRecipeId(recipe: Recipe, documentId: String, db: FirebaseFirestore) {

        val updatedRecipe = mapOf("id" to documentId)
        val user = com.google.firebase.ktx.Firebase.auth.currentUser

        if (user != null) {
            db.collection("FavouriteMeals")
                .document(documentId)
                .set(updatedRecipe, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Recipe ID updated successfully")
                    recipes.clear()
                    recipes.add(recipe)
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error updating recipe ID", e)
                }
        } else {
            println("No data or error")
        }
    }

    fun handleFirestoreRemove(recipe: Recipe) {
        val user = com.google.firebase.ktx.Firebase.auth.currentUser
        Log.d(ContentValues.TAG, "Before get()")
        firestore.collection("FavouriteMeals")
            .whereEqualTo("userid", user?.uid)
            .whereEqualTo("id", recipe.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Get the document ID
                    val documentId = document.id

                    // Delete the document based on the document ID
                    firestore.collection("FavouriteMeals")
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted with ID: $documentId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error deleting document with ID: $documentId", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
            }
        Log.d(ContentValues.TAG, "After get()")
    }
}

class RecipeSource1() {
    //dette er en backup av recipesource for feilsøking og andre ting
    //blir ikke brukt i vanlig drift
    private val recipes: MutableList<Recipe> = mutableListOf(
        Recipe("test1","mat", R.drawable.food, "test",
            "45min", true, "dsf", "agI84BahTTXBHvltC1dfNndLk0n2"),
        Recipe("test2", "pizza", R.drawable.food, "test",
            "30min", false,
            "1. Preheat a panini press or a stovetop grill pan over medium-high heat.\n\n" +
                    "2. Take 2 slices of bread and lay them out on a clean surface.\n\n" +
                    "3. Place a slice of turkey ham on each of the bread slices.\n\n",
            "agI84BahTTXBHvltC1dfNndLk0n2"),
        Recipe("test3", "hamburger", R.drawable.hamburger, "test",
            "2000min", true, "bare lag den bror", "agI84BahTTXBHvltC1dfNndLk0n2")
    )
    fun loadRecipes(): List<Recipe> {
        return recipes.toList()
    }
}

