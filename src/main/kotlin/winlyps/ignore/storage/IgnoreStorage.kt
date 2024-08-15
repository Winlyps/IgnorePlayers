//4.File: IgnoreStorage.kt
package winlyps.ignore.storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID

class IgnoreStorage(private val plugin: JavaPlugin) {

    private val ignoreList: MutableMap<UUID, MutableSet<UUID>> = mutableMapOf()
    private val gson = Gson()
    private val file = File(plugin.dataFolder, "ignorelist.json")

    init {
        load()
    }

    fun addIgnore(player: UUID, target: UUID) {
        ignoreList.getOrPut(player) { mutableSetOf() }.add(target)
        save()
    }

    fun removeIgnore(player: UUID, target: UUID) {
        ignoreList[player]?.remove(target)
        save()
    }

    fun isIgnored(player: UUID, target: UUID): Boolean {
        return ignoreList[player]?.contains(target) ?: false
    }

    private fun load() {
        if (!file.exists()) {
            save() // Create the file if it doesn't exist
            return
        }

        FileReader(file).use { reader ->
            val type = object : TypeToken<Map<UUID, Set<UUID>>>() {}.type
            ignoreList.putAll(gson.fromJson(reader, type))
        }
    }

    private fun save() {
        plugin.dataFolder.mkdirs() // Ensure the plugin's data folder exists
        FileWriter(file).use { writer ->
            gson.toJson(ignoreList, writer)
        }
    }
}