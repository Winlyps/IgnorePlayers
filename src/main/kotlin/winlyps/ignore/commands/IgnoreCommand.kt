//2.File: IgnoreCommand.kt
package winlyps.ignore.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import winlyps.ignore.storage.IgnoreStorage

class IgnoreCommand(private val storage: IgnoreStorage) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.size < 2) return false

        val target = sender.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage("Player not found or not online.")
            return true
        }

        when (args[0].toLowerCase()) {
            "add" -> {
                storage.addIgnore(sender.uniqueId, target.uniqueId)
                sender.sendMessage("${target.name} has been added to your ignore list.")
            }
            "remove" -> {
                storage.removeIgnore(sender.uniqueId, target.uniqueId)
                sender.sendMessage("${target.name} has been removed from your ignore list.")
            }
            else -> return false
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 2) {
            return sender.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[1], true) }
        }
        return emptyList()
    }
}