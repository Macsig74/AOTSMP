package fr.aotsmp.maintenance.manager;

import com.velocitypowered.api.proxy.Player;
import fr.aotsmp.maintenance.MaintenanceQueuePlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PriorityManager {
    
    private final MaintenanceQueuePlugin plugin;
    private final Set<UUID> priorityPlayers;
    private final Path priorityFile;

    public PriorityManager(MaintenanceQueuePlugin plugin) {
        this.plugin = plugin;
        this.priorityPlayers = new HashSet<>();
        this.priorityFile = plugin.getDataDirectory().resolve("priority.yml");
        loadPriorityList();
    }

    /**
     * Charge la liste de priorité depuis le fichier
     */
    private void loadPriorityList() {
        if (!Files.exists(priorityFile)) {
            plugin.getLogger().info("Fichier priority.yml non trouvé, création...");
            savePriorityList();
            return;
        }

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(Files.newInputStream(priorityFile));
            
            if (data != null && data.containsKey("priority-players")) {
                @SuppressWarnings("unchecked")
                List<String> uuids = (List<String>) data.get("priority-players");
                
                for (String uuidStr : uuids) {
                    try {
                        priorityPlayers.add(UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warn("UUID invalide ignoré: " + uuidStr);
                    }
                }
                
                plugin.getLogger().info("Liste de priorité chargée: " + priorityPlayers.size() + " joueur(s)");
            }
        } catch (IOException e) {
            plugin.getLogger().error("Erreur lors du chargement de priority.yml", e);
        }
    }

    /**
     * Sauvegarde la liste de priorité dans le fichier
     */
    public void savePriorityList() {
        try {
            Map<String, Object> data = new HashMap<>();
            List<String> uuids = new ArrayList<>();
            
            for (UUID uuid : priorityPlayers) {
                uuids.add(uuid.toString());
            }
            
            data.put("priority-players", uuids);
            
            Yaml yaml = new Yaml();
            Files.writeString(priorityFile, yaml.dump(data));
            
        } catch (IOException e) {
            plugin.getLogger().error("Erreur lors de la sauvegarde de priority.yml", e);
        }
    }

    /**
     * Ajoute un joueur à la liste prioritaire
     */
    public boolean addPriority(UUID playerUuid) {
        boolean added = priorityPlayers.add(playerUuid);
        if (added) {
            savePriorityList();
        }
        return added;
    }

    /**
     * Retire un joueur de la liste prioritaire
     */
    public boolean removePriority(UUID playerUuid) {
        boolean removed = priorityPlayers.remove(playerUuid);
        if (removed) {
            savePriorityList();
        }
        return removed;
    }

    /**
     * Vérifie si un joueur est dans la liste prioritaire
     */
    public boolean hasPriority(UUID playerUuid) {
        return priorityPlayers.contains(playerUuid);
    }

    /**
     * Vérifie si un joueur est dans la liste prioritaire
     */
    public boolean hasPriority(Player player) {
        return hasPriority(player.getUniqueId());
    }

    /**
     * Récupère tous les UUIDs de la liste prioritaire
     */
    public Set<UUID> getPriorityPlayers() {
        return new HashSet<>(priorityPlayers);
    }

    /**
     * Récupère les noms des joueurs prioritaires actuellement connectés
     */
    public List<String> getPriorityPlayerNames() {
        List<String> names = new ArrayList<>();
        
        for (UUID uuid : priorityPlayers) {
            plugin.getServer().getPlayer(uuid).ifPresent(player -> 
                names.add(player.getUsername())
            );
        }
        
        return names;
    }

    /**
     * Vide la liste de priorité
     */
    public void clearPriorityList() {
        priorityPlayers.clear();
        savePriorityList();
    }

    /**
     * Obtient le nombre de joueurs prioritaires
     */
    public int getPriorityCount() {
        return priorityPlayers.size();
    }
}
