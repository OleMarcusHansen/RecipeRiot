package no.hiof.reciperiot.ViewModels

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.hiof.reciperiot.R
import no.hiof.reciperiot.Secrets
import no.hiof.reciperiot.model.Recipe
import no.hiof.reciperiot.screens.getIngredients
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class HomeViewModel : ViewModel() {
    // Options
    val time = mutableStateOf("20")

    var ingredients by mutableStateOf(emptyList<String>())

    val buttonEnabled = mutableStateOf(true)

    fun handleFirestoreRemove(recipe: Recipe, db: FirebaseFirestore) {
        val user = com.google.firebase.ktx.Firebase.auth.currentUser
        Log.d(ContentValues.TAG, "Before get()")
        db.collection("FavouriteMeals")
            .whereEqualTo("userid", user?.uid)
            .whereEqualTo("id", recipe.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Get the document ID
                    val documentId = document.id

                    // Delete the document based on the document ID
                    db.collection("FavouriteMeals")
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

    fun handleFirestoreAdd(recipe: Recipe, db: FirebaseFirestore) {
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

        db.collection("FavouriteMeals")
            .add(recipeadd)
            .addOnSuccessListener { documentReference ->
                val updatedRecipe = recipe.copy(id = documentReference.id)
                updateRecipeId(updatedRecipe, documentReference.id, db)

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
                    recipes.value = listOf(recipe)
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error updating recipe ID", e)
                }
        } else {
            println("No data or error")
        }
    }

    // Function to fetch ingredients from Firestore
    fun getIngredientsFromFirestore(db: FirebaseFirestore) {
        getIngredients(db) { data ->
            if (data != null) {
                val firestoreIngredients = data.entries.map { it.key to mutableStateOf(it.value as Boolean) }
                ingredients = firestoreIngredients
                    .filter {it.second.value}
                    .map {it.first}
            } else {
                println("No data or error")
            }
        }
    }

    // Liste med recipes. For å kanskje generere flere samtidig
    val recipes = mutableStateOf(emptyList<Recipe>())

    suspend fun generateGPT(client: OkHttpClient, ingredients: List<String>, time: String): List<Recipe> = withContext(Dispatchers.IO) {
        val user = Firebase.auth.currentUser

        try {
            if (ingredients.isEmpty() || time == "" || time.toInt() <= 0){
                println("Ingredients list empty or time is invalid")
                val defaultRecipe = Recipe(
                    "iv",
                    "Empty bowl",
                    R.drawable.food,
                    "https://cdn.discordapp.com/attachments/1148561836724207708/1174328461351997492/image.png?ex=6567319b&is=6554bc9b&hm=554c6bc1c7793a83a9bc3f1b72b5f26edda4a14f243d6bbc25eb318170b28347&",
                    "0",
                    false,
                    "Get an empty bowl",
                    "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                    "[\"Empty bowl\"]",
                    user!!.uid
                )
                return@withContext listOf(defaultRecipe)
            }

            println("start gpt generate")
            //prompt til chatGPT
            //bør bli justert og testet for å få best mulig resultat
            val prompt = """I have only the ingredients: ${ingredients}. I have ${time} minutes to make food. Generate a recipe for me. Your output should be in JSON format: {recipe_name: String, recipe_time: String, recipe_instructions: String, recipe_nutrition: Object, recipe_ingredients: Array}"""

            println(prompt)

            //kalle chatCompletion api
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            val body = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [{"role": "system", "content": "You are a recipe generator"}, {"role": "user", "content": "${prompt}"}]
        }
    """
            val postBody: RequestBody = body.toRequestBody(mediaType)
            val url = "https://api.openai.com/v1/chat/completions"
            val request : Request = Request.Builder()
                .url(url)
                .post(postBody)
                .addHeader("Authorization", "Bearer " + Secrets.gpt_key)
                .build()

            // Make the API call
            val response: Response = try {
                client.newCall(request).await()
            } catch (exception: IOException) {
                // Log error and return default timeout recipe
                Log.e("ChatCompletionError", "Error calling API: ${exception.message}")
                val defaultRecipe = Recipe(
                    "ik",
                    "Burned toast",
                    R.drawable.food,
                    "https://cdn.discordapp.com/attachments/1148561836724207708/1172152256683065384/image.png?ex=655f46db&is=654cd1db&hm=2450543bf60afc32ad2c67d54b00328112ba7cd43656abb0d32b34f60d339d98&",
                    "60",
                    false,
                    "Timed out",
                    "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                    "[\"Bread\"]",
                    user!!.uid
                )
                return@withContext listOf(defaultRecipe)
            }

            // Handle the response and return the list of recipes
            if (response.isSuccessful) {
                val responseString = response.body?.string()
                println(responseString)
                if (responseString != null) {
                    try{
                        val responseJSON = JSONObject(responseString)
                        val messageJSON = JSONObject(responseJSON.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"))
                        println(messageJSON)

                        // Image creation
                        //val imageResponse = generateImage(client, messageJSON.getString("recipe_name"))
                        //val imageResponseURL = JSONObject(imageResponse).getJSONArray("data").getJSONObject(0).getString("url")

                        // Standard image
                        val imageResponseURL = "https://cdn.discordapp.com/attachments/1148561836724207708/1172157068497666048/image.png?ex=655f4b56&is=654cd656&hm=a296565e26720c460d137ee7941dd195e597378e26b6e77cc7d1320551067ad0&"

                        val recipes = listOf(
                            Recipe(
                                "yh",
                                messageJSON.getString("recipe_name"),
                                R.drawable.food,
                                imageResponseURL,
                                messageJSON.getString("recipe_time"),
                                false,
                                messageJSON.getString("recipe_instructions"),
                                messageJSON.getString("recipe_nutrition"),
                                messageJSON.getString("recipe_ingredients"),
                                user!!.uid
                            )
                        )
                        return@withContext recipes
                    }
                    catch (exception: JSONException){
                        Log.e("ChatCompletionError", "Error parsing chat completion to JSON: ${exception.message}")
                    }
                }
            }
            else{
                println(response.message)
                println(response)
                println(response.body)
            }

            // Log error and return default failed recipe
            Log.e("ChatCompletionError", "Error generating recipe")
            val defaultRecipe = Recipe(
                "uh",
                "Failed tomato soup",
                R.drawable.food,
                "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
                "60",
                false,
                "Something failed",
                "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                "[\"Failure\"]",
                user!!.uid
            )
            return@withContext listOf(defaultRecipe)
        }
        catch (exception: Exception){
            Log.e("ParseError", "Error parsing time to int: ${exception.message}")
            println("Ingredients list empty or time is invalid")
            val defaultRecipe = Recipe(
                "uh",
                "Failed tomato soup",
                R.drawable.food,
                "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
                "60",
                false,
                "Something failed",
                "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                "[\"Failure\"]",
                user!!.uid
            )
            return@withContext listOf(defaultRecipe)
        }
    }

    suspend fun generateImage(client: OkHttpClient, recipeName: String): String {
        println("start image generate")

        //prompt
        //bør bli justert og testet for å få best mulig resultat
        val prompt = """Dish called $recipeName"""

        println(prompt)

        //kalle chatCompletion api
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        val body = """
        {
            "prompt": "$prompt",
            "size": "256x256"
        }
    """
        val postBody: RequestBody = body.toRequestBody(mediaType)
        val url = "https://api.openai.com/v1/images/generations"
        val request : Request = Request.Builder()
            .url(url)
            .post(postBody)
            .addHeader("Authorization", "Bearer " + Secrets.gpt_key)
            .build()

        // Make the API call
        val response: Response = try {
            client.newCall(request).await()
        } catch (exception: IOException) {
            // Log error and return default failed image
            Log.e("ImageGenerationError", "Error calling API: $exception")
            return """"created": 1699533459,"data": [{"url": "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&"}]}"""
        }

        if (response.isSuccessful) {
            val responseString = response.body?.string()
            println(responseString)
            if (responseString != null) {
                return responseString
            }
        }
        else{
            println(response.message)
            println(response)
            println(response.body)
        }

        // Log error and return default failed image
        Log.e("ImageGenerationError", "Error calling API")
        return """"created": 1699533459,"data": [{"url": "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&"}]}"""
    }

    // Extension function to make Call awaitable with a coroutine
    suspend fun Call.await(): Response = withContext(Dispatchers.IO) {
        val deferred = async { execute() }
        try {
            deferred.await()
        } catch (e: CancellationException) {
            throw e
        }
    }
}
