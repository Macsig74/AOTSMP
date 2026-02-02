package fr.aotsmp.maintenance.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import fr.aotsmp.maintenance.MaintenanceQueuePlugin;
import net.kyori.adventure.text.Component;

public class ServerConnectListener {
    
    private final MaintenanceQueuePlugin plugin;

    public ServerConnectListener(MaintenanceQueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        var player = event.getPlayer();
        var targetServer = event.getResult().getServer();
        
        // Si pas de serveur cible, ignorer
        if (targetServer.isEmpty()) {
            return;
        }
        
        String smpServerName = plugin.getConfigManager().getSmpServer();
        String targetServerName = targetServer.get().getServerInfo().getName();
        
        // Si le joueur essaie de rejoindre le SMP pendant la maintenance
        if (plugin.getMaintenanceManager().isMaintenanceMode() 
            && targetServerName.equalsIgnoreCase(smpServerName)) {
            
            // Vérifier si le joueur a la permission de bypass
            if (!player.hasPermission("maintenance.bypass")) {
                // Bloquer la connexion
                String message = plugin.getConfigManager().getMessage("blocked-connection");
                
                // Rediriger vers le lobby
                var lobbyServer = plugin.getServer()
                    .getServer(plugin.getConfigManager().getLobbyServer());
                
                if (lobbyServer.isPresent()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.allowed(lobbyServer.get()));
                    player.sendMessage(Component.text(message));
                    
                    plugin.getLogger().info(player.getUsername() + 
                        " a tenté de rejoindre le SMP pendant la maintenance");
                } else {
                    // Si pas de lobby, déconnecter
                    player.disconnect(Component.text(message));
                }
            } else {
                plugin.getLogger().info(player.getUsername() + 
                    " a rejoint le SMP pendant la maintenance (bypass)");
            }
        }
    }
}
