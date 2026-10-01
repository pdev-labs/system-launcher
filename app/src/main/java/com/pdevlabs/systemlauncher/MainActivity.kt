package com.pdevlabs.systemlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.data.Folder
import com.pdevlabs.systemlauncher.ui.AppActionsSheet
import com.pdevlabs.systemlauncher.ui.DrawerScreen
import com.pdevlabs.systemlauncher.ui.FolderSheet
import com.pdevlabs.systemlauncher.ui.HomeScreen
import com.pdevlabs.systemlauncher.ui.NovaHomeScreen
import com.pdevlabs.systemlauncher.ui.SettingsScreen
import com.pdevlabs.systemlauncher.ui.theme.LauncherTheme
import com.pdevlabs.systemlauncher.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by vm.state.collectAsState()
            val scope = rememberCoroutineScope()
            // 0=Home (Nova grid or Last list) 1=Drawer 2=Settings
            var tab by remember { mutableIntStateOf(0) }
            var actionApp by remember { mutableStateOf<AppEntry?>(null) }
            var openFolder by remember { mutableStateOf<Folder?>(null) }
            var folderMenu by remember { mutableStateOf<Folder?>(null) }

            LauncherTheme(dark = isSystemInDarkTheme()) {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize()) {
                        Column(Modifier.weight(1f).fillMaxWidth()) {
                            when (tab) {
                                0 -> if (state.homeLayout == 0) {
                                    NovaHomeScreen(
                                        state = state,
                                        onTap = { vm.launch(it) },
                                        onLongPressApp = { actionApp = it },
                                        onOpenFolder = { openFolder = it },
                                        onLongPressFolder = { folderMenu = it }
                                    )
                                } else {
                                    // Last Launcher mode preserved.
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
                                }
                                1 -> DrawerScreen(
                                    state = state,
                                    onTap = { vm.launch(it) },
                                    onLongPress = { actionApp = it },
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
                                else -> SettingsScreen(vm = vm, onShowHidden = {
                                    state.allApps.filter { it.key in state.hidden }
                                        .forEach { vm.toggleHide(it) }
                                })
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { tab = 0 },
                                modifier = Modifier.weight(1f)
                            ) { Text("Home") }
                            Button(
                                onClick = { tab = 1 },
                                modifier = Modifier.weight(1f)
                            ) { Text("Drawer") }
                            Button(
                                onClick = { tab = 2 },
                                modifier = Modifier.weight(1f)
                            ) { Text("Settings") }
                        }
                        TextButton(onClick = { vm.refresh() }) { Text("Refresh apps") }
                    }

                    // Shared sheets
                    AppActionsSheet(
                        app = actionApp,
                        state = state,
                        folders = state.folders,
                        onDismiss = { actionApp = null },
                        onRename = { a, n -> vm.rename(a, n) },
                        onPin = { vm.togglePin(it) },
                        onDock = { vm.toggleDock(it) },
                        onHide = { vm.toggleHide(it) },
                        onInfo = { vm.openInfo(it) },
                        onUninstall = { vm.openUninstall(it) },
                        onToggleFolderMember = { fid, key -> vm.toggleFolderMember(fid, key) },
                        onCreateFolder = { name, key -> vm.createFolder(name, listOf(key)) }
                    )
                    FolderSheet(
                        folder = openFolder,
                        state = state,
                        onDismiss = { openFolder = null },
                        onLaunch = { vm.launch(it) },
                        onRemove = { key ->
                            openFolder?.let { vm.toggleFolderMember(it.id, key) }
                        },
                        onDelete = { vm.deleteFolder(it) }
                    )
                    // Long-press folder on home: open it (delete available inside).
                    folderMenu?.let {
                        openFolder = it
                        folderMenu = null
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
