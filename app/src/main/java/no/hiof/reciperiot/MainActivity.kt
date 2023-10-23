package no.hiof.reciperiot

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import no.hiof.reciperiot.ui.theme.FavouriteMeals
import no.hiof.reciperiot.ui.theme.HomeScreen
import no.hiof.reciperiot.ui.theme.IngredientsScreen
import no.hiof.reciperiot.ui.theme.LoginScreen
import no.hiof.reciperiot.ui.theme.RecipePage
import no.hiof.reciperiot.ui.theme.SettingsScreen
import no.hiof.reciperiot.ui.theme.ShoppingListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
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
    object Login : Screen("login", R.string.nav_login, Icons.Default.Home)
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Ingredients : Screen("ingredients", R.string.nav_ingredients, Icons.Default.Add)
    object Favourites : Screen("favourites", R.string.nav_favourites, Icons.Default.Favorite)
    object Shopping : Screen("shopping", R.string.nav_shopping, Icons.Default.ShoppingCart)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
    object RecipePage : Screen("recipepage", R.string.nav_recipepage, Icons.Default.ArrowForward)

}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val bottomNavigationScreens = listOf(
        Screen.Home,
        Screen.Ingredients,
        Screen.Favourites,
        Screen.Shopping,
        Screen.Settings

    )

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                bottomNavigationScreens = bottomNavigationScreens
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState)
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "login", modifier = Modifier.padding(top = 100.dp)) {
            composable(Screen.Login.route) { LoginScreen(login = {navController.navigate("home")}) }
            composable(Screen.Home.route) {
                HomeScreen(navController, snackBarHostState)
            }
            composable(Screen.Ingredients.route) {
                IngredientsScreen()
            }
            composable(Screen.Favourites.route) {
                FavouriteMeals(navController)
            }
            composable(Screen.Shopping.route) {
                ShoppingListScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(logout = {navController.navigate("login")})
            }
            composable(Screen.RecipePage.route) {
                RecipePage()
            }
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier.padding(16.dp),
        title = { Text(text = "") },
        navigationIcon = {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
            Image(
                painter = painterResource(id = R.drawable.reciperiot),
                contentDescription = null
            )}
        }
    )
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        MainApp()
    }
}