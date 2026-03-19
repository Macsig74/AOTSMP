package main.shop.command;

import main.shop.Shop;
import main.shop.gui.GuiBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RulesCommand implements CommandExecutor {

    private final Shop plugin;

    public RulesCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        String title = plugin.getConfig().getString("rules.title", "§8Role rules");
        Material material = Material.valueOf(plugin.getConfig().getString("rules.material", "PAINTING"));
        String itemName = plugin.getConfig().getString("rules.item-name", "§bRole rules");
        List<String> lore = plugin.getConfig().getStringList("rules.lore");

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