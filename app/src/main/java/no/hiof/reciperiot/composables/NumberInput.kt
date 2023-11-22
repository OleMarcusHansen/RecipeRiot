package no.hiof.reciperiot.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberInput(text : String, state : MutableState<String>){
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