package no.hiof.reciperiot.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.R
import no.hiof.reciperiot.model.Recipe


class RecipeRepository() {
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
}


