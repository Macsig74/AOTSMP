package main.shop.command;

import main.shop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private Shop plugin;

    public SetSpawnCommand() {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED + "Commande pour joueur");
            return true;
        }
        plugin.getConfig().set("spawn.world",  player.getWorld().getName());
        plugin.getConfig().set("spawn.x",  player.getLocation().getX());
        plugin.getConfig().set("spawn.y",  player.getLocation().getY());
        plugin.getConfig().set("spawn.z",  player.getLocation().getZ());
        plugin.getConfig().set("spawn.yaw",  player.getLocation().getYaw());
        plugin.getConfig().set("spawn.pitch",  player.getLocation().getPitch());
        plugin.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Spawn configuré");
        return true;

    }
}
