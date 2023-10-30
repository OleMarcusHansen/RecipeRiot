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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import no.hiof.reciperiot.impl.NotificationService
import no.hiof.reciperiot.screens.AuthenticationScreen
import no.hiof.reciperiot.screens.FavouriteMeals
import no.hiof.reciperiot.screens.HomeScreen
import no.hiof.reciperiot.screens.IngredientsScreen
import no.hiof.reciperiot.screens.LoginScreen
import no.hiof.reciperiot.screens.RecipePage1
import no.hiof.reciperiot.screens.SettingsScreen
import no.hiof.reciperiot.screens.ShoppingListScreen
import okhttp3.OkHttpClient


class MainActivity : ComponentActivity() {

    val client = OkHttpClient()

    // Firestore connection, maybe change this
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = NotificationService(applicationContext)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call LoginScreen with the showNotification function
                    LoginScreen(login = { /* Handle successful login */ }, showNotification = { user ->
                        service.showNotification(user)
                    })

                    MainApp(service, client, db)
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
fun MainApp(notificationService: NotificationService, client: OkHttpClient,db: FirebaseFirestore , modifier: Modifier = Modifier) {
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
        NavHost(navController = navController, startDestination = Screen.Login.route, modifier = Modifier.padding(top = 100.dp).padding(bottom = 80.dp)) {
            composable(Screen.Login.route) {
                AuthenticationScreen(
                    onSignInClick = { email, password ->
                        navController.navigate(Screen.Home.route)

                    }
                )
                /*
                LoginScreen(
                    login = { navController.navigate(Screen.Home.route) },
                    showNotification = { user ->
                        notificationService.showNotification(user)
                    }
                )

                 */
            }
            composable(Screen.Home.route) {
                HomeScreen(navController, snackBarHostState, client)
            }
            composable(Screen.Ingredients.route) {
                IngredientsScreen(snackBarHostState)
            }
            composable(Screen.Favourites.route) {
                FavouriteMeals(navController, db)
            }
            composable(Screen.Shopping.route) {
                ShoppingListScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(logout = {navController.navigate("login")})
            }
            composable("${Screen.RecipePage.route}/{recipeid}",
                arguments = listOf(navArgument("recipeid"){ type = NavType.IntType})
            ) { backStackEntry ->
                RecipePage1(navController, backStackEntry.arguments!!.getInt("recipeid", 1), db)
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



/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        MainApp()
    }
}*/