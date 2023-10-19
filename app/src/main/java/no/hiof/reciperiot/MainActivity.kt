package no.hiof.reciperiot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.ui.theme.FavouriteMeals
import no.hiof.reciperiot.ui.theme.HomeScreen
import no.hiof.reciperiot.ui.theme.IngredientsScreen
import no.hiof.reciperiot.ui.theme.LoginScreen
import no.hiof.reciperiot.ui.theme.RecipeRiotTheme
import no.hiof.reciperiot.ui.theme.SettingsScreen
import no.hiof.reciperiot.ui.theme.ShoppingListScreen


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
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
        // Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth?.getCurrentUser()
        updateUI(currentUser);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = googleCredential.googleIdToken
        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            updateUI(null)
                        }
                    }
            }
            else -> {
                // Shouldn't happen.
                Log.d(TAG, "No ID token!")
            }
        }
        /*
        super.onActivityResult()
        val task = BeginSignInRequest.getSignInResultFromIntent(data)
        try {
            val idToken = task.getResult(ApiException::class.java)!!.googleIdToken
            if (idToken != null) {
                // Send ID token to backend
            } else {
                // Show error to user
            }
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
        */
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        TODO("Not yet implemented")
    }

    sealed class Screen(val route: String, val title: Int, val icon: ImageVector) {
        object Login : Screen("login", R.string.login, Icons.Default.Home)
        object Home : Screen("home", R.string.home, Icons.Default.Home)
        object Ingridients : Screen("ingridients", R.string.ingridients, Icons.Default.Add)
        object Favourites : Screen("favourites", R.string.favourites, Icons.Default.Favorite)
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
            Screen.Favourites,
            Screen.Shopping,
            Screen.Settings

        )

        Scaffold(
            topBar = { AppTopBar() },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    bottomNavigationScreens = bottomNavigationScreens
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(top = 100.dp)
            ) {
                composable(Screen.Login.route) { LoginScreen(login = { navController.navigate("home") }) }
                composable(Screen.Home.route) {
                    HomeScreen()
                }
                composable(Screen.Ingridients.route) {
                    IngredientsScreen()
                }
                composable(Screen.Favourites.route) {
                    FavouriteMeals()
                }
                composable(Screen.Shopping.route) {
                    ShoppingListScreen()
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(logout = { navController.navigate("login") })
                }
            }
        }
        val webClientId = R.string.your_web_client_id

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(stringResource(webClientId))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

    }

    @Composable
    fun BottomNavBar(navController: NavController, bottomNavigationScreens: List<Screen>) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        BottomAppBar {
            bottomNavigationScreens.forEach { screen ->
                NavigationBarItem(selected = currentDestination == screen.route, onClick = {
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
            title = { Text(text = "") },
            navigationIcon = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reciperiot),
                        contentDescription = null
                    )
                }
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        RecipeRiotTheme {
            MainApp()
        }
    }
}