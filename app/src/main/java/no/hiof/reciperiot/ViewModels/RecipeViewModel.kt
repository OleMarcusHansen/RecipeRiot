package no.hiof.reciperiot.ViewModels

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.data.RecipeRepository
import no.hiof.reciperiot.model.Recipe

class RecipeViewModel : ViewModel() {
    val recipes by mutableStateOf(RecipeRepository().loadFavourites())
    val history by mutableStateOf(RecipeRepository().loadRecipes())
    var searchText by  mutableStateOf("")

    private val recipeRepository = RecipeRepository()

    fun updateRecipeFavouriteStatus(recipe: Recipe, fav: Boolean) {
        recipeRepository.updateRecipeFavouriteStatus(recipe, fav)
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
