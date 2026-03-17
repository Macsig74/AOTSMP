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

import java.sql.SQLException;
import java.util.Collections;

public class ShardBuyConfirmGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;
    private final Material material;
    private final String name;
    private final int pricePerUnit;
    private final String command;
    private int quantity;

    @Override
    public Inventory getInventory() { return inv; }

    public ShardBuyConfirmGui(Shop plugin, Material material, String name, int pricePerUnit, String command) {
        this.plugin = plugin;
        this.material = material;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.command = command;
        this.quantity = 1;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setItem(int slot, Material mat, String displayName, int amount) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void open(Player player) {
        inv = Bukkit.createInventory(this, 27, "§5Confirmer achat");

        // Item au centre
        ItemStack display = new ItemStack(material, quantity);
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList("§5Prix : " + (pricePerUnit * quantity) + " Shards"));
        display.setItemMeta(meta);
        inv.setItem(13, display);

        setItem(21, Material.RED_STAINED_GLASS_PANE,   "§cAnnuler",   1);
        setItem(23, Material.GREEN_STAINED_GLASS_PANE, "§aConfirmer", 1);

        player.openInventory(inv);
    }

    private void refreshGui(Player player) {
        ItemStack display = new ItemStack(material, quantity);
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(name + " §7x" + quantity);
        meta.setLore(Collections.singletonList("§5Prix total : " + (pricePerUnit * quantity) + " Shards"));
        display.setItemMeta(meta);
        inv.setItem(13, display);
        player.updateInventory();
    }

    private void changeQuantity(Player player, int delta) {
        quantity = Math.max(1, Math.min(64, quantity + delta));
        refreshGui(player);
    }

    private void handlePurchase(Player player) {
        try {
            int total = pricePerUnit * quantity;
            int shards = Shop.getInstance().getShardManager().getShards(player);

            if (shards < total) {
                player.sendMessage("§cPas assez de shards ! §5(" + shards + "/" + total + ")");
                return;
            }

            Shop.getInstance().getShardManager().setShards(player, shards - total);

            // Exécute la commande si définie, sinon donne l'item
            if (command != null && !command.isEmpty()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName()));
            } else {
                player.getInventory().addItem(new ItemStack(material, quantity));
            }

            player.sendMessage("§aAchat effectué ! §5(-" + total + " Shards)");

        } catch (SQLException e) {
            player.sendMessage("§cErreur base de données !");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShardBuyConfirmGui holder)) return;
        if (holder != this) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null) return;

        switch (meta.getDisplayName()) {
            case "§cAnnuler"  -> { p.closeInventory(); }
            case "§aConfirmer" -> { handlePurchase(p); p.closeInventory(); }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShardBuyConfirmGui holder)) return;
        if (holder != this) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}