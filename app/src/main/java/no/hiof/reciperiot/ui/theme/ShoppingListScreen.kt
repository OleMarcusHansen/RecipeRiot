package no.hiof.reciperiot.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.hiof.reciperiot.ui.theme.RecipeRiotTheme

@Composable
fun ShoppingListScreen() {
    // Create a remember variable to hold the text entered in the input field
    val textState = remember { mutableStateOf("") }

    Column {
        BasicTextField(
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
        )
    }
}

@Preview
@Composable
fun ShoppingListScreenPreview() {
    RecipeRiotTheme {
        ShoppingListScreen()
    }
}
