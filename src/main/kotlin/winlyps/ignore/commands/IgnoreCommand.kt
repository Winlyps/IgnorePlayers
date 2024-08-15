//2.File: IgnoreCommand.kt
package winlyps.ignore.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import winlyps.ignore.storage.IgnoreStorage

class IgnoreCommand(private val storage: IgnoreStorage, private val audiences: BukkitAudiences) : CommandExecutor, TabCompleter {

    private val miniMessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.size < 2) return false

        val target = sender.server.getPlayer(args[1])
        if (target == null) {
            audiences.sender(sender).sendMessage(miniMessage.deserialize("<red>Player not found or not online.</red>"))
            return true
        }

        when (args[0].toLowerCase()) {
            "add" -> {
                storage.addIgnore(sender.uniqueId, target.uniqueId)
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<green>${target.name} has been added to your ignore list.</green>"))
            }
            "remove" -> {
                storage.removeIgnore(sender.uniqueId, target.uniqueId)
                audiences.sender(sender).sendMessage(miniMessage.deserialize("<green>${target.name} has been removed from your ignore list.</green>"))
            }
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()

        if (args.size == 1) {
            return listOf("add", "remove").filter { it.startsWith(args[0], true) }
        }

        if (args.size == 2) {
            return when (args[0].toLowerCase()) {
                "add" -> sender.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[1], true) }
                "remove" -> {
                    val ignoredPlayers = storage.getIgnoredPlayers(sender.uniqueId)
                    val onlinePlayers = sender.server.onlinePlayers.filter { ignoredPlayers.contains(it.uniqueId) }.map { it.name }
                    val offlinePlayers = ignoredPlayers.filterNot { sender.server.onlinePlayers.map { player -> player.uniqueId }.contains(it) }.map { sender.server.getOfflinePlayer(it).name ?: "Unknown" }
                    (onlinePlayers + offlinePlayers).filter { it.startsWith(args[1], true) }
                }
                else -> emptyList()
            }
        }

        return emptyList()
    }
}