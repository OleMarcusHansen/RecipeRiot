package no.hiof.reciperiot.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class IngredientsRepository {

    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    val ingredientsList = mutableStateOf(emptyList<Pair<String, MutableState<Boolean>>>())


    suspend fun getIngredients() {
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }
        val data = docRef?.get()?.await()?.data

        if (data != null) {
            val firestoreIngredients =
                data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
            ingredientsList.value = firestoreIngredients
        } else {
            // Handle case where there's no data
        }
    }

    suspend fun getIngredientsForHomeScreen() {
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }
        val data = docRef?.get()?.await()?.data

        if (data != null) {
            val firestoreIngredients =
                data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
            firestoreIngredients
                .filter { it.second.value }
                .map { it.first }
        } else {
            println("No data or error")
        }
    }

    suspend fun saveCheckedStatesOfIngredients(ingredientList: List<Pair<String, Boolean>>) {
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }
        val data = hashMapOf<String, Any>()
        for ((name, checked) in ingredientList) {
            data[name] = checked
        }

        docRef?.set(data)?.await()
    }

    suspend fun deleteIngredient(ingredientName: String) {
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }

        // Delete the specific field from the document
        docRef?.update(ingredientName, FieldValue.delete())?.await()
    }
}