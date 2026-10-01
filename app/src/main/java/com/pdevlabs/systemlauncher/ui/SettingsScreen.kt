package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdevlabs.systemlauncher.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

/** Last Launcher-inspired settings: icons, text-only, theme. No Nova options. */
@Composable
fun SettingsScreen(vm: LauncherViewModel, onShowHidden: () -> Unit) {
    val s = vm.state.collectAsState().value
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(20.dp)) {
        Text("Appearance")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Show icon logos", Modifier.weight(1f))
            Switch(checked = s.showIcons, onCheckedChange = {
                scope.launch { vm.settings.setShowIcons(it) }
            })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Text-only (pure Last Launcher)", Modifier.weight(1f))
            Switch(checked = s.textOnly, onCheckedChange = {
                scope.launch { vm.settings.setTextOnly(it) }
            })
        }
        Text("Icon size: ${s.iconSizeDp}dp")
        Slider(
            value = s.iconSizeDp.toFloat(),
            onValueChange = { scope.launch { vm.settings.setIconSize(it.toInt()) } },
            valueRange = 32f..72f
        )
        Text("Behavior")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Theme: 0=system 1=light 2=dark", Modifier.weight(1f))
        }
        Row {
            TextButton(onClick = { scope.launch { vm.settings.setTheme(0) } }) { Text("System") }
            TextButton(onClick = { scope.launch { vm.settings.setTheme(1) } }) { Text("Light") }
            TextButton(onClick = { scope.launch { vm.settings.setTheme(2) } }) { Text("Dark") }
        }
        TextButton(onClick = onShowHidden) { Text("Unhide apps (${s.hidden.size} hidden)") }
        Text("Tip: long-press any row for pin / rename / hide / uninstall.", Modifier.padding(top = 12.dp))
    }
}
