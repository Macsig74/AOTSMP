package main.shop.command;

import main.shop.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Shop plugin;

    public ReloadCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("macsigcore.reload")) {
            sender.sendMessage("§cPas la permission !");
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage("§aConfig rechargée !");
        return true;
    }
}