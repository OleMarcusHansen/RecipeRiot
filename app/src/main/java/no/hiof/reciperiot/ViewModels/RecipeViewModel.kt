package no.hiof.reciperiot.ViewModels

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import no.hiof.reciperiot.data.RecipeRepository
import no.hiof.reciperiot.model.Recipe

class RecipeViewModel : ViewModel() {


    val recipes by mutableStateOf(RecipeRepository().loadFavourites())
    val history by mutableStateOf(RecipeRepository().loadRecipes())


    fun updateRecipeFavouriteStatus(recipe: Recipe, db: FirebaseFirestore, fav: Boolean) {
        val docid = recipe.id
        val updatedRecipe = mapOf("favourite" to fav)
        val user = com.google.firebase.ktx.Firebase.auth.currentUser

        if (user != null) {
            db.collection("FavouriteMeals")
                .document(docid)
                .set(updatedRecipe, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Recipe favourite updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error updating recipe favourite", e)
                }
        }
    }

    fun firestoreCleanup(db: FirebaseFirestore) {
        val user = com.google.firebase.ktx.Firebase.auth.currentUser
        val query = db.collection("FavouriteMeals")
            .whereEqualTo("userid", user?.uid)
            .whereEqualTo("favourite", false)
        query.get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                for (document in documents) {
                    Log.d(ContentValues.TAG, "$document")
                    val documentRef = db.collection("FavouriteMeals").document(document.id)
                    Log.d(ContentValues.TAG, "Deleting document with ID: ${document.id}, Data: ${document.data}")

                    batch.delete(documentRef)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Documents successfully deleted")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error deleting documents", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
            }
    }
}
