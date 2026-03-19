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
import java.util.Arrays;
import java.util.List;

public class MessageSelectorGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;
    private final Player player;
    private final String grade;
    private final String type;

    @Override
    public Inventory getInventory() { return inv; }

    public MessageSelectorGui(Shop plugin, Player player, String grade, String type) {
        this.plugin = plugin;
        this.player = player;
        this.grade = grade;
        this.type = type;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inv = Bukkit.createInventory(this, 27, "§8Messages - " + type);

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Charge les messages depuis la config
        List<String> messages = plugin.getMessagesConfig()
                .getStringList("custom-messages." + grade + "." + type);

        // Place chaque message dans un slot
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        for (int i = 0; i < messages.size() && i < slots.length; i++) {
            String msg = messages.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§aOption " + (i + 1));
            meta.setLore(Arrays.asList(
                    msg.replace("%player%", player.getName())
                            .replace("%victim%", "§cVictime"),
                    "",
                    "§eClique pour sélectionner"
            ));
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        // Bouton retour
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cRetour");
        back.setItemMeta(backMeta);
        inv.setItem(18, back);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof MessageSelectorGui holder)) return;
        if (holder != this) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null) return;

        if (meta.getDisplayName().equals("§cRetour")) {
            new MessageGui(plugin, player).open();
            return;
        }

        if (!meta.getDisplayName().startsWith("§aOption")) return;

        // Récupère le message depuis le lore
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;
        String selected = lore.get(0);

        // Récupère le vrai message depuis la config (pas le preview)
        List<String> messages = plugin.getMessagesConfig()
                .getStringList("custom-messages." + grade + "." + type);
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        int slotIndex = -1;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == e.getSlot()) { slotIndex = i; break; }
        }
        if (slotIndex < 0 || slotIndex >= messages.size()) return;
        String realMessage = messages.get(slotIndex);

        try {
            switch (type) {
                case "join" -> Shop.getInstance().getMessageManager().setJoinMessage(player, realMessage);
                case "quit" -> Shop.getInstance().getMessageManager().setQuitMessage(player, realMessage);
                case "kill" -> Shop.getInstance().getMessageManager().setKillMessage(player, realMessage);
            }
            player.sendMessage("§aMessage §e" + type + " §achangé !");
            player.closeInventory();
        } catch (SQLException ex) {
            player.sendMessage("§cErreur base de données !");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof MessageSelectorGui holder)) return;
        if (holder != this) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}