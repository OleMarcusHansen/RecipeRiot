package no.hiof.reciperiot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.hiof.reciperiot.ui.theme.HomeScreen
import no.hiof.reciperiot.ui.theme.IngredientsScreen
import no.hiof.reciperiot.ui.theme.LoginScreen
import no.hiof.reciperiot.ui.theme.RecipeRiotTheme
import no.hiof.reciperiot.ui.theme.SettingsScreen
import no.hiof.reciperiot.ui.theme.ShoppingListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeRiotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    Column(verticalArrangement = Arrangement.SpaceBetween) {
        NavHost(navController = navController, startDestination = "login") {
            composable(route = "login") { LoginScreen() }
            composable(route = "main") { HomeScreen() }
            composable(route = "ingredients") { IngredientsScreen() }
            composable(route = "shoppinglist") { ShoppingListScreen() }
            composable(route = "settings") { SettingsScreen(logout = {navController.navigate("login")}) }
        }
        BottomAppBar {
            Button(onClick = { navController.navigate("main") }) {
                Text("Home")
            }
            Button(onClick = { navController.navigate("ingredients") }) {
                Text("Ingredients")
            }
            Button(onClick = { navController.navigate("shoppinglist") }) {
                Text("Shopping")
            }
            Button(onClick = { navController.navigate("settings") }) {
                Text("Settings")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipeRiotTheme {
        MainApp()
    }
}