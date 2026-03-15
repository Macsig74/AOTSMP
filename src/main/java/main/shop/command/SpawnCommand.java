package main.shop.command;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private Shop plugin;

    public SpawnCommand() {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        int delay = plugin.getConfig().getInt("spawn.teleport-delay", 3);
        p.sendMessage("§eTéléportation dans §6" + delay + " §esecondes...");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String worldName = plugin.getConfig().getString("spawn.world");
            double x = plugin.getConfig().getDouble("spawn.x");
            double y = plugin.getConfig().getDouble("spawn.y");
            double z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            p.teleport(loc);

            // Son configurable
            String soundName = plugin.getConfig().getString("spawn.sound", "ENTITY_ENDERMAN_TELEPORT");
            try {
                p.playSound(p.getLocation(), Sound.valueOf(soundName), 1f, 1f);
            } catch (IllegalArgumentException e) {
                // Son invalide dans la config, on ignore
            }

            p.sendMessage("§aTéléporté au spawn !");
        }, delay * 20L);

        return true;
    }
}