package com.pdevlabs.systemlauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pdevlabs.systemlauncher.data.AppEntry
import com.pdevlabs.systemlauncher.data.AppRepository
import com.pdevlabs.systemlauncher.data.Folder
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
    val loading: Boolean = true,
    // Nova-like
    val homeLayout: Int = 0, // 0=Nova grid 1=Last list
    val homeColumns: Int = 4,
    val drawerColumns: Int = 4,
    val showDock: Boolean = true,
    val dock: Set<String> = emptySet(),
    val showLabels: Boolean = true,
    val folders: List<Folder> = emptyList()
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
    // Nova-like derived lists
    val dockApps: List<AppEntry>
        get() = allApps.filter { it.key in dock && it.key !in hidden }
    val homeGridApps: List<AppEntry>
        get() = homePinned.filter { it.key !in dock }
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
        prefs.showIcons, prefs.textOnly, prefs.iconSize,
        prefs.homeLayout, prefs.homeColumns, prefs.drawerColumns,
        prefs.showDock, prefs.dock, prefs.showLabels, prefs.folders
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
            iconSizeDp = (arr[8] as Int),
            homeLayout = (arr[9] as Int),
            homeColumns = (arr[10] as Int),
            drawerColumns = (arr[11] as Int),
            showDock = (arr[12] as Boolean),
            dock = (arr[13] as Set<String>),
            showLabels = (arr[14] as Boolean),
            folders = Folder.decodeAll(arr[15] as Set<String>)
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
    fun toggleDock(e: AppEntry) = viewModelScope.launch {
        prefs.toggleDock(e.key, state.value.dock)
    }
    fun toggleHide(e: AppEntry) = viewModelScope.launch {
        prefs.toggleHide(e.key, state.value.hidden)
    }
    // Folder ops
    fun createFolder(name: String, members: List<String> = emptyList()) = viewModelScope.launch {
        val clean = name.trim().ifBlank { return@launch }
        val updated = state.value.folders + Folder(name = clean, members = members)
        prefs.saveFolders(Folder.encodeAll(updated))
    }
    fun deleteFolder(id: String) = viewModelScope.launch {
        prefs.saveFolders(Folder.encodeAll(state.value.folders.filter { it.id != id }))
    }
    fun toggleFolderMember(folderId: String, appKey: String) = viewModelScope.launch {
        val updated = state.value.folders.map { f ->
            if (f.id != folderId) f
            else if (appKey in f.members) f.copy(members = f.members - appKey)
            else f.copy(members = f.members + appKey)
        }
        prefs.saveFolders(Folder.encodeAll(updated))
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
