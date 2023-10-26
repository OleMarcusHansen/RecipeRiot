package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(modifier: Modifier = Modifier) {
    // Create a remember variable to hold the text entered in the input field
    val textState = remember { mutableStateOf("") }

    Card(modifier = modifier.fillMaxSize().padding(10.dp, 0.dp, 10.dp, 95.dp),
        elevation = CardDefaults.cardElevation(8.dp)){
        TextField(value = textState.value,
            onValueChange = { newText ->
                // Update the textState with the new text when it changes
                textState.value = newText
            },
            textStyle = TextStyle(fontSize = 16.sp),
            label = {Text("Shopping List")},
            modifier = modifier.fillMaxSize()
        )
        /*BasicTextField(
            value = textState.value,
            onValueChange = { newText ->
                // Update the textState with the new text when it changes
                textState.value = newText
            },
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .fillMaxWidth()
                .border(1.dp, Color.Gray), // Add border for styling
        )
        Text(
            text = "ShoppingListScreen",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )*/
    }
}

@Preview
@Composable
fun ShoppingListScreenPreview() {
    AppTheme {
        ShoppingListScreen()
    }
}
