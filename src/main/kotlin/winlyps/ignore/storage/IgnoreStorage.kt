//5.File: IgnoreStorage.kt
//5.File: IgnoreStorage.kt
package winlyps.ignore.storage

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

class IgnoreStorage(private val plugin: JavaPlugin) {

    private val databaseFile = File(plugin.dataFolder, "ignorelist.db")
    private var connection: Connection? = null

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        // Ensure the plugin's data folder exists
        plugin.dataFolder.mkdirs()

        // Initialize the database connection
        connection = DriverManager.getConnection("jdbc:sqlite:${databaseFile.absolutePath}")
        connection?.createStatement()?.executeUpdate("CREATE TABLE IF NOT EXISTS ignorelist (player TEXT, target TEXT)")
        connection?.createStatement()?.executeUpdate("CREATE TABLE IF NOT EXISTS last_sender (player TEXT PRIMARY KEY, sender TEXT)")
    }

    fun addIgnore(player: UUID, target: UUID) {
        val statement = connection?.prepareStatement("INSERT INTO ignorelist (player, target) VALUES (?, ?)")
        statement?.setString(1, player.toString())
        statement?.setString(2, target.toString())
        statement?.executeUpdate()
    }

    fun removeIgnore(player: UUID, target: UUID) {
        val statement = connection?.prepareStatement("DELETE FROM ignorelist WHERE player = ? AND target = ?")
        statement?.setString(1, player.toString())
        statement?.setString(2, target.toString())
        statement?.executeUpdate()
    }

    fun isIgnored(player: UUID, target: UUID): Boolean {
        val statement = connection?.prepareStatement("SELECT * FROM ignorelist WHERE player = ? AND target = ?")
        statement?.setString(1, player.toString())
        statement?.setString(2, target.toString())
        val resultSet = statement?.executeQuery()
        return resultSet?.next() ?: false
    }

    fun getIgnoredPlayers(player: UUID): Set<UUID> {
        val statement = connection?.prepareStatement("SELECT target FROM ignorelist WHERE player = ?")
        statement?.setString(1, player.toString())
        val resultSet = statement?.executeQuery()
        val ignoredPlayers = mutableSetOf<UUID>()
        while (resultSet?.next() == true) {
            ignoredPlayers.add(UUID.fromString(resultSet.getString("target")))
        }
        return ignoredPlayers
    }

    fun getIgnoredPlayerNames(player: UUID): List<String> {
        return getIgnoredPlayers(player).map { uuid ->
            plugin.server.getOfflinePlayer(uuid).name ?: "Unknown"
        }
    }

    fun setLastSender(player: UUID, sender: UUID) {
        plugin.logger.info("Setting last sender for player ${player} to ${sender}")
        val statement = connection?.prepareStatement("REPLACE INTO last_sender (player, sender) VALUES (?, ?)")
        statement?.setString(1, player.toString())
        statement?.setString(2, sender.toString())
        statement?.executeUpdate()
    }

    fun getLastSender(player: UUID): UUID? {
        plugin.logger.info("Getting last sender for player ${player}")
        val statement = connection?.prepareStatement("SELECT sender FROM last_sender WHERE player = ?")
        statement?.setString(1, player.toString())
        val resultSet = statement?.executeQuery()
        return if (resultSet?.next() == true) UUID.fromString(resultSet.getString("sender")) else null
    }

    fun close() {
        connection?.close()
    }
}