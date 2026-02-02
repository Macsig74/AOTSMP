package fr.aotsmp.maintenance;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.aotsmp.maintenance.commands.MaintenanceCommand;
import fr.aotsmp.maintenance.config.ConfigManager;
import fr.aotsmp.maintenance.listeners.ServerConnectListener;
import fr.aotsmp.maintenance.manager.MaintenanceManager;
import fr.aotsmp.maintenance.manager.PriorityManager; // IMPORT AJOUTÉ
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "maintenance-queue",
        name = "MaintenanceQueue",
        version = "1.0.0",
        description = "Plugin de file d'attente pour maintenance avec système de rangs",
        authors = {"AotSMP"}
)
public class MaintenanceQueuePlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private ConfigManager configManager;
    private MaintenanceManager maintenanceManager;
    private PriorityManager priorityManager;

    @Inject
    public MaintenanceQueuePlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Démarrage de MaintenanceQueue...");

        // Charger la configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialiser le gestionnaire de priorité
        priorityManager = new PriorityManager(this);

        // Initialiser le gestionnaire de maintenance
        maintenanceManager = new MaintenanceManager(this);

        // Enregistrer les commandes
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("maintenance")
                        .aliases("maint", "mt")
                        .build(),
                new MaintenanceCommand(this)
        );

        // Enregistrer les listeners
        server.getEventManager().register(this, new ServerConnectListener(this));

        logger.info("MaintenanceQueue démarré avec succès !");
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }
}