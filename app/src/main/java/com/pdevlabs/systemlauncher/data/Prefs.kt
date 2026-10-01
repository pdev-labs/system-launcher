package com.pdevlabs.systemlauncher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("launcher_prefs")

/** Persists pins, hidden apps, aliases, icon + theme settings.
 *  Nova-like additions: home layout (grid vs Last list), columns, dock, folders, labels. */
class Prefs(private val context: Context) {
    val showIcons: Flow<Boolean> = context.store.data.map { it[SHOW_ICONS] ?: true }
    val iconSize: Flow<Int> = context.store.data.map { it[ICON_SIZE] ?: 48 }
    val textOnly: Flow<Boolean> = context.store.data.map { it[TEXT_ONLY] ?: false }
    val darkTheme: Flow<Int> = context.store.data.map { it[THEME] ?: 0 } // 0=system 1=light 2=dark/amoled
    val autoLaunchSingle: Flow<Boolean> = context.store.data.map { it[AUTO_LAUNCH] ?: true }
    val pinned: Flow<Set<String>> = context.store.data.map { it[PINNED] ?: emptySet() }
    val hidden: Flow<Set<String>> = context.store.data.map { it[HIDDEN] ?: emptySet() }
    // Nova-like
    val homeLayout: Flow<Int> = context.store.data.map { it[HOME_LAYOUT] ?: 0 } // 0=Nova grid 1=Last list
    val homeColumns: Flow<Int> = context.store.data.map { it[HOME_COLS] ?: 4 }
    val drawerColumns: Flow<Int> = context.store.data.map { it[DRAWER_COLS] ?: 4 }
    val showDock: Flow<Boolean> = context.store.data.map { it[SHOW_DOCK] ?: true }
    val dock: Flow<Set<String>> = context.store.data.map { it[DOCK] ?: emptySet() }
    val showLabels: Flow<Boolean> = context.store.data.map { it[SHOW_LABELS] ?: true }
    val folders: Flow<Set<String>> = context.store.data.map { it[FOLDERS] ?: emptySet() }
    val aliases: Flow<Map<String, String>> = context.store.data.map { p ->
        p.asMap().entries.mapNotNull {
            if (it.key.name.startsWith("alias_")) it.key.name.removePrefix("alias_") to (it.value as? String ?: "") else null
        }.toMap()
    }

    suspend fun setShowIcons(v: Boolean) = context.store.edit { it[SHOW_ICONS] = v }
    suspend fun setIconSize(dp: Int) = context.store.edit { it[ICON_SIZE] = dp.coerceIn(32, 72) }
    suspend fun setTextOnly(v: Boolean) = context.store.edit { it[TEXT_ONLY] = v }
    suspend fun setTheme(v: Int) = context.store.edit { it[THEME] = v }
    suspend fun setAutoLaunch(v: Boolean) = context.store.edit { it[AUTO_LAUNCH] = v }
    // Nova-like setters
    suspend fun setHomeLayout(v: Int) = context.store.edit { it[HOME_LAYOUT] = v.coerceIn(0, 1) }
    suspend fun setHomeColumns(v: Int) = context.store.edit { it[HOME_COLS] = v.coerceIn(3, 6) }
    suspend fun setDrawerColumns(v: Int) = context.store.edit { it[DRAWER_COLS] = v.coerceIn(3, 6) }
    suspend fun setShowDock(v: Boolean) = context.store.edit { it[SHOW_DOCK] = v }
    suspend fun setShowLabels(v: Boolean) = context.store.edit { it[SHOW_LABELS] = v }
    suspend fun toggleDock(key: String, dockNow: Set<String>) = context.store.edit {
        it[DOCK] = if (key in dockNow) dockNow - key else (dockNow + key).toList().takeLast(8).toSet()
    }
    suspend fun saveFolders(encoded: Set<String>) = context.store.edit { it[FOLDERS] = encoded }

    suspend fun togglePin(key: String, pinnedNow: Set<String>) = context.store.edit {
        it[PINNED] = if (key in pinnedNow) pinnedNow - key else pinnedNow + key
    }
    suspend fun toggleHide(key: String, hiddenNow: Set<String>) = context.store.edit {
        it[HIDDEN] = if (key in hiddenNow) hiddenNow - key else hiddenNow + key
    }
    suspend fun setAlias(key: String, alias: String) = context.store.edit {
        if (alias.isBlank()) it.remove(stringPreferencesKey("alias_$key"))
        else it[stringPreferencesKey("alias_$key")] = alias
    }

    companion object {
        private val SHOW_ICONS = booleanPreferencesKey("show_icons")
        private val TEXT_ONLY = booleanPreferencesKey("text_only")
        private val AUTO_LAUNCH = booleanPreferencesKey("auto_launch")
        private val ICON_SIZE = androidx.datastore.preferences.core.intPreferencesKey("icon_size")
        private val THEME = androidx.datastore.preferences.core.intPreferencesKey("theme")
        private val PINNED = stringSetPreferencesKey("pinned")
        private val HIDDEN = stringSetPreferencesKey("hidden")
        // Nova-like keys
        private val HOME_LAYOUT = androidx.datastore.preferences.core.intPreferencesKey("home_layout")
        private val HOME_COLS = androidx.datastore.preferences.core.intPreferencesKey("home_cols")
        private val DRAWER_COLS = androidx.datastore.preferences.core.intPreferencesKey("drawer_cols")
        private val SHOW_DOCK = booleanPreferencesKey("show_dock")
        private val SHOW_LABELS = booleanPreferencesKey("show_labels")
        private val DOCK = stringSetPreferencesKey("dock")
        private val FOLDERS = stringSetPreferencesKey("folders")
    }
}
