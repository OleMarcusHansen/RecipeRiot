package no.hiof.reciperiot.data

import android.util.Log
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
    private val recipes: MutableList<Recipe> = mutableListOf()

    init {
        collectionReference
            .whereEqualTo("userid", user?.uid)
            //.whereEqualTo("favourite", true)
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
        return recipes
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

