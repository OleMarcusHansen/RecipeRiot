package no.hiof.reciperiot.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import no.hiof.reciperiot.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    onSignInClick: (String, String) -> Unit,
    //TODO: Fix notification
    showNotification: (String) -> Unit,
    //showNotification: (String) -> Unit,
    //TODO: Implement sign up
    //onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            //text = stringResource(R.string.username),
            //state = email
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        val auth = FirebaseAuth.getInstance()
        Button(onClick = {
            auth.signInWithEmailAndPassword("default@mcdefaultson.com", "default")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSignInClick("default@mcdefaultson.com", "default")
                        val user = Firebase.auth.currentUser

                        if (user != null) {
                            Log.d(TAG, "Testicular torsion \n${user.uid}")
                        }
                        // Sign in successful, navigate to the main app screen.
                        showNotification("Nils") // Pass a sample user string for now
                    } else {
                        //onSignInClick(email, password)
                        // Sign in failed, display an error message.
                        Log.e(TAG, "Error Login failed")
                    }
                }
        }) {
            Text("Test Sign In")
        }

        Button(onClick = {
            Log.d(TAG,"$email  $password")
            if (email == "" || password == "") {
                //Email or password empty
                Log.e(TAG, "Email or password empty")
            }
            else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSignInClick(email, password)
                            val user = Firebase.auth.currentUser

                            if (user != null) {
                                Log.d(TAG, "Testicular torsion \n${user.uid}")
                            }
                            showNotification(email)
                            // Sign in successful, navigate to the main app screen.
                        } else {
                            //onSignInClick(email, password)
                            // Sign in failed, display an error message.
                            Log.e(TAG, "Error Login failed")
                        }
                    }
            }
        }) {
            Text("Sign In")
        }
        /*
        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up")
        }

         */
    }
}

@Composable
fun LoginScreen(login: () -> Unit, modifier : Modifier = Modifier, showNotification: (String) -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextInput(text = stringResource(R.string.username), state = username)
        TextInput(text = stringResource(R.string.password), state = password)

        Button(onClick = {
            login()
            showNotification(username.value) // Pass a sample user string for now
        }) {
            Text(stringResource(R.string.login))
        }
    }
}

@Composable
fun TextInput(text : String, state : MutableState<String>){
    Row(){
        Text(text)
        BasicTextField(value = state.value,
            onValueChange = {newText -> state.value = newText},
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, Color.Gray)
        )
    }
}