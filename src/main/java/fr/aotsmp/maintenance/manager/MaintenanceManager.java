package fr.aotsmp.maintenance.manager;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fr.aotsmp.maintenance.MaintenanceQueuePlugin;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MaintenanceManager {
    
    private final MaintenanceQueuePlugin plugin;
    private boolean maintenanceMode = false;
    private ScheduledTask currentTask = null;

    public MaintenanceManager(MaintenanceQueuePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    /**
     * Active la maintenance et kick progressivement les joueurs du SMP vers le lobby
     */
    public void enableMaintenance() {
        if (maintenanceMode) {
            return;
        }
        
        maintenanceMode = true;
        plugin.getLogger().info("Maintenance activée !");
        
        // Broadcast aux joueurs
        String message = plugin.getConfigManager().getMessage("maintenance-enabled");
        plugin.getServer().getAllPlayers().forEach(p -> 
            p.sendMessage(Component.text(message))
        );
        
        // Récupérer tous les joueurs du SMP
        RegisteredServer smpServer = plugin.getServer()
            .getServer(plugin.getConfigManager().getSmpServer())
            .orElse(null);
        
        if (smpServer == null) {
            plugin.getLogger().error("Serveur SMP introuvable !");
            return;
        }
        
        List<Player> smpPlayers = new ArrayList<>(smpServer.getPlayersConnected());
        
        if (smpPlayers.isEmpty()) {
            plugin.getLogger().info("Aucun joueur sur le SMP.");
            return;
        }
        
        // Trier : joueurs prioritaires en dernier (kick en dernier)
        smpPlayers.sort(Comparator.comparing(p -> 
            plugin.getPriorityManager().hasPriority(p)
        ));
        
        plugin.getLogger().info("Kick progressif de " + smpPlayers.size() + " joueurs...");
        
        // Kick progressif
        kickPlayersProgressively(smpPlayers);
    }

    /**
     * Kick les joueurs progressivement vers le lobby
     */
    private void kickPlayersProgressively(List<Player> players) {
        RegisteredServer lobbyServer = plugin.getServer()
            .getServer(plugin.getConfigManager().getLobbyServer())
            .orElse(null);
        
        if (lobbyServer == null) {
            plugin.getLogger().error("Serveur Lobby introuvable !");
            return;
        }
        
        final int[] index = {0};
        int delaySeconds = plugin.getConfigManager().getKickDelay();
        
        currentTask = plugin.getServer().getScheduler()
            .buildTask(plugin, () -> {
                if (index[0] >= players.size()) {
                    currentTask.cancel();
                    checkIfSmpEmpty();
                    return;
                }
                
                Player player = players.get(index[0]);
                
                if (player.isActive()) {
                    String kickMessage = plugin.getConfigManager().getMessage("kick-to-lobby");
                    player.sendMessage(Component.text(kickMessage));
                    
                    player.createConnectionRequest(lobbyServer).fireAndForget();
                    
                    boolean isPriority = plugin.getPriorityManager().hasPriority(player);
                    plugin.getLogger().info("Transféré: " + player.getUsername() + 
                        (isPriority ? " [PRIORITAIRE]" : ""));
                }
                
                index[0]++;
            })
            .repeat(delaySeconds, TimeUnit.SECONDS)
            .schedule();
    }

    /**
     * Vérifie si le SMP est vide et lance le restart si nécessaire
     */
    private void checkIfSmpEmpty() {
        RegisteredServer smpServer = plugin.getServer()
            .getServer(plugin.getConfigManager().getSmpServer())
            .orElse(null);
        
        if (smpServer == null) return;
        
        if (smpServer.getPlayersConnected().isEmpty()) {
            plugin.getLogger().info("SMP vide !");
            
            String message = plugin.getConfigManager().getMessage("smp-empty");
            plugin.getServer().getAllPlayers().forEach(p -> 
                p.sendMessage(Component.text(message))
            );
            
            if (plugin.getConfigManager().isAutoRestart()) {
                // TODO: Implémenter le restart du serveur SMP
                plugin.getLogger().info("Restart automatique du SMP...");
            }
        }
    }

    /**
     * Désactive la maintenance et reconnecte progressivement les joueurs
     */
    public void disableMaintenance() {
        if (!maintenanceMode) {
            return;
        }
        
        maintenanceMode = false;
        plugin.getLogger().info("Maintenance désactivée !");
        
        // Broadcast aux joueurs
        String message = plugin.getConfigManager().getMessage("maintenance-disabled");
        plugin.getServer().getAllPlayers().forEach(p -> 
            p.sendMessage(Component.text(message))
        );
        
        // Récupérer tous les joueurs du lobby
        RegisteredServer lobbyServer = plugin.getServer()
            .getServer(plugin.getConfigManager().getLobbyServer())
            .orElse(null);
        
        if (lobbyServer == null) {
            plugin.getLogger().error("Serveur Lobby introuvable !");
            return;
        }
        
        List<Player> lobbyPlayers = new ArrayList<>(lobbyServer.getPlayersConnected());
        
        if (lobbyPlayers.isEmpty()) {
            plugin.getLogger().info("Aucun joueur au lobby.");
            return;
        }
        
        // Trier : joueurs prioritaires en premier (reconnexion en premier)
        lobbyPlayers.sort(Comparator.comparing(p -> 
            !plugin.getPriorityManager().hasPriority(p)
        ));
        
        plugin.getLogger().info("Reconnexion progressive de " + lobbyPlayers.size() + " joueurs...");
        
        // Reconnexion progressive
        reconnectPlayersProgressively(lobbyPlayers);
    }

    /**
     * Reconnecte les joueurs progressivement au SMP
     */
    private void reconnectPlayersProgressively(List<Player> players) {
        RegisteredServer smpServer = plugin.getServer()
            .getServer(plugin.getConfigManager().getSmpServer())
            .orElse(null);
        
        if (smpServer == null) {
            plugin.getLogger().error("Serveur SMP introuvable !");
            return;
        }
        
        final int totalPlayers = players.size();
        final int[] index = {0};
        int delaySeconds = plugin.getConfigManager().getReconnectDelay();
        
        currentTask = plugin.getServer().getScheduler()
            .buildTask(plugin, () -> {
                if (index[0] >= players.size()) {
                    currentTask.cancel();
                    plugin.getLogger().info("Reconnexion terminée !");
                    return;
                }
                
                Player player = players.get(index[0]);
                
                if (player.isActive()) {
                    int position = index[0] + 1;
                    
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("position", String.valueOf(position));
                    placeholders.put("total", String.valueOf(totalPlayers));
                    
                    String reconnectMessage = plugin.getConfigManager()
                        .getMessage("reconnect-to-smp", placeholders);
                    player.sendMessage(Component.text(reconnectMessage));
                    
                    player.createConnectionRequest(smpServer).fireAndForget();
                    
                    boolean isPriority = plugin.getPriorityManager().hasPriority(player);
                    plugin.getLogger().info("Reconnecté: " + player.getUsername() + 
                        (isPriority ? " [PRIORITAIRE]" : "") + " - " + position + "/" + totalPlayers);
                }
                
                index[0]++;
            })
            .repeat(delaySeconds, TimeUnit.SECONDS)
            .schedule();
    }

    /**
     * Annule la tâche en cours si elle existe
     */
    /**
     * Annule la tâche en cours si elle existe
     */
    public void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }
}
