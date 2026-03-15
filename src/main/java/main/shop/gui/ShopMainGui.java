package main.shop.gui;

import io.papermc.paper.datacomponent.item.ItemLore;
import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;


public class ShopMainGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;

    @Override
    public Inventory getInventory() {
        return inv;
    }
    private void setCategory(int slot, Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        inv.setItem(slot, item);
    }

    public ShopMainGui(Shop plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    public void open(Player player){
        inv = Bukkit.createInventory(this, 27, "ʙᴏᴜᴛɪǫᴜᴇ");
        setCategory(11, Material.END_STONE,      "§fEnd",              "§7Items de l'End");
        setCategory(12, Material.NETHERRACK,     "§cNether",           "§7Items du Nether");
        setCategory(13, Material.TOTEM_OF_UNDYING,  "§bGear",             "§7Equipements & consommables");
        setCategory(14, Material.COOKED_BEEF,    "§6Nourriture",       "§7Comestibles & potions");
        setCategory(15, Material.AMETHYST_SHARD, "§5Shard Shop",       "§7Dépense tes shards");
        player.openInventory(inv);

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShopMainGui)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        if (slot == 11) {
            ShopEndGui EndGui = new ShopEndGui(plugin);
            EndGui.open(p);
        } else if (slot == 12) {
            ShopNetherGui NetherGui = new ShopNetherGui(plugin);
            NetherGui.open(p);
        } else if (slot == 13) {
            ShopGearGui GearGui = new ShopGearGui(plugin);
            GearGui.open(p);
        } else if (slot == 14) {
            ShopFoodGui FoodGui = new ShopFoodGui(plugin);
            FoodGui.open(p);
        } else if (slot == 15) {
            ShopShardGui ShardGui = new ShopShardGui(plugin);
            ShardGui.open(p);
        }
    }
}