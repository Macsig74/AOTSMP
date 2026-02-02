package fr.aotsmp.maintenance.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;
import fr.aotsmp.maintenance.MaintenanceQueuePlugin;
import fr.aotsmp.maintenance.manager.PriorityManager;   // IMPORT AJOUTÉ
import fr.aotsmp.maintenance.manager.MaintenanceManager; // IMPORT AJOUTÉ
import fr.aotsmp.maintenance.config.ConfigManager;      // IMPORT AJOUTÉ
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

public class MaintenanceCommand implements SimpleCommand {

    private final MaintenanceQueuePlugin plugin;
    // Note: Ton ConfigManager utilise "§", on utilise donc legacySection au lieu de legacyAmpersand
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

    public MaintenanceCommand(MaintenanceQueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sendHelp(source);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on", "enable" -> handleEnable(source);
            case "off", "disable" -> handleDisable(source);
            case "status" -> handleStatus(source);
            case "reload" -> handleReload(source);
            case "priority" -> handlePriority(source, args);
            default -> sendHelp(source);
        }
    }

    private void handleEnable(CommandSource source) {
        if (plugin.getMaintenanceManager().isMaintenanceMode()) {
            source.sendMessage(SERIALIZER.deserialize(plugin.getConfigManager().getMessage("already-in-maintenance")));
            return;
        }
        plugin.getMaintenanceManager().enableMaintenance();
        source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §aMaintenance activée !"));
    }

    private void handleDisable(CommandSource source) {
        if (!plugin.getMaintenanceManager().isMaintenanceMode()) {
            source.sendMessage(SERIALIZER.deserialize(plugin.getConfigManager().getMessage("not-in-maintenance")));
            return;
        }
        plugin.getMaintenanceManager().disableMaintenance();
        source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §aMaintenance désactivée !"));
    }

    private void handleStatus(CommandSource source) {
        boolean isEnabled = plugin.getMaintenanceManager().isMaintenanceMode();
        source.sendMessage(Component.text("=== MAINTENANCE STATUS ===", NamedTextColor.YELLOW));
        source.sendMessage(SERIALIZER.deserialize("§7Mode: " + (isEnabled ? "§c§lACTIVÉ" : "§a§lDÉSACTIVÉ")));

        plugin.getServer().getServer(plugin.getConfigManager().getSmpServer()).ifPresent(server ->
                source.sendMessage(SERIALIZER.deserialize("§7Joueurs SMP: §e" + server.getPlayersConnected().size()))
        );
    }

    private void handleReload(CommandSource source) {
        plugin.getConfigManager().loadConfig();
        source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §aConfiguration rechargée !"));
    }

    private void handlePriority(CommandSource source, String[] args) {
        if (args.length < 2) {
            sendPriorityHelp(source);
            return;
        }

        String action = args[1].toLowerCase();

        if ((action.equals("add") || action.equals("remove")) && args.length < 3) {
            source.sendMessage(SERIALIZER.deserialize("§cUsage: /maintenance priority " + action + " <joueur>"));
            return;
        }

        switch (action) {
            case "add" -> plugin.getServer().getPlayer(args[2]).ifPresentOrElse(
                    p -> {
                        plugin.getPriorityManager().addPriority(p.getUniqueId());
                        source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §e" + p.getUsername() + " §aajouté à la priorité."));
                    },
                    () -> source.sendMessage(SERIALIZER.deserialize("§c§lMAINTENANCE §7» §cJoueur non trouvé."))
            );

            case "remove" -> plugin.getServer().getPlayer(args[2]).ifPresentOrElse(
                    p -> {
                        plugin.getPriorityManager().removePriority(p.getUniqueId());
                        source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §e" + p.getUsername() + " §cretiré de la priorité."));
                    },
                    () -> source.sendMessage(SERIALIZER.deserialize("§c§lMAINTENANCE §7» §cJoueur non trouvé."))
            );

            case "list" -> {
                source.sendMessage(Component.text("=== LISTE PRIORITAIRE ===", NamedTextColor.GOLD));
                var players = plugin.getPriorityManager().getPriorityPlayers();
                if (players.isEmpty()) {
                    source.sendMessage(SERIALIZER.deserialize("§7La liste est vide."));
                } else {
                    for (UUID uuid : players) {
                        source.sendMessage(SERIALIZER.deserialize("§7- §e" + uuid.toString()));
                    }
                }
            }

            case "clear" -> {
                plugin.getPriorityManager().clearPriorityList();
                source.sendMessage(SERIALIZER.deserialize("§a§lMAINTENANCE §7» §aListe vidée !"));
            }

            default -> sendPriorityHelp(source);
        }
    }

    private void sendHelp(CommandSource source) {
        source.sendMessage(Component.text("=== AIDE MAINTENANCE ===", NamedTextColor.YELLOW));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance on §7- Activer"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance off §7- Désactiver"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance status §7- Voir l'état"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance priority §7- Gérer la liste"));
    }

    private void sendPriorityHelp(CommandSource source) {
        source.sendMessage(Component.text("=== AIDE PRIORITÉ ===", NamedTextColor.YELLOW));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance priority add <joueur>"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance priority remove <joueur>"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance priority list"));
        source.sendMessage(SERIALIZER.deserialize("§e/maintenance priority clear"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("maintenance.admin");
    }
}