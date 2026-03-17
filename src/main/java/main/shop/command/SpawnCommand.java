package main.shop.command;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SpawnCommand implements CommandExecutor {

    private final Shop plugin;

    public SpawnCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        int delay = plugin.getConfig().getInt("spawn.teleport-delay", 3);
        String soundCountdown = plugin.getConfig().getString("spawn.sound-countdown", "BLOCK_NOTE_BLOCK_PLING");
        String soundArrival = plugin.getConfig().getString("spawn.sound-arrival", "ENTITY_ENDERMAN_TELEPORT");

        for (int i = delay; i > 0; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§eTéléportation au spawn dans §6" + count + "§e..."));
                try {
                    p.playSound(p.getLocation(), Sound.valueOf(soundCountdown), 1f, 1f);
                } catch (IllegalArgumentException ignored) {}
            }, (delay - i) * 20L);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String worldName = plugin.getConfig().getString("spawn.world");
            double x = plugin.getConfig().getDouble("spawn.x");
            double y = plugin.getConfig().getDouble("spawn.y");
            double z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            p.teleport(loc);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§aBienvenue au spawn !"));
            try {
                p.playSound(p.getLocation(), Sound.valueOf(soundArrival), 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
            p.sendMessage("§aTéléporté au spawn !");
        }, delay * 20L);

        return true;
    }
}