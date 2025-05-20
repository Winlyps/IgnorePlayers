package winlyps.ignore.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import winlyps.ignore.storage.IgnoreStorage

class ChatListener(private val storage: IgnoreStorage) : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val sender = event.player

        // Build a list of recipients to remove FIRST, then remove them outside the loop
        val toRemove = mutableListOf<org.bukkit.entity.Player>()
        for (recipient in event.recipients) {
            if (storage.isIgnored(recipient.uniqueId, sender.uniqueId)) {
                toRemove.add(recipient)
            }
        }

        event.recipients.removeAll(toRemove)
    }
}
