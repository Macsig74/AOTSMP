package main.shop.command;

import main.shop.Shop;
import main.shop.gui.GuiBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SoutienCommand implements CommandExecutor {

    private final Shop plugin;

    public SoutienCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        String title = plugin.getConfig().getString("soutien.title", "§8Role soutien");
        Material material = Material.valueOf(plugin.getConfig().getString("soutien.material", "PAINTING"));
        String itemName = plugin.getConfig().getString("soutien.item-name", "§bRole soutien");
        List<String> lore = plugin.getConfig().getStringList("soutien.lore");

        new GuiBuilder.Builder(plugin)
                .title(title)
                .material(material)
                .itemName(itemName)
                .lore(lore)
                .build()
                .open(player);

        return true;
    }
}