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
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {

    public DiscordCommand(Shop shop) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        // Ligne déco haut
        player.sendMessage(ChatColor.of("#5865F2") + "══════════════════════════════════════");

        // Lien cliquable avec hover
        TextComponent prefix = new TextComponent("          ");

        TextComponent link = new TextComponent(ChatColor.of("#5865F2") + "» " +
                ChatColor.WHITE + "Rejoins notre Discord : " +
                ChatColor.of("#5865F2") + ChatColor.UNDERLINE + "discord.gg/aotsmp");

        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/aotsmp"));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.of("#5865F2") + "Clique pour rejoindre\n" +
                        ChatColor.GRAY + "discord.gg/aotsmp")));

        TextComponent message = new TextComponent(prefix);
        message.addExtra(link);
        player.spigot().sendMessage(message);

        // Ligne déco bas
        player.sendMessage(ChatColor.of("#5865F2") + "══════════════════════════════════════");

        // Son
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

        return true;
    }
}