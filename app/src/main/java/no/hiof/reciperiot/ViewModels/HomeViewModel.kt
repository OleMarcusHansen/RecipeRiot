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
import no.hiof.reciperiot.data.RecipeRepository
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

    private val repository = RecipeRepository()

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
    //val recipes = mutableStateOf(repository.loadGeneratedRecipe())
    val recipes = repository.generatedRecipes

    fun addToDatabase(recipe: Recipe){
        repository.handleFirestoreAdd(recipe)
        //recipes.value = repository.loadGeneratedRecipe()
    }

    suspend fun generateGPT(client: OkHttpClient, ingredients: List<String>, time: String): List<Recipe> = withContext(Dispatchers.IO) {
        val user = Firebase.auth.currentUser

        try {
            if (ingredients.isEmpty() || time == "" || time.toInt() <= 0){
                Log.w("InvalidInput", "Invalid input for ingredients and/or time")
                val defaultRecipe = Recipe(
                    "iv",
                    "Empty bowl - please try again",
                    R.drawable.food,
                    "https://cdn.discordapp.com/attachments/1148561836724207708/1174328461351997492/image.png?ex=6567319b&is=6554bc9b&hm=554c6bc1c7793a83a9bc3f1b72b5f26edda4a14f243d6bbc25eb318170b28347&",
                    "0",
                    false,
                    "Make sure to add ingredients and provide time as a positive integer",
                    "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                    "[\"Empty bowl\"]",
                    user!!.uid
                )
                return@withContext listOf(defaultRecipe)
            }

            //prompt til chatGPT
            //bør bli justert og testet for å få best mulig resultat
            val prompt = """I have only the ingredients: ${ingredients}. I have ${time} minutes to make food. Generate a recipe for me. Your output should be in JSON format: {recipe_name: String, recipe_time: String, recipe_instructions: String, recipe_nutrition: Object, recipe_ingredients: Array}"""

            Log.i("RecipeGeneration", "Start generating recipe with prompt: $prompt")

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
                    "Burned toast - please try again",
                    R.drawable.food,
                    "https://cdn.discordapp.com/attachments/1148561836724207708/1172152256683065384/image.png?ex=655f46db&is=654cd1db&hm=2450543bf60afc32ad2c67d54b00328112ba7cd43656abb0d32b34f60d339d98&",
                    "60",
                    false,
                    "Recipe generation timed out, please try again",
                    "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                    "[\"Bread\"]",
                    user!!.uid
                )
                return@withContext listOf(defaultRecipe)
            }

            // Handle the response and return the list of recipes
            if (response.isSuccessful) {
                val responseString = response.body?.string()
                //println(responseString)
                if (responseString != null) {
                    try{
                        val responseJSON = JSONObject(responseString)
                        val messageJSON = JSONObject(responseJSON.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"))
                        println(messageJSON)
                        Log.i("RecipeGeneration", "Finished ChatCompletion with output message: $messageJSON")

                        // Image creation
                        val imageResponse = generateImage(client, messageJSON.getString("recipe_name"))
                        val imageResponseURL = JSONObject(imageResponse).getJSONArray("data").getJSONObject(0).getString("url")

                        // Standard image
                        //val imageResponseURL = "https://cdn.discordapp.com/attachments/1148561836724207708/1172157068497666048/image.png?ex=655f4b56&is=654cd656&hm=a296565e26720c460d137ee7941dd195e597378e26b6e77cc7d1320551067ad0&"

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
                        val defaultRecipe = Recipe(
                            "uh",
                            "Failed tomato soup - please try again",
                            R.drawable.food,
                            "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
                            "60",
                            false,
                            "Chat generation output failed, please try again",
                            "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                            "[\"Failure\"]",
                            user!!.uid
                        )
                        return@withContext listOf(defaultRecipe)
                    }
                }
            }
            else{
                Log.e("ChatCompletion", "ChatCompletion unsuccessful: ${response.message}")
                println(response.message)
                println(response)
                println(response.body)
            }

            // Log error and return default failed recipe
            Log.e("ChatCompletionError", "Error generating recipe")
            val defaultRecipe = Recipe(
                "uh",
                "Failed tomato soup - please try again",
                R.drawable.food,
                "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
                "60",
                false,
                "Something failed, please try again",
                "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                "[\"Failure\"]",
                user!!.uid
            )
            return@withContext listOf(defaultRecipe)
        }
        catch (exception: Exception){
            Log.e("ParseError", "Error parsing time to int: ${exception.message}")
            val defaultRecipe = Recipe(
                "ih",
                "Failed tomato soup - please try again",
                R.drawable.food,
                "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
                "60",
                false,
                "Make sure to provide time as a positive integer",
                "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}",
                "[\"Failure\"]",
                user!!.uid
            )
            return@withContext listOf(defaultRecipe)
        }
    }

    suspend fun generateImage(client: OkHttpClient, recipeName: String): String {
        //prompt
        //bør bli justert og testet for å få best mulig resultat
        val prompt = """Dish called $recipeName"""

        Log.i("ImageGeneration", "Start generating image with prompt: $prompt")

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
            Log.i("ImageGeneration", "Finished image generation with output: $responseString")
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
