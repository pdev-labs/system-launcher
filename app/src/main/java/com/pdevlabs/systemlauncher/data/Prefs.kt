package com.pdevlabs.systemlauncher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("launcher_prefs")

/**
 * Last Launcher-inspired prefs: pins, hidden, aliases, icons + theme.
 * No Nova grid/dock/folders.
 */
class Prefs(private val context: Context) {
    val showIcons: Flow<Boolean> = context.store.data.map { it[SHOW_ICONS] ?: true }
    val iconSize: Flow<Int> = context.store.data.map { it[ICON_SIZE] ?: 48 }
    val textOnly: Flow<Boolean> = context.store.data.map { it[TEXT_ONLY] ?: false }
    val darkTheme: Flow<Int> = context.store.data.map { it[THEME] ?: 0 } // 0=system 1=light 2=dark/amoled
    val autoLaunchSingle: Flow<Boolean> = context.store.data.map { it[AUTO_LAUNCH] ?: true }
    val pinned: Flow<Set<String>> = context.store.data.map { it[PINNED] ?: emptySet() }
    val hidden: Flow<Set<String>> = context.store.data.map { it[HIDDEN] ?: emptySet() }
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
        private val ICON_SIZE = intPreferencesKey("icon_size")
        private val THEME = intPreferencesKey("theme")
        private val PINNED = stringSetPreferencesKey("pinned")
        private val HIDDEN = stringSetPreferencesKey("hidden")
    }
}
