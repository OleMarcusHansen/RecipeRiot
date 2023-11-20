package no.hiof.reciperiot.ViewModels


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.hiof.reciperiot.data.IngredientsRepository

class IngredientsViewModel : ViewModel() {
    var newIngredient by mutableStateOf("")
    var ingredientsList by mutableStateOf(emptyList<Pair<String, MutableState<Boolean>>>())

    private val ingredientRepository = IngredientsRepository()

    fun deleteIngredient(ingredientName: String) {
        ingredientRepository.deleteIngredient(ingredientName)
        updateIngredientsList()
    }

    fun saveIngredientsToDb() {
        //TODO: ensure logged in
        val ingredientsToSave =
            ingredientsList.map { (name, checkedState) ->
                name to checkedState.value
            }
        ingredientRepository.saveIngredientsToDb(ingredientsToSave)
    }

    fun updateIngredientsList() {
        ingredientRepository.fetchIngredients() { data ->
            if (data != null) {
                val firestoreIngredients =
                    data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
                ingredientsList = firestoreIngredients
            } else {
                println("No data or error")
            }
        }
    }

    /*
    fun getIngredientsToIngredientScreen(callback: (Map<String, Any>?) -> Unit) {
        // TODO: Ensure logged in
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                callback(document.data)
            } else {
                val emptyData = emptyMap<String, Any>()
                docRef.set(emptyData)
                    ?.addOnSuccessListener {
                        callback(emptyData)
                    }
                    ?.addOnFailureListener { exception ->
                        Log.d(ContentValues.TAG, "Error: Could not create doc", exception)
                        callback(null)
                    }
            }
        }?.addOnFailureListener { exception ->
            Log.d(ContentValues.TAG, "get failed with ", exception)
            callback(null)
        }

    }
    fun fetchDataFromFireStore() {
        getIngredientsToIngredientScreen() { data ->
            if (data != null) {
                val firestoreIngredients =
                    data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
                ingredientsList = firestoreIngredients
            } else {
                println("No data or error")
            }
        }
    }
    */
}