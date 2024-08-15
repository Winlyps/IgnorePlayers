//2.File: IgnoreCommand.kt
package winlyps.ignore.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import winlyps.ignore.storage.IgnoreStorage

class IgnoreCommand(private val storage: IgnoreStorage) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) return false

        when (args[0].toLowerCase()) {
            "add" -> {
                if (args.size < 2) return false
                val target = sender.server.getPlayer(args[1])
                if (target == null) {
                    sender.sendMessage("${ChatColor.RED}Player not found or not online.")
                    return true
                }
                storage.addIgnore(sender.uniqueId, target.uniqueId)
                sender.sendMessage("${ChatColor.GREEN}${target.name} has been added to your ignore list.")
            }
            "remove" -> {
                if (args.size < 2) return false
                val target = sender.server.getPlayer(args[1])
                if (target == null) {
                    val offlineTarget = sender.server.getOfflinePlayer(args[1])
                    if (offlineTarget.uniqueId !in storage.getIgnoredPlayers(sender.uniqueId)) {
                        sender.sendMessage("${ChatColor.RED}Player not found in your ignore list.")
                        return true
                    }
                    storage.removeIgnore(sender.uniqueId, offlineTarget.uniqueId)
                    sender.sendMessage("${ChatColor.GREEN}${offlineTarget.name ?: "Unknown"} has been removed from your ignore list.")
                } else {
                    storage.removeIgnore(sender.uniqueId, target.uniqueId)
                    sender.sendMessage("${ChatColor.GREEN}${target.name} has been removed from your ignore list.")
                }
            }
            "list" -> {
                val ignoredPlayers = storage.getIgnoredPlayerNames(sender.uniqueId)
                if (ignoredPlayers.isEmpty()) {
                    sender.sendMessage("${ChatColor.YELLOW}Your ignore list is empty.")
                } else {
                    val ignoredList = ignoredPlayers.joinToString(", ")
                    sender.sendMessage("${ChatColor.YELLOW}Ignored players: $ignoredList")
                }
            }
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()

        if (args.size == 1) {
            return listOf("add", "remove", "list").filter { it.startsWith(args[0], true) }
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