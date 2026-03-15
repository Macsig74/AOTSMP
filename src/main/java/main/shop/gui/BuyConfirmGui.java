package main.shop.gui;

import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import main.shop.Shop;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;


public class BuyConfirmGui implements InventoryHolder, Listener {
    private final Shop plugin;
    private Inventory inv;
    private final Material material;
    private final String name;
    private int quantity;
    private final boolean simpleMode;

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public BuyConfirmGui(Shop plugin, Material material, String name, boolean simpleMode) {
        this.plugin = plugin;
        this.material = material;
        this.name = name;
        this.simpleMode = simpleMode;
        this.quantity = 1;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setItem(int slot, Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void open(Player player) {
        inv = Bukkit.createInventory(this, 27, "Achat de " + material.name());
        setItem(13, material, material.name(), quantity);
        setItem(21, Material.RED_STAINED_GLASS_PANE, "Annuler", 1);
        if (simpleMode) {
            setItem(23, Material.GREEN_STAINED_GLASS_PANE, "Confirmer", 1);
        } else {
            setItem(23, Material.GREEN_STAINED_GLASS_PANE, "Confirmer", 1);
            setItem(11, Material.RED_STAINED_GLASS_PANE, "-1", 1);
            setItem(10, Material.RED_STAINED_GLASS_PANE, "-10", 1);
            setItem(9,  Material.RED_STAINED_GLASS_PANE, "-64", 1);
            setItem(15, Material.GREEN_STAINED_GLASS_PANE, "+1", 1);
            setItem(16, Material.GREEN_STAINED_GLASS_PANE, "+10", 1);
            setItem(17, Material.GREEN_STAINED_GLASS_PANE, "+64", 1);
        }
        player.openInventory(inv);
    }

    private void refreshGui(Player player) {
        setItem(13, material,material.name() + " x" + quantity, quantity);
        player.updateInventory();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BuyConfirmGui holder)) return;
        if (holder != this) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        if (meta == null) return;
        String itemName = meta.getDisplayName();

        switch (itemName) {
            case "Annuler" -> {
                player.closeInventory();
                player.sendMessage("Achat annulé.");
            }
            case "Confirmer" -> {
                handlePurchase(player);
            }
            case "-1"  -> changeQuantity(player, -1);
            case "-10" -> changeQuantity(player, -10);
            case "-64" -> changeQuantity(player, -64);
            case "+1"  -> changeQuantity(player, 1);
            case "+10" -> changeQuantity(player, 10);
            case "+64" -> changeQuantity(player, 64);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BuyConfirmGui holder)) return;
        if (holder != this) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    private void changeQuantity(Player player, int delta) {
        quantity = Math.max(1, Math.min(64, quantity + delta));
        refreshGui(player);
    }

    private void handlePurchase(Player player) {
        BigDecimal price = Shop.getInstance().getEssentials().getWorth().getPrice(Shop.getInstance().getEssentials(), new ItemStack(material));
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        User user = Shop.getInstance().getEssentials().getUser(player);


        if (user.getMoney().compareTo(total) >= 0){
            try {
                user.setMoney(user.getMoney().subtract(total));
            } catch (MaxMoneyException e) {
                throw new RuntimeException(e);
            }
            player.getInventory().addItem(new ItemStack(material, quantity));
        } else {
            player.sendMessage("Tu n'as pas assez d'argent");
        }



    }
}