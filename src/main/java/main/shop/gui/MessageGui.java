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

import java.util.Arrays;

public class MessageGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;
    private final Player player;

    @Override
    public Inventory getInventory() { return inv; }

    public MessageGui(Shop plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inv = Bukkit.createInventory(this, 27, "§8Mes Messages");

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        String grade = Shop.getInstance().getMessageManager().getGrade(player);
        if (grade == null) {
            player.sendMessage("§cVous n'avez pas de grade custom !");
            return;
        }

        setItem(11, Material.OAK_DOOR,    "§aMessage de Join", "§7Changer votre message d'arrivée");
        setItem(13, Material.BARRIER,     "§cMessage de Quit", "§7Changer votre message de départ");
        setItem(15, Material.DIAMOND_SWORD, "§6Message de Kill", "§7Changer votre message de kill");

        player.openInventory(inv);
    }

    private void setItem(int slot, Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore, "", "§eClique pour changer"));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof MessageGui holder)) return;
        if (holder != this) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;

        String grade = Shop.getInstance().getMessageManager().getGrade(player);
        if (grade == null) return;

        switch (e.getSlot()) {
            case 11 -> new MessageSelectorGui(plugin, player, grade, "join").open();
            case 13 -> new MessageSelectorGui(plugin, player, grade, "quit").open();
            case 15 -> new MessageSelectorGui(plugin, player, grade, "kill").open();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof MessageGui holder)) return;
        if (holder != this) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}