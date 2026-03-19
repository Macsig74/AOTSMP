package main.shop.gui;

import com.earth2me.essentials.paperlib.PaperLib;
import main.shop.Shop;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Random;

public class RtpGui implements InventoryHolder, Listener {

    private final Shop plugin;
    private Inventory inv;

    @Override
    public Inventory getInventory() { return inv; }

    public RtpGui(Shop plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setItem(int slot, Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void open(Player player) {
        inv = Bukkit.createInventory(this, 27, "§8RTP - Choisir dimension");

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        setItem(11, Material.GRASS_BLOCK, "§aOverworld", "§7Téléportation aléatoire dans l'Overworld");
        setItem(13, Material.NETHERRACK,  "§cNether",    "§7Téléportation aléatoire dans le Nether");
        setItem(15, Material.END_STONE,   "§fEnd",       "§7Téléportation aléatoire dans l'End");

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof RtpGui)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();

        switch (e.getSlot()) {
            case 11 -> startRtp(p, "world",          10000);
            case 13 -> startRtp(p, "world_nether",   5000);
            case 15 -> startRtp(p, "world_the_end",  3000);
        }
    }

    private void startRtp(Player player, String worldName, int maxRange) {
        player.closeInventory();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cMonde introuvable : " + worldName);
            return;
        }


        for (int i = 5; i > 0; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent("§eTéléportation dans §6" + count + "§e...")),
                    (5 - i) * 20L);
        }


        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            Random random = new Random();
            int x = random.nextInt(maxRange * 2) - maxRange;
            int z = random.nextInt(maxRange * 2) - maxRange;

            PaperLib.getChunkAtAsync(world, x >> 4, z >> 4).thenAccept(chunk -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    int y = world.getHighestBlockYAt(x, z);

                    if (y <= world.getMinHeight() || y >= world.getMaxHeight() - 2) {
                        // Réessaie si zone invalide
                        startRtp(player, worldName, maxRange);
                        return;
                    }

                    Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                    player.teleport(loc);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§aTéléporté !"));
                    player.sendMessage("§aTéléporté en §6" + worldName + " §a(" +
                            x + ", " + y + ", " + z + ")");
                });
            });
        }, 100L);
    }


}