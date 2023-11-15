package no.hiof.reciperiot.screens

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(modifier: Modifier = Modifier, db: FirebaseFirestore) {
    // Create a remember variable to hold the text entered in the input field
    val textState = remember { mutableStateOf("") }
    getShoppingList(db) { data ->
        if (textState.value.isEmpty()) {
            textState.value = data.toString()
        }
    }


    Card(modifier = modifier.fillMaxSize().padding(10.dp, 0.dp, 10.dp, 95.dp),
        elevation = CardDefaults.cardElevation(8.dp)){
        TextField(value = textState.value,
            onValueChange = { newText ->
                // Update the textState with the new text when it changes
                textState.value = newText
            },
            textStyle = TextStyle(fontSize = 16.sp),
            label = {Text("Shopping List")},
            modifier = modifier.fillMaxSize()
        )
    }
    LaunchedEffect(textState.value) {
        // This block will run when textState.value changes
        saveShoppinglistToDb(db, textState.value)
    }
    // Button to save the shopping list
    /*
    Button(
        onClick = {
            // Call the saveShoppinglistToDb function with the text content
            saveShoppinglistToDb(db, textState.value)
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Save Shopping List")
    }

     */
}
fun getShoppingList(db: FirebaseFirestore, callback: (shoppingListContent: String) -> Unit) {
    val user = Firebase.auth.currentUser
    // TODO: Ensure logged in
    val docRef = user?.let { db.collection("shoppinglist").document(it.uid) }
    docRef?.get()?.addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            val shoppingListContent = document.getString("shoppingListContent") ?: ""
            callback(shoppingListContent)
        } else {
            val emptyData = emptyMap<String, Any>()
            docRef?.set(emptyData)
                ?.addOnSuccessListener {
                    callback("")
                }
                ?.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error: Could not create doc", exception)
                    callback("")
                }
        }
    }?.addOnFailureListener { exception ->
        Log.d(ContentValues.TAG, "get failed with ", exception)
        callback("")
    }
}
fun saveShoppinglistToDb(db: FirebaseFirestore, shoppingListContent: String) {
    if (shoppingListContent == "") {
        Log.d(TAG, "error tom shoppinglist")
    } else {
        val user = Firebase.auth.currentUser
        // TODO: Ensure user is logged in

        if (user != null) {
            val docRef = db.collection("shoppinglist").document(user.uid)
            val data = hashMapOf(
                "shoppingListContent" to shoppingListContent
            )

            docRef.set(data)
                .addOnSuccessListener {
                    // Successfully saved the shopping list to the database
                    Log.d("ShoppingListScreen", "Shopping list saved to the database.")
                }
                .addOnFailureListener { e ->
                    Log.e("ShoppingListScreen", "Error saving shopping list to the database: $e")
                }
        } else {
            // Handle the case when the user is not logged in
            Log.e("ShoppingListScreen", "User is not logged in.")
        }
    }
}








@Preview
@Composable
fun ShoppingListScreenPreview() {
    /*
    AppTheme {
        ShoppingListScreen()
    }

     */
}
