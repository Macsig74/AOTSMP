package main.shop;

import com.earth2me.essentials.Essentials;
import main.shop.command.*;
import main.shop.listeners.AfkListener;
import main.shop.managers.MessageManager;
import main.shop.managers.ShardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import main.shop.listeners.MessageListener;

import java.io.File;
import java.sql.SQLException;

public class Shop extends JavaPlugin {

    private static Shop instance;
    private Essentials ess;
    private ShardManager shardManager;
    private FileConfiguration messagesConfig;
    private MessageManager messageManager;

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
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("media").setExecutor(new MediaCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));
        getCommand("soutien").setExecutor(new SoutienCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("store").setExecutor(new StoreCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        new AfkListener(this);


        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) saveResource("messages.yml", false);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        messageManager = new MessageManager(this);
        try {
            messageManager.connect();
            messageManager.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new MessageListener(this);

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
    public FileConfiguration getMessagesConfig() {return messagesConfig;}
    public MessageManager getMessageManager() {return messageManager;}
}