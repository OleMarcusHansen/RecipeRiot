package no.hiof.reciperiot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(logout: () -> Unit, modifier : Modifier = Modifier) {
    var darkTheme by remember { mutableStateOf(false) }
    var otherSetting by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)){
        SwitchSetting(bool = darkTheme, update = {newValue -> darkTheme = newValue}, text = "Dark theme")
        SwitchSetting(bool = otherSetting, update = {newValue -> otherSetting = newValue}, text = "Other setting")
        Button(onClick = { logout() }) {
            Text("Log out")
        }
    }
}

@Composable
fun SwitchSetting(bool: Boolean, update: (Boolean) -> Unit, text: String){
    Row {
        Switch(checked = bool, onCheckedChange = {
            val newValue = !bool
            update(newValue)
        })
        Text(text)
    }
}