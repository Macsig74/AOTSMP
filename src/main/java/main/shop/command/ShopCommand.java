package main.shop.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import main.shop.gui.ShopMainGui;
import main.shop.Shop;

public class ShopCommand implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage(ChatColor.RED + "Seul les joeurs peuvent executer la commande");
            return true;
        }
        Player player = (Player) sender;

        ShopMainGui gui = new ShopMainGui(Shop.getInstance());
        gui.open(player);

        return  true;
    }
}
