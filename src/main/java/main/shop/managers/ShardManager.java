package main.shop.managers;

import main.shop.Shop;
import org.bukkit.entity.Player;

import java.sql.*;

public class ShardManager {
    private final Shop plugin;
    private Connection connection;


    public ShardManager(Shop plugin) {
        this.plugin = plugin;
    }


    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/shards.db");
    }

    public void createTable() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS shards (uuid VARCHAR(36) PRIMARY KEY, amount INT DEFAULT 0)"
        );
    }
    public int getShards(Player player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT amount FROM shards WHERE uuid = ?");
        ps.setString(1, player.getUniqueId().toString());
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("amount") : 0;
    }

    public void setShards(Player player, int amount) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO shards (uuid, amount) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET amount = ?"
        );
        ps.setString(1, player.getUniqueId().toString());
        ps.setInt(2, amount);
        ps.setInt(3, amount);
        ps.execute();
    }
}