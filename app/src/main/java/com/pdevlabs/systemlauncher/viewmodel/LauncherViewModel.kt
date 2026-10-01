package com.pdevlabs.systemlauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.data.AppRepository
import com.pdevlabs.systemlauncher.data.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LauncherUiState(
    val query: String = "",
    val allApps: List<AppEntry> = emptyList(),
    val pinned: Set<String> = emptySet(),
    val hidden: Set<String> = emptySet(),
    val aliases: Map<String, String> = emptyMap(),
    val showIcons: Boolean = true,
    val textOnly: Boolean = false,
    val iconSizeDp: Int = 48,
    val loading: Boolean = true
) {
    fun displayName(e: AppEntry): String = aliases[e.key]?.takeIf { it.isNotBlank() } ?: e.label
    val visible: List<AppEntry>
        get() = allApps.filter { it.key !in hidden }.let { list ->
            if (query.isBlank()) list
            else list.filter { displayName(it).contains(query.trim(), ignoreCase = true) }
        }
    val homePinned: List<AppEntry>
        get() = visible.filter { it.key in pinned }
    val drawerRest: List<AppEntry>
        get() = visible.filter { it.key !in pinned }
}

class LauncherViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppRepository.fresh(app)
    private val prefs = Prefs(app)

    private val _query = MutableStateFlow("")
    private val _apps = MutableStateFlow<List<AppEntry>>(emptyList())
    private val _loading = MutableStateFlow(true)

    val state: StateFlow<LauncherUiState> = combine(
        _query, _apps, _loading,
        prefs.pinned, prefs.hidden, prefs.aliases,
        prefs.showIcons, prefs.textOnly, prefs.iconSizeDp
    ) { arr ->
        @Suppress("UNCHECKED_CAST")
        LauncherUiState(
            query = arr[0] as String,
            allApps = arr[1] as List<AppEntry>,
            loading = arr[2] as Boolean,
            pinned = arr[3] as Set<String>,
            hidden = arr[4] as Set<String>,
            aliases = arr[5] as Map<String, String>,
            showIcons = arr[6] as Boolean,
            textOnly = arr[7] as Boolean,
            iconSizeDp = (arr[8] as Int)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LauncherUiState())

    val settings = prefs

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _loading.value = true
        _apps.value = repo.loadApps()
        _loading.value = false
    }

    fun onQuery(q: String) { _query.value = q }

    fun launch(e: AppEntry) = repo.launch(e.packageName, e.className)

    fun togglePin(e: AppEntry) = viewModelScope.launch {
        prefs.togglePin(e.key, state.value.pinned)
    }
    fun toggleHide(e: AppEntry) = viewModelScope.launch {
        prefs.toggleHide(e.key, state.value.hidden)
    }
    fun rename(e: AppEntry, alias: String) = viewModelScope.launch {
        prefs.setAlias(e.key, alias)
    }
    fun openInfo(e: AppEntry) = viewModelScope.launch {
        getApplication<Application>().startActivity(repo.appInfoIntent(e.packageName))
    }
    fun openUninstall(e: AppEntry) = viewModelScope.launch {
        getApplication<Application>().startActivity(repo.uninstallIntent(e.packageName))
    }
}
