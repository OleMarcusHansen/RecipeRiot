package no.hiof.reciperiot.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.hiof.reciperiot.R
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
fun HomeScreen(navController: NavController, snackbarHost : SnackbarHostState, client: OkHttpClient, modifier: Modifier = Modifier) {
    val time = remember { mutableStateOf("") }

    //Last inn ingredienser fra databasen, gjør som i ingredientsscreen
    val paprika = remember { mutableStateOf(false) }

    val recipes = remember { mutableStateOf(emptyList<Recipe>()) }

    //Til snackbar
    val scope = rememberCoroutineScope()

    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.home_options), fontSize = 20.sp)
        TimeInput(text = stringResource(R.string.home_options_time), state = time)
        Text(stringResource(R.string.home_ingredients), fontSize = 20.sp)
        //bruk mutablestate til ingredienser til å lage liste av ingredienser
        Ingredient(text = "Paprika", state = paprika)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                /*ChatGPT*/

                scope.launch {
                    val newRecipes = generateGPT(client)
                    recipes.value = newRecipes
                    snackbarHost.showSnackbar("Oppskrift generert")
                }
            }) {
                Text("Generer oppskrift")
            }
        }
        RecipeList(recipes = recipes.value, navController, onFavouriteToggle = {})
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
fun Ingredient(text : String, state : MutableState<Boolean>){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        Text(text)
        Checkbox(
            checked = state.value,
            onCheckedChange = { newValue ->
                state.value = newValue
            },
            modifier = Modifier.size(24.dp)
        )
    }
}


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

suspend fun generateGPT(client: OkHttpClient): List<Recipe> = withContext(Dispatchers.IO) {
    // ... (Your existing code)
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

    // Make the API call
    val response: Response = try {
        client.newCall(request).await()
    } catch (e: IOException) {
        // Handle the exception here
        val defaultRecipe = Recipe(
            2,
            "Failed tomato soup",
            R.drawable.food,
            "60",
            false,
            "ddddd"
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
            val recipes = listOf(
                Recipe(
                    2,
                    messageJSON.getString("recipe_name"),
                    R.drawable.food,
                    messageJSON.getString("recipe_time"),
                    false,
                    "ddddd"
                )
            )
            return@withContext recipes
        }
    }

    // Handle errors or return a default value in case of failure
    val defaultRecipe = Recipe(
        2,
        "Failed tomato soup",
        R.drawable.food,
        "60",
        false,
        "ddddd"
    )
    return@withContext listOf(defaultRecipe)
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