package fr.aotsmp.maintenance.config;

import fr.aotsmp.maintenance.MaintenanceQueuePlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final MaintenanceQueuePlugin plugin;
    private Map<String, Object> config;

    public ConfigManager(MaintenanceQueuePlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        Path configPath = plugin.getDataDirectory().resolve("config.yml");
        
        // Créer le dossier de données s'il n'existe pas
        try {
            if (!Files.exists(plugin.getDataDirectory())) {
                Files.createDirectories(plugin.getDataDirectory());
            }
            
            // Copier le config.yml par défaut s'il n'existe pas
            if (!Files.exists(configPath)) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                        plugin.getLogger().info("Fichier config.yml créé avec succès !");
                    }
                }
            }
            
            // Charger le fichier de configuration
            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(configPath)) {
                config = yaml.load(in);
            }
            
            plugin.getLogger().info("Configuration chargée avec succès !");
            
        } catch (IOException e) {
            plugin.getLogger().error("Erreur lors du chargement de la configuration", e);
            config = new HashMap<>();
        }
    }

    public String getLobbyServer() {
        return getString("servers.lobby", "lobby");
    }

    public String getSmpServer() {
        return getString("servers.smp", "smp");
    }

    public int getKickDelay() {
        return getInt("maintenance.kick-delay", 1);
    }

    public int getReconnectDelay() {
        return getInt("maintenance.reconnect-delay", 1);
    }

    public boolean isAutoRestart() {
        return getBoolean("maintenance.auto-restart", true);
    }

    public String getRestartCommand() {
        return getString("maintenance.restart-command", "restart");
    }

    public String getMessage(String key) {
        return getString("messages." + key, "§cMessage manquant: " + key);
    }
    
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    @SuppressWarnings("unchecked")
    private String getString(String path, String defaultValue) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = config;
        
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                return defaultValue;
            }
        }
        
        Object value = current.get(keys[keys.length - 1]);
        return value != null ? value.toString() : defaultValue;
    }

    @SuppressWarnings("unchecked")
    private int getInt(String path, int defaultValue) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = config;
        
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                return defaultValue;
            }
        }
        
        Object value = current.get(keys[keys.length - 1]);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private boolean getBoolean(String path, boolean defaultValue) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = config;
        
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                return defaultValue;
            }
        }
        
        Object value = current.get(keys[keys.length - 1]);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}
