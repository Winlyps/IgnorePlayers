//1.File: Ignore.kt
package winlyps.ignore

import org.bukkit.plugin.java.JavaPlugin
import winlyps.ignore.commands.IgnoreCommand
import winlyps.ignore.listeners.ChatListener
import winlyps.ignore.storage.IgnoreStorage

class Ignore : JavaPlugin() {

    override fun onEnable() {
        // Initialize storage
        val storage = IgnoreStorage(this)

        // Register command
        getCommand("ignore")?.setExecutor(IgnoreCommand(storage))

        // Register event listener
        server.pluginManager.registerEvents(ChatListener(storage), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
