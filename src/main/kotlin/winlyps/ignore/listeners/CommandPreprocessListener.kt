//4.File: CommandPreprocessListener.kt
package winlyps.ignore.listeners

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import winlyps.ignore.storage.IgnoreStorage

class CommandPreprocessListener(private val storage: IgnoreStorage, private val audiences: BukkitAudiences) : Listener {

    private val messagingCommands = setOf("/msg", "/tell", "/whisper", "/m", "/t", "/w")
    private val miniMessage = MiniMessage.miniMessage()

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val command = event.message.split(" ")
        if (command.size < 3) return

        val sender = event.player
        val commandPrefix = command[0].toLowerCase()

        // Check if the command is a messaging command
        if (messagingCommands.contains(commandPrefix)) {
            val recipientName = command[1]
            val recipient = sender.server.getPlayer(recipientName)

            if (recipient == null) {
                sender.sendMessage("Player not found or not online.")
                return
            }

            if (storage.isIgnored(recipient.uniqueId, sender.uniqueId)) {
                audiences.player(sender).sendMessage(miniMessage.deserialize("<red>You are ignored by ${recipient.name} and cannot send messages to them.</red>"))
                event.isCancelled = true
            }
        }
    }
}