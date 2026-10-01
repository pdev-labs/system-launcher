package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun SettingsScreen(vm: LauncherViewModel, onShowHidden: () -> Unit) {
    val s = vm.state.collectAsState().value
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Home style (Nova vs Last)")
        Row {
            TextButton(onClick = { scope.launch { vm.settings.setHomeLayout(0) } }) {
                Text(if (s.homeLayout == 0) "● Nova grid" else "Nova grid")
            }
            TextButton(onClick = { scope.launch { vm.settings.setHomeLayout(1) } }) {
                Text(if (s.homeLayout == 1) "● Last list" else "Last list")
            }
        }
        Text("Home columns: ${s.homeColumns}")
        Slider(
            value = s.homeColumns.toFloat(),
            onValueChange = { scope.launch { vm.settings.setHomeColumns(it.toInt()) } },
            valueRange = 3f..6f, steps = 2
        )
        Text("Drawer columns: ${s.drawerColumns}")
        Slider(
            value = s.drawerColumns.toFloat(),
            onValueChange = { scope.launch { vm.settings.setDrawerColumns(it.toInt()) } },
            valueRange = 3f..6f, steps = 2
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Show dock", Modifier.weight(1f))
            Switch(checked = s.showDock, onCheckedChange = {
                scope.launch { vm.settings.setShowDock(it) }
            })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Show labels under icons", Modifier.weight(1f))
            Switch(checked = s.showLabels, onCheckedChange = {
                scope.launch { vm.settings.setShowLabels(it) }
            })
        }
        Text("Appearance")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Show icon logos", Modifier.weight(1f))
            Switch(checked = s.showIcons, onCheckedChange = {
                scope.launch { vm.settings.setShowIcons(it) }
            })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Text-only (Last Launcher mode)", Modifier.weight(1f))
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
        Text("Folders: ${s.folders.size} on Home. Long-press → create/add.", Modifier.padding(top = 4.dp))
        Text("Tip: Home=Nova grid+dock+folders. Drawer=search grid. Last list=text mode.", Modifier.padding(top = 12.dp))
    }
}
