package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.data.Folder
import com.pdevlabs.systemlauncher.viewmodel.LauncherUiState

/**
 * Shared long-press menu: Nova actions (pin to home, dock, folders)
 * + Last Launcher actions (rename, hide, info, uninstall).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppActionsSheet(
    app: AppEntry?,
    state: LauncherUiState,
    folders: List<Folder>,
    onDismiss: () -> Unit,
    onRename: (AppEntry, String) -> Unit,
    onPin: (AppEntry) -> Unit,
    onDock: (AppEntry) -> Unit,
    onHide: (AppEntry) -> Unit,
    onInfo: (AppEntry) -> Unit,
    onUninstall: (AppEntry) -> Unit,
    onToggleFolderMember: (String, String) -> Unit,
    onCreateFolder: (String, String) -> Unit
) {
    if (app == null) return
    var aliasText by remember(app.key) { mutableStateOf(state.aliases[app.key] ?: "") }
    var newFolderName by remember(app.key) { mutableStateOf("") }
    val isPinned = app.key in state.pinned
    val isDocked = app.key in state.dock

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text(state.displayName(app))
            OutlinedTextField(
                value = aliasText,
                onValueChange = { aliasText = it },
                label = { Text("Rename (alias)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = { onRename(app, aliasText); onDismiss() }) { Text("Save name") }
            TextButton(onClick = { onPin(app); onDismiss() }) {
                Text(if (isPinned) "Unpin from Home" else "Pin to Home")
            }
            TextButton(onClick = { onDock(app); onDismiss() }) {
                Text(if (isDocked) "Remove from Dock" else "Add to Dock")
            }
            TextButton(onClick = { onHide(app); onDismiss() }) { Text("Hide app") }
            TextButton(onClick = { onInfo(app); onDismiss() }) { Text("App info") }
            TextButton(onClick = { onUninstall(app); onDismiss() }) { Text("Uninstall") }
            Text("Folders:", Modifier.padding(top = 8.dp))
            folders.forEach { f ->
                val inFolder = app.key in f.members
                TextButton(onClick = { onToggleFolderMember(f.id, app.key) }) {
                    Text(if (inFolder) "Remove from ${f.name}" else "Add to ${f.name}")
                }
            }
            OutlinedTextField(
                value = newFolderName,
                onValueChange = { newFolderName = it },
                label = { Text("New folder name (+ this app)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = {
                    if (newFolderName.isNotBlank()) {
                        onCreateFolder(newFolderName, app.key); onDismiss()
                    }
                }
            ) { Text("Create folder with this app") }
        }
    }
}
