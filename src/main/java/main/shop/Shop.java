package main.shop;

import com.earth2me.essentials.Essentials;
import main.shop.command.*;
import main.shop.listeners.AfkListener;
import main.shop.managers.ShardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Shop extends JavaPlugin {

    private static Shop instance;
    private Essentials ess;
    private ShardManager shardManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("setafk").setExecutor(new SetAfkCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("afk").setExecutor(new AfkCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("shard").setExecutor(new ShardCommand(this));
        getCommand("macsigcore").setExecutor(new ReloadCommand(this));
        new AfkListener(this);

        ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        shardManager = new ShardManager(this);
        try {
            shardManager.connect();
            shardManager.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("ShopPlugin activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("ShopPlugin désactivé !");
    }
    public Essentials getEssentials() {
        return ess;
    }
    public ShardManager getShardManager() {return shardManager;}
    public static Shop getInstance() {
        return instance;
    }
}