package no.hiof.reciperiot

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
sealed class Screen(val route: String, val title: Int, val icon: ImageVector){
    object Login : Screen("login", R.string.login, Icons.Default.Home)
    object Home : Screen("home", R.string.home, Icons.Default.Home)
    object Ingridients : Screen("ingridients", R.string.ingridients, Icons.Default.Add)
    object Shopping : Screen("shopping", R.string.shopping, Icons.Default.ShoppingCart)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(modifier: Modifier = Modifier) {


    val navController = rememberNavController()

    val bottomNavigationScreens = listOf(
        Screen.Home,
        Screen.Ingridients,
        Screen.Shopping,
        Screen.Settings
    )

    Scaffold(bottomBar = {
        BottomNavBar(
            navController = navController,
            bottomNavigationScreens = bottomNavigationScreens
        )
    }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "login") {
            composable(Screen.Login.route) { LoginScreen() }
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Ingridients.route) {
                IngredientsScreen()
            }
            composable(Screen.Shopping.route) {
                ShoppingListScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(logout = {navController.navigate("login")})
            }
        }
    }
    /*Scaffold() {innerPadding ->
        NavHost(NavController = navController, startDestination = Screen.Home.route) {
            composable(route = Screen.Home.route) {
                Text(text = "HomeScreen()" ) }
            composable(route = Screen.Ingridients.route) { IngredientsScreen() }
            composable(route = Screen.Shopping.route) { ShoppingListScreen() }
            composable(route = Screen.Settings.route) { SettingsScreen(logout = {navController.navigate("login")}) }
        }
    }*/
/*
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
        }*/
    //}
}
@Composable
fun BottomNavBar(navController: NavController, bottomNavigationScreens: List<Screen>)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    BottomAppBar {
        bottomNavigationScreens.forEach { screen ->
            NavigationBarItem(selected = currentDestination == screen.route
                , onClick = {
                    navController.navigate(screen.route)
                }, icon = {
                    Icon(imageVector = screen.icon, contentDescription = "Icon")
                }, label = {
                    Text(stringResource(id = screen.title))
                }
            )
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