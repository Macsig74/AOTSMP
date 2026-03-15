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

public class ShopShardGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;

    @Override
    public Inventory getInventory() { return inv; }

    public ShopShardGui(Shop plugin) {
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
        inv = Bukkit.createInventory(this, 27, "ʙᴏᴜᴛɪǫᴜᴇ - ѕʜᴀʀᴅ");
        setBack(18);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShopShardGui)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        if (slot == 18) {
            new ShopMainGui(plugin).open(p);
        }
    }
}