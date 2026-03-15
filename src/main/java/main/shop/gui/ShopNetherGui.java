package main.shop.gui;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class ShopNetherGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;

    @Override
    public Inventory getInventory() { return inv; }

    public ShopNetherGui(Shop plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setCategory(int slot, Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        BigDecimal price = Shop.getInstance().getEssentials().getWorth().getPrice(Shop.getInstance().getEssentials(), new ItemStack(material));
        String priceText = price != null ? "§aPrix : $" + price : "§cPrix inconnu";
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(priceText));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private void setBack(int slot) {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cArrière");
        meta.setLore(Collections.singletonList("§7Revenir en arrière"));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void open(Player player) {
        inv = Bukkit.createInventory(this, 27, "ʙᴏᴜᴛɪǫᴜᴇ - ɴᴇᴛʜᴇʀ");
        setCategory(9,  Material.BLAZE_ROD,          "§fBlaze Rod",       1);
        setCategory(10, Material.NETHER_WART,        "§fNether Wart",     1);
        setCategory(11, Material.GLOWSTONE_DUST,     "§fGlow Stone",      1);
        setCategory(12, Material.MAGMA_CREAM,        "§fMagma Cream",     1);
        setCategory(13, Material.GHAST_TEAR,         "§fGhast Tear",      1);
        setCategory(14, Material.NETHER_QUARTZ_ORE,  "§fNether Quartz",   1);
        setCategory(15, Material.SOUL_SAND,          "§fSoul Sand",       1);
        setCategory(16, Material.MAGMA_BLOCK,        "§fMagma Block",     1);
        setCategory(17, Material.CRYING_OBSIDIAN,    "§fCrying Obsidian", 1);
        setBack(18);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShopNetherGui)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        switch (slot) {
            case 9  -> new BuyConfirmGui(plugin, Material.BLAZE_ROD,         "§fBlaze Rod",       false).open(p);
            case 10 -> new BuyConfirmGui(plugin, Material.NETHER_WART,       "§fNether Wart",     false).open(p);
            case 11 -> new BuyConfirmGui(plugin, Material.GLOWSTONE_DUST,    "§fGlow Stone",      false).open(p);
            case 12 -> new BuyConfirmGui(plugin, Material.MAGMA_CREAM,       "§fMagma Cream",     false).open(p);
            case 13 -> new BuyConfirmGui(plugin, Material.GHAST_TEAR,        "§fGhast Tear",      false).open(p);
            case 14 -> new BuyConfirmGui(plugin, Material.NETHER_QUARTZ_ORE, "§fNether Quartz",   false).open(p);
            case 15 -> new BuyConfirmGui(plugin, Material.SOUL_SAND,         "§fSoul Sand",       false).open(p);
            case 16 -> new BuyConfirmGui(plugin, Material.MAGMA_BLOCK,       "§fMagma Block",     false).open(p);
            case 17 -> new BuyConfirmGui(plugin, Material.CRYING_OBSIDIAN,   "§fCrying Obsidian", false).open(p);
            case 18 -> new ShopMainGui(plugin).open(p);
        }

    }
}