package com.pdevlabs.systemlauncher.data

import java.util.UUID

/**
 * Nova-style folder on the home screen.
 * Encoded for DataStore stringSet as "id|name|key1,key2".
 */
data class Folder(
    val id: String = UUID.randomUUID().toString().take(8),
    val name: String,
    val members: List<String> = emptyList()
) {
    fun encode(): String = "$id|$name|${members.joinToString(",")}"

    companion object {
        fun decode(raw: String): Folder? {
            val parts = raw.split("|")
            if (parts.size < 2) return null
            val id = parts[0].ifBlank { return null }
            val name = parts[1].ifBlank { return null }
            val members = if (parts.size > 2 && parts[2].isNotBlank()) parts[2].split(",").filter { it.isNotBlank() } else emptyList()
            return Folder(id, name, members)
        }

        fun decodeAll(raw: Set<String>): List<Folder> = raw.mapNotNull { decode(it) }
        fun encodeAll(folders: List<Folder>): Set<String> = folders.map { it.encode() }.toSet()
    }
}
