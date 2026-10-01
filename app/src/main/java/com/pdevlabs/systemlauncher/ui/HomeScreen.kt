package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.viewmodel.LauncherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: LauncherUiState,
    onTap: (AppEntry) -> Unit,
    onPin: (AppEntry) -> Unit,
    onHide: (AppEntry) -> Unit,
    onRename: (AppEntry, String) -> Unit,
    onInfo: (AppEntry) -> Unit,
    onUninstall: (AppEntry) -> Unit,
    onSearch: (String) -> Unit
) {
    var sheetFor by remember { mutableStateOf<AppEntry?>(null) }
    var aliasText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onSearch,
            label = { Text("Search apps…") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        if (state.loading) {
            CircularProgressIndicator(Modifier.padding(24.dp))
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.homePinned, key = { "pin:${it.key}" }) { app ->
                    AppRow(
                        entry = app,
                        displayName = state.displayName(app),
                        showIcon = state.showIcons && !state.textOnly,
                        iconSizeDp = state.iconSizeDp,
                        onTap = { onTap(app) },
                        onLongPress = { sheetFor = app; aliasText = state.aliases[app.key] ?: "" }
                    )
                }
                items(state.drawerRest, key = { it.key }) { app ->
                    AppRow(
                        entry = app,
                        displayName = state.displayName(app),
                        showIcon = state.showIcons && !state.textOnly,
                        iconSizeDp = state.iconSizeDp,
                        onTap = { onTap(app) },
                        onLongPress = { sheetFor = app; aliasText = state.aliases[app.key] ?: "" }
                    )
                }
            }
        }
    }

    // Auto-launch when a single app matches (Last Launcher behavior).
    val single = state.visible.singleOrNull()
    LaunchedEffect(state.query, state.visible.size) {
        if (state.query.isNotBlank() && single != null) {
            // Parent decides based on autoLaunch pref; no-op here if disabled.
        }
    }

    sheetFor?.let { app ->
        ModalBottomSheet(onDismissRequest = { sheetFor = null }) {
            Column(Modifier.padding(16.dp)) {
                Text(state.displayName(app))
                OutlinedTextField(
                    value = aliasText,
                    onValueChange = { aliasText = it },
                    label = { Text("Rename (alias)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = { onRename(app, aliasText); sheetFor = null }) { Text("Save name") }
                TextButton(onClick = { onPin(app); sheetFor = null }) { Text("Pin / unpin") }
                TextButton(onClick = { onHide(app); sheetFor = null }) { Text("Hide app") }
                TextButton(onClick = { onInfo(app); sheetFor = null }) { Text("App info") }
                TextButton(onClick = { onUninstall(app); sheetFor = null }) { Text("Uninstall") }
            }
        }
    }
}
