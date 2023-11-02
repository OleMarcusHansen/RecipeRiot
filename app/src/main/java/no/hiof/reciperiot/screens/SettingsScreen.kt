package no.hiof.reciperiot.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(logout: () -> Unit, modifier : Modifier = Modifier) {
    var darkTheme by remember { mutableStateOf(false) }
    var otherSetting by remember { mutableStateOf(false) }

    var langExpanded by remember { mutableStateOf(false)}
    val langItems = listOf("English", "Norwegian")
    var langSelectedIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier
        .padding(horizontal = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)){
        SwitchSetting(bool = darkTheme, update = {newValue -> darkTheme = newValue}, text = "Dark theme")
        SwitchSetting(bool = otherSetting, update = {newValue -> otherSetting = newValue}, text = "Other setting")
        Row{
            Text("Language")
            Box(modifier = Modifier.fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .clip(shape = RoundedCornerShape(15.dp))) {
                Text(
                    langItems[langSelectedIndex],
                    Modifier.clickable(onClick = { langExpanded = true })
                        .background(Color.Gray)
                        .padding(10.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                )
                DropdownMenu(expanded = langExpanded,
                    onDismissRequest = { langExpanded = false }) {
                    langItems.forEachIndexed { index, s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = {
                            langSelectedIndex = index
                            langExpanded = false
                        })
                    }
                }
            }
        }
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