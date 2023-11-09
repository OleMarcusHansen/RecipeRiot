package no.hiof.reciperiot.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.hiof.reciperiot.R
import no.hiof.reciperiot.Secrets
import no.hiof.reciperiot.model.Recipe
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

@Composable
fun HomeScreen(navController: NavController, snackbarHost : SnackbarHostState, client: OkHttpClient, modifier: Modifier = Modifier, db: FirebaseFirestore) {
    // Options
    val time = remember { mutableStateOf("20") }

    // Ingredienser fra databasen, som i ingredientsscreen
    val ingredients = listOf("Banana", "Eggs", "Bacon", "Ham", "Turkey")

    // Liste med recipes. For å kanskje generere flere samtidig
    val recipes = remember { mutableStateOf(emptyList<Recipe>()) }

    // Til snackbar
    val scope = rememberCoroutineScope()

    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.home_options), fontSize = 20.sp)
        TimeInput(text = stringResource(R.string.home_options_time), state = time)
        Text(stringResource(R.string.home_ingredients), fontSize = 20.sp)
        LazyVerticalGrid(columns = GridCells.Adaptive(90.dp),
            content = {
                items(ingredients.size) {index ->
                    Text(ingredients[index])
                }
            }
        )
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                /*ChatGPT*/

                scope.launch {
                    val newRecipes = generateGPT(client, ingredients, time.value)
                    recipes.value = newRecipes
                    snackbarHost.showSnackbar("Oppskrift generert")
                }
            }) {
                Text(stringResource(R.string.home_generate))
            }
        }
        RecipeList(recipes = recipes.value, navController, onFavouriteToggle = {},
            onAddToFavorites = { recipe ->
                handleFirestoreAdd(recipe, db)
            },
            onRemoveFromFavorites = { recipe ->
                handleFirestoreRemove(recipe, db)
            })
    }
}

@Composable
fun TimeInput(text : String, state : MutableState<String>){
    Row(){
        Text(text)
        BasicTextField(value = state.value,
            onValueChange = {newText -> state.value = newText},
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, Color.Gray),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun IngredientList(ingredients : List<String>){

}
/*
@Composable
fun Ingredient(text : String, state : MutableState<Boolean>){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        Text(text)
    }
}*/


//bør ta options og ingredienser som parametere
/*fun generateGPT(client: OkHttpClient) : List<Recipe>{

    println("test")

    //prompt til chatGPT
    //bør bli justert og testet for å få best mulig resultat
    val prompt = """I have the ingredients: cheese, bacon, eggs, onions.
        I have 20 minutes to make food. Generate a recipe for me.
        The output should be in JSON format with the keys recipe_name, 
        recipe_time, recipe_instructions and recipe_nutrition.
        Answer with only the JSON string."""

    //kalle chatCompletion api

    val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
    val body = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [{"role": "system", "content": "You are a recipe generator"}, {"role": "user", "content": "Create a recipe in JSON format with the keys recipe_name, recipe_time, recipe_instructions and recipe_nutrition."}]
        }
    """
    val postBody: RequestBody = body.toRequestBody(mediaType)

    val url = "https://api.openai.com/v1/chat/completions"

    val request : Request = Request.Builder()
        .url(url)
        .post(postBody)
        .addHeader("Authorization", "Bearer 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")
        .build()

    //val call : Call = client.newCall(request)
    //val resp : Response = call.execute()

    var responseString = """{
            "recipe_name": "Early response baguettes",
            "recipe_time": "1",
            "recipe_instructions": [
                "1. Preheat a panini press or a stovetop grill pan over medium-high heat.",
                "2. Take 2 slices of bread and lay them out on a clean surface."
            ],
            "recipe_nutrition": {
                "calories": "Approximately 400-500 calories per serving",
                "carbohydrates": "Approximately 35-45g per serving"
             }
        }
    """

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            println(response.toString())
            //println(response.body?.string())
            //val responseJSON = JSONObject(response.body?.string())
            val responseBody = response.body?.string()
            print(responseBody)
            if (responseBody != null) {
                val responseJSON = JSONObject(responseBody)
                responseString = responseJSON.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                // Now you can work with responseJSON
            } else {
                // Handle the case where the response body is null
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("failed. message: " + e.message)
            responseString = """{
                    "recipe_name": "Failed tomato soup",
                    "recipe_time": "60",
                    "recipe_instructions": [
                        "1. Preheat a panini press or a stovetop grill pan over medium-high heat.",
                        "2. Take 2 slices of bread and lay them out on a clean surface."
                    ],
                    "recipe_nutrition": {
                        "calories": "Approximately 400-500 calories per serving",
                        "carbohydrates": "Approximately 35-45g per serving"
                     }
                }
            """
        }
    })
    val generatedjson = JSONObject(responseString)

    val recipes = listOf(Recipe(2, generatedjson.getString("recipe_name"), R.drawable.food, generatedjson.getString("recipe_time"),false, "ddddd"))
    return recipes
}*/

suspend fun generateGPT(client: OkHttpClient, ingredients: List<String>, time: String): List<Recipe> = withContext(Dispatchers.IO) {
    // ... (Your existing code)
    println("start gpt generate")

    val user = Firebase.auth.currentUser
    //prompt til chatGPT
    //bør bli justert og testet for å få best mulig resultat
    val prompt = """I have only the ingredients: ${ingredients}. I have ${time} minutes to make food. Generate a recipe for me. Your output should be in JSON format with the keys (recipe_name, recipe_time, recipe_instructions): String, recipe_nutrition: object"""

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
    } catch (e: IOException) {
        // Handle the exception here
        println(e)
        val defaultRecipe = Recipe(
            "ik",
            "Burned toast",
            R.drawable.food,
            "https://cdn.discordapp.com/attachments/1148561836724207708/1172152256683065384/image.png?ex=655f46db&is=654cd1db&hm=2450543bf60afc32ad2c67d54b00328112ba7cd43656abb0d32b34f60d339d98&",
            "60",
            false,
            "Timed out",
            "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}"
        )
        return@withContext listOf(defaultRecipe)
    }

    // Handle the response and return the list of recipes
    if (response.isSuccessful) {
        val responseString = response.body?.string()
        println(responseString)
        if (responseString != null) {
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
                    user!!.uid
                )
            )
            return@withContext recipes
        }
    }
    else{
        println(response.message)
        println(response)
        println(response.body)
    }

    println("Something failed")
    // Handle errors or return a default value in case of failure
    val defaultRecipe = Recipe(
        "uh",
        "Failed tomato soup",
        R.drawable.food,
        "https://cdn.discordapp.com/attachments/1148561836724207708/1172151716741906503/image.png?ex=655f465a&is=654cd15a&hm=2ee66b50819a6faa6c8b4e3afa638b5540f1cd59f386703b10b40609ac7645a4&",
        "60",
        false,
        "Something failed",
        "{\"calories\":0,\"protein\":0,\"carbohydrates\":0,\"fat\":0}"
    )
    return@withContext listOf(defaultRecipe)
}

suspend fun generateImage(client: OkHttpClient, recipe: String): String {
    println("start image generate")

    //prompt
    //bør bli justert og testet for å få best mulig resultat
    val prompt = """$recipe"""

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
    } catch (e: IOException) {
        // Handle the exception here
        println(e)
        return "failed"
    }

    // Handle the response and return the list of recipes
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

    println("Something failed")
    // Handle errors or return a default value in case of failure
    return "failed"
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