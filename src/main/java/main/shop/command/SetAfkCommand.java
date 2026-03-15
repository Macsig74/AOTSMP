package main.shop.command;

import main.shop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAfkCommand implements CommandExecutor {

    private Shop plugin;


    public SetAfkCommand(Shop plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED + "Commande pour joueur");
            return true;
        }
        plugin.getConfig().set("afk.world",  player.getWorld().getName());
        plugin.getConfig().set("afk.x",  player.getLocation().getX());
        plugin.getConfig().set("afk.y",  player.getLocation().getY());
        plugin.getConfig().set("afk.z",  player.getLocation().getZ());
        plugin.getConfig().set("afk.yaw",  player.getLocation().getYaw());
        plugin.getConfig().set("afk.pitch",  player.getLocation().getPitch());
        plugin.saveConfig();
        player.sendMessage(ChatColor.GREEN + "AFK configuré");
        return true;

    }
}
