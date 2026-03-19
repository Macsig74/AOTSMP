package main.shop.command;

import main.shop.Shop;
import main.shop.gui.MessageGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {

    private final Shop plugin;

    public MessageCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        String grade = Shop.getInstance().getMessageManager().getGrade(player);
        if (grade == null) {
            player.sendMessage("§cVous n'avez pas de grade custom !");
            return true;
        }

        new MessageGui(plugin, player).open();
        return true;
    }
}