package com.pdevlabs.systemlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pdevlabs.systemlauncher.ui.HomeScreen
import com.pdevlabs.systemlauncher.ui.SettingsScreen
import com.pdevlabs.systemlauncher.ui.theme.LauncherTheme
import com.pdevlabs.systemlauncher.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

/**
 * Last Launcher-inspired single-page launcher with icon logos.
 * Home list + Settings only. No Nova drawer/grid/dock.
 */
class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by vm.state.collectAsState()
            val scope = rememberCoroutineScope()
            var tab by remember { mutableIntStateOf(0) }
            LauncherTheme(dark = isSystemInDarkTheme()) {
                Surface(Modifier.fillMaxSize()) {
                    Column {
                        if (tab == 0) {
                            HomeScreen(
                                state = state,
                                onTap = { vm.launch(it) },
                                onPin = { vm.togglePin(it) },
                                onHide = { vm.toggleHide(it) },
                                onRename = { a, n -> vm.rename(a, n) },
                                onInfo = { vm.openInfo(it) },
                                onUninstall = { vm.openUninstall(it) },
                                onSearch = { q ->
                                    vm.onQuery(q)
                                    scope.launch {
                                        val cur = vm.state.value
                                        if (q.isNotBlank() && cur.visible.size == 1) {
                                            vm.launch(cur.visible.first())
                                        }
                                    }
                                }
                            )
                        } else {
                            SettingsScreen(vm = vm, onShowHidden = {
                                state.allApps.filter { it.key in state.hidden }
                                    .forEach { vm.toggleHide(it) }
                            })
                        }
                        Button(onClick = { tab = if (tab == 0) 1 else 0 }) {
                            Text(if (tab == 0) "Settings" else "Home")
                        }
                        TextButton(onClick = { vm.refresh() }) { Text("Refresh apps") }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refresh()
    }
}
