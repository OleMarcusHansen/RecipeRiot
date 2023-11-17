package no.hiof.reciperiot.ViewModels


import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import no.hiof.reciperiot.data.IngredientsRepository

class IngredientsViewModel : ViewModel() {

    var newIngredient by mutableStateOf("")
    var ingredientsList by mutableStateOf(emptyList<Pair<String, MutableState<Boolean>>>())

    private val repository = IngredientsRepository() // Create a repository for data operations


    fun getIngredients() {
        viewModelScope.launch {
            repository.getIngredients()
        }
    }

    fun saveIngredients(ingredientsToSave: List<Pair<String, Boolean>>) {
        viewModelScope.launch {
            repository.saveCheckedStatesOfIngredients(ingredientsToSave)
        }
    }

    fun getIngredientsForHomeScreen() {
        viewModelScope.launch {
            repository.getIngredientsForHomeScreen()
        }
    }

    fun deleteIngredient(ingredientName: String) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredientName)
        }
    }
    fun saveIngredientstoDb1(db: FirebaseFirestore, ingredientList: List<Pair<String, Boolean>>) {
        val user = Firebase.auth.currentUser
        //TODO: ensure logged in
        val docRef = user?.let { db.collection("ingredients").document(it.uid) }
        val data = hashMapOf<String, Any>()
        for ((name, checked) in ingredientList) {
            data[name] = checked
        }
        //TODO: ensure logged in
        if (docRef != null) {
            docRef.set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(ContentValues.TAG, "DocumentSnapcshot added!")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
        }
    }
    fun getIngredients1(db: FirebaseFirestore, callback: (Map<String, Any>?) -> Unit) {
        val user = Firebase.auth.currentUser
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
}