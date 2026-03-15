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
import java.util.Collections;

public class ShopFoodGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;

    @Override
    public Inventory getInventory() { return inv; }

    public ShopFoodGui(Shop plugin) {
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
        inv = Bukkit.createInventory(this, 27, "ʙᴏᴜᴛɪǫᴜᴇ - ꜰᴏᴏᴅ");
        setCategory(9,  Material.POTATO,        "§fPotato",         1);
        setCategory(10, Material.SWEET_BERRIES, "§fSweet Berrie",   1);
        setCategory(11, Material.MELON_SLICE,   "§fWatermelon",     1);
        setCategory(12, Material.CARROT,        "§fCarrot",         1);
        setCategory(13, Material.APPLE,         "§fApple",          1);
        setCategory(14, Material.COOKED_CHICKEN,"§fCooked Chicken", 1);
        setCategory(15, Material.COOKED_BEEF,   "§fSteak",          1);
        setCategory(16, Material.GOLDEN_CARROT, "§fGolden Carrot",  1);
        setCategory(17, Material.GOLDEN_APPLE,  "§fGolden Apple",   1);
        setBack(18);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShopFoodGui)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        switch (slot) {
            case 9  -> new BuyConfirmGui(plugin, Material.POTATO,         "§fPotato",         false).open(p);
            case 10 -> new BuyConfirmGui(plugin, Material.SWEET_BERRIES,  "§fSweet Berrie",   false).open(p);
            case 11 -> new BuyConfirmGui(plugin, Material.MELON_SLICE,    "§fWatermelon",     false).open(p);
            case 12 -> new BuyConfirmGui(plugin, Material.CARROT,         "§fCarrot",         false).open(p);
            case 13 -> new BuyConfirmGui(plugin, Material.APPLE,          "§fApple",          false).open(p);
            case 14 -> new BuyConfirmGui(plugin, Material.COOKED_CHICKEN, "§fCooked Chicken", false).open(p);
            case 15 -> new BuyConfirmGui(plugin, Material.COOKED_BEEF,    "§fSteak",          false).open(p);
            case 16 -> new BuyConfirmGui(plugin, Material.GOLDEN_CARROT,  "§fGolden Carrot",  false).open(p);
            case 17 -> new BuyConfirmGui(plugin, Material.GOLDEN_APPLE,   "§fGolden Apple",   false).open(p);
            case 18 -> new ShopMainGui(plugin).open(p);
        }
    }
}