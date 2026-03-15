package main.shop;

import com.earth2me.essentials.Essentials;
import main.shop.command.ShopCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Shop extends JavaPlugin {

    private static Shop instance;
    private Essentials ess;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Enregistre la commande /shop
        getCommand("shop").setExecutor(new ShopCommand());
        ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        getLogger().info("ShopPlugin activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("ShopPlugin désactivé !");
    }
    public Essentials getEssentials() {
        return ess;
    }

    public static Shop getInstance() {
        return instance;
    }
}