//3.File: ChatListener.kt
package winlyps.ignore.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import winlyps.ignore.storage.IgnoreStorage

class ChatListener(private val storage: IgnoreStorage) : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val sender = event.player
        val recipients = event.recipients.toMutableSet()

        for (recipient in recipients) {
            if (storage.isIgnored(recipient.uniqueId, sender.uniqueId)) {
                recipients.remove(recipient)
            }
        }

        event.recipients.clear()
        event.recipients.addAll(recipients)
    }
}