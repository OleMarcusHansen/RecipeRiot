package no.hiof.reciperiot.data

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
    private val recipes: MutableList<Recipe> = mutableStateListOf()


    fun getRecipes(test: Boolean){
        val query = if (test) {
            collectionReference
                .whereEqualTo("userid", user?.uid)
                .whereEqualTo("favourite", true)
        } else {
            collectionReference
                .whereEqualTo("userid", user?.uid)
        }


        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("FirestoreError", "Feil ved henting av data: ${exception.message}")
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


    fun loadFavourites(): List<Recipe> {
        getRecipes(true)
        return recipes
    }
    fun loadRecipes(): List<Recipe> {
        getRecipes(false)
        return recipes
    }
    fun loadGeneratedRecipe(): List<Recipe> {
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


