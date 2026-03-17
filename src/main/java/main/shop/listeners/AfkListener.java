package main.shop.listeners;

import main.shop.Shop;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class AfkListener {

    private final Shop plugin;

    public AfkListener(Shop plugin) {
        this.plugin = plugin;

        AtomicInteger countdown = new AtomicInteger(60);

        // Countdown actionbar chaque seconde
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            String afkWorld = plugin.getConfig().getString("afk.world");
            if (afkWorld == null) return;

            int timeLeft = countdown.getAndDecrement();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equals(afkWorld)) continue;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§7Prochaine shard dans §5" + timeLeft + "s"));
            }

            // Reset le countdown et donne les shards
            if (timeLeft <= 0) {
                countdown.set(60);
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
            }
        }, 0L, 20L); // toutes les secondes
    }
}