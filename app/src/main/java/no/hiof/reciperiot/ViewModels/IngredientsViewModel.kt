package no.hiof.reciperiot.ViewModels


import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class IngredientsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    var newIngredient by mutableStateOf("")
    var ingredientsList by mutableStateOf(emptyList<Pair<String, MutableState<Boolean>>>())



    fun deleteIngredient(ingredientName: String) {

        val docRef = user?.let { db.collection("ingredients").document(it.uid) }

        // Delete the specific field from the document
        docRef?.update(ingredientName, FieldValue.delete())
            ?.addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
            }
            ?.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error deleting document", e)
            }
    }
    fun saveIngredientsToDb(ingredientList: List<Pair<String, Boolean>>) {
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
    fun getIngredientsToIngredientScreen(db: FirebaseFirestore, callback: (Map<String, Any>?) -> Unit) {
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