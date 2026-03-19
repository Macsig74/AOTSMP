package main.shop.gui;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiBuilder implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;
    private final String title;
    private final Material material;
    private final String itemName;
    private final List<String> lore;

    @Override
    public Inventory getInventory() { return inv; }

    // Constructeur
    private GuiBuilder(Builder builder) {
        this.plugin = builder.plugin;
        this.title = builder.title;
        this.material = builder.material;
        this.itemName = builder.itemName;
        this.lore = builder.lore;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        inv = Bukkit.createInventory(this, 27, title);

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Item au centre
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof GuiBuilder)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof GuiBuilder)) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    // Builder pattern
    public static class Builder {
        private final Shop plugin;
        private String title = "§8Menu";
        private Material material = Material.PAPER;
        private String itemName = "§fItem";
        private List<String> lore = List.of();

        public Builder(Shop plugin) {
            this.plugin = plugin;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public GuiBuilder build() {
            return new GuiBuilder(this);
        }
    }
}