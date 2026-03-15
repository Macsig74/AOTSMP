package main.shop.listeners;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class AfkListener {

    private final Shop plugin;

    public AfkListener(Shop plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            String afkWorld = plugin.getConfig().getString("afk.world");
            if (afkWorld == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equals(afkWorld)) continue;

                try {
                    int shards = Shop.getInstance().getShardManager().getShards(player);
                    Shop.getInstance().getShardManager().setShards(player, shards + 1);
                    player.sendMessage("§5+1 Shard §7(AFK)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0L, 1200L); // 1200 ticks = 1 minute
    }
}