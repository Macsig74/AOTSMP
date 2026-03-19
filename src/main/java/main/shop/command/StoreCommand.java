package main.shop.command;

import main.shop.Shop;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class StoreCommand implements CommandExecutor {

    public StoreCommand(Shop shop) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        // Ligne déco haut
        player.sendMessage(ChatColor.GOLD + "══════════════════════════════════════");

        // Lien cliquable avec hover
        TextComponent prefix = new TextComponent("          ");

        TextComponent link = new TextComponent(ChatColor.GOLD + "» " +
                ChatColor.WHITE + "Visite notre store : " +
                ChatColor.GOLD + ChatColor.UNDERLINE + "store.aotsmp.net");

        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.aotsmp.net"));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.GOLD + "Clique pour visiter le store\n" +
                        ChatColor.GRAY + "store.aotsmp.net")));

        TextComponent message = new TextComponent(prefix);
        message.addExtra(link);
        player.spigot().sendMessage(message);

        // Ligne déco bas
        player.sendMessage(ChatColor.GOLD + "══════════════════════════════════════");

        // Son
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

        return true;
    }
}