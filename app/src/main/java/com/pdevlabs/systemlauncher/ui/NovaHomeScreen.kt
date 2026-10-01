package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.data.Folder
import com.pdevlabs.systemlauncher.viewmodel.LauncherUiState

/**
 * Nova-like home: grid of pinned apps + folders, dock row at bottom.
 * Last Launcher behaviors preserved: long-press actions, hide/rename, fast tap-to-launch.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaHomeScreen(
    state: LauncherUiState,
    onTap: (AppEntry) -> Unit,
    onLongPressApp: (AppEntry) -> Unit,
    onOpenFolder: (Folder) -> Unit,
    onLongPressFolder: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (state.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center).padding(24.dp))
            } else if (state.homeGridApps.isEmpty() && state.folders.isEmpty()) {
                Text(
                    "Long-press apps in Drawer to pin them here.\nNova-style home, Last Launcher speed.",
                    Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(state.homeColumns.coerceIn(3, 6)),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.folders, key = { "folder:${it.id}" }) { folder ->
                        FolderGridIcon(
                            name = folder.name,
                            count = folder.members.size,
                            onTap = { onOpenFolder(folder) },
                            onLongPress = { onLongPressFolder(folder) }
                        )
                    }
                    items(state.homeGridApps, key = { it.key }) { app ->
                        AppGridIcon(
                            entry = app,
                            displayName = state.displayName(app),
                            showIcon = state.showIcons && !state.textOnly,
                            showLabel = state.showLabels,
                            iconSizeDp = state.iconSizeDp,
                            onTap = { onTap(app) },
                            onLongPress = { onLongPressApp(app) }
                        )
                    }
                }
            }
        }
        // Dock — Nova signature. Shows dock apps, always visible.
        if (state.showDock) {
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.dockApps.isEmpty()) {
                    Text("Dock: long-press → Dock in Drawer", modifier = Modifier.padding(8.dp))
                } else {
                    state.dockApps.take(8).forEach { app ->
                        AppGridIcon(
                            entry = app,
                            displayName = state.displayName(app),
                            showIcon = state.showIcons && !state.textOnly,
                            showLabel = state.showLabels,
                            iconSizeDp = (state.iconSizeDp - 8).coerceAtLeast(32),
                            onTap = { onTap(app) },
                            onLongPress = { onLongPressApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSheet(
    folder: Folder?,
    state: LauncherUiState,
    onDismiss: () -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onRemove: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    if (folder == null) return
    val members = state.allApps.filter { it.key in folder.members }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("📁 ${folder.name} (${members.size})")
            members.forEach { app ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        AppRow(
                            entry = app,
                            displayName = state.displayName(app),
                            showIcon = state.showIcons && !state.textOnly,
                            iconSizeDp = 40,
                            onTap = { onLaunch(app) },
                            onLongPress = {}
                        )
                    }
                    TextButton(onClick = { onRemove(app.key) }) { Text("Remove") }
                }
            }
            TextButton(onClick = { onDelete(folder.id); onDismiss() }) { Text("Delete folder") }
            // Add-members hint: use Drawer long-press → folder toggles (wired in MainActivity sheet).
            var newName by remember(folder.id) { mutableStateOf("") }
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Add by search name… (use Drawer)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
