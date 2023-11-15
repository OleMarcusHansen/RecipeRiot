package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.reciperiot.ViewModels.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(modifier: Modifier = Modifier, db: FirebaseFirestore, ShoppingListViewModel: ShoppingListViewModel = viewModel()) {
    //val textState = remember { mutableStateOf("") }
    //val prevState = remember { mutableStateOf("") }
    ShoppingListViewModel.getShoppingList(db) { data ->
        if (ShoppingListViewModel.textState.isEmpty()) {
            ShoppingListViewModel.textState = data.toString()
        }
    }
    Card(modifier = modifier
        .fillMaxSize()
        .padding(10.dp, 0.dp, 10.dp, 95.dp),
        elevation = CardDefaults.cardElevation(8.dp)){
        TextField(value = ShoppingListViewModel.textState,
            onValueChange = { newText ->
                ShoppingListViewModel.textState = newText
            },
            textStyle = TextStyle(fontSize = 16.sp),
            label = {Text("Shopping List")},
            modifier = modifier.fillMaxSize()
        )
    }
    LaunchedEffect(ShoppingListViewModel.textState) {
        //TODO: sjekke om if burde være i viewmodel
        if (ShoppingListViewModel.prevState != ShoppingListViewModel.textState) {
            ShoppingListViewModel.saveShoppinglistToDb(db, ShoppingListViewModel.textState)
            ShoppingListViewModel.prevState = ShoppingListViewModel.textState
        }
    }
    // Button to save the shopping list
    /*
    Button(
        onClick = {
            // Call the saveShoppinglistToDb function with the text content
            saveShoppinglistToDb(db, textState.value)
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Save Shopping List")
    }
     */
}