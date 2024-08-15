//1.File: Ignore.kt
package winlyps.ignore

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin
import winlyps.ignore.commands.IgnoreCommand
import winlyps.ignore.listeners.ChatListener
import winlyps.ignore.listeners.CommandPreprocessListener
import winlyps.ignore.storage.IgnoreStorage

class Ignore : JavaPlugin() {

    lateinit var audiences: BukkitAudiences

    override fun onEnable() {
        audiences = BukkitAudiences.create(this)

        // Initialize storage
        val storage = IgnoreStorage(this)

        // Register command
        getCommand("ignore")?.setExecutor(IgnoreCommand(storage, audiences))

        // Register event listener
        server.pluginManager.registerEvents(ChatListener(storage, audiences), this)
        server.pluginManager.registerEvents(CommandPreprocessListener(storage, audiences), this)
    }

    override fun onDisable() {
        audiences.close()
    }
}
