package main.shop.command;

import main.shop.Shop;
import main.shop.gui.RtpGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCommand implements CommandExecutor {

    private final Shop plugin;

    public RtpCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }
        new RtpGui(plugin).open(p);
        return true;
    }
}