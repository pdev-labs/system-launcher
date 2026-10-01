package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.viewmodel.LauncherUiState

/**
 * Nova-like drawer: searchable grid (default) with Last Launcher instant filter.
 * Falls back to list when textOnly is on.
 */
@Composable
fun DrawerScreen(
    state: LauncherUiState,
    onTap: (AppEntry) -> Unit,
    onLongPress: (AppEntry) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(top = 12.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onSearch,
            label = { Text("Search apps… (auto-launch on single match)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        if (state.loading) {
            CircularProgressIndicator(Modifier.padding(24.dp))
        } else if (state.textOnly) {
            // Pure Last Launcher mode.
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.visible, key = { it.key }) { app ->
                    AppRow(
                        entry = app,
                        displayName = state.displayName(app),
                        showIcon = false,
                        iconSizeDp = state.iconSizeDp,
                        onTap = { onTap(app) },
                        onLongPress = { onLongPress(app) }
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(state.drawerColumns.coerceIn(3, 6)),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.visible, key = { it.key }) { app: AppEntry ->
                    AppGridIcon(
                        entry = app,
                        displayName = state.displayName(app),
                        showIcon = state.showIcons,
                        showLabel = state.showLabels,
                        iconSizeDp = state.iconSizeDp,
                        onTap = { onTap(app) },
                        onLongPress = { onLongPress(app) }
                    )
                }
            }
        }
    }
}
