package main.shop.managers;

import main.shop.Shop;
import org.bukkit.entity.Player;

import java.sql.*;

public class MessageManager {

    private final Shop plugin;
    private Connection connection;

    public MessageManager(Shop plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/messages.db");
    }

    public void createTable() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS messages (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "join_message TEXT," +
                        "quit_message TEXT," +
                        "kill_message TEXT)"
        );
    }

    public String getJoinMessage(Player player) throws SQLException {
        return getMessage(player, "join_message");
    }

    public String getQuitMessage(Player player) throws SQLException {
        return getMessage(player, "quit_message");
    }

    public String getKillMessage(Player player) throws SQLException {
        return getMessage(player, "kill_message");
    }

    private String getMessage(Player player, String column) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT " + column + " FROM messages WHERE uuid = ?");
        ps.setString(1, player.getUniqueId().toString());
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getString(column) : null;
    }

    public void setJoinMessage(Player player, String message) throws SQLException {
        setMessage(player, "join_message", message);
    }

    public void setQuitMessage(Player player, String message) throws SQLException {
        setMessage(player, "quit_message", message);
    }

    public void setKillMessage(Player player, String message) throws SQLException {
        setMessage(player, "kill_message", message);
    }

    private void setMessage(Player player, String column, String message) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO messages (uuid, " + column + ") VALUES (?, ?) " +
                        "ON CONFLICT(uuid) DO UPDATE SET " + column + " = ?");
        ps.setString(1, player.getUniqueId().toString());
        ps.setString(2, message);
        ps.setString(3, message);
        ps.execute();
    }

    // Retourne le grade du joueur (le plus haut)
    public String getGrade(Player player) {
        if (player.hasPermission("aot.owner"))    return "owner";
        if (player.hasPermission("aot.staff"))    return "staff";
        if (player.hasPermission("aot.plusplus")) return "aotplusplus";
        if (player.hasPermission("aot.plus"))     return "aotplus";
        return null; // pas de grade custom
    }

    // Retourne le 1er message par défaut d'un type pour un grade
    public String getDefaultMessage(String grade, String type) {
        java.util.List<String> messages = plugin.getMessagesConfig()
                .getStringList("custom-messages." + grade + "." + type);
        return messages.isEmpty() ? null : messages.get(0);
    }
}