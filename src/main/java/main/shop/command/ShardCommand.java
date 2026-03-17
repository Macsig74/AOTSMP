package main.shop.command;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ShardCommand implements CommandExecutor {

    private final Shop plugin;

    public ShardCommand(Shop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /shard <give|take|set|dump> <player> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable !");
            return true;
        }

        try {
            int current = Shop.getInstance().getShardManager().getShards(target);

            switch (args[0].toLowerCase()) {
                case "give" -> {
                    if (args.length < 3) { sender.sendMessage("§cUsage: /shard give <player> <amount>"); return true; }
                    int amount = Integer.parseInt(args[2]);
                    Shop.getInstance().getShardManager().setShards(target, current + amount);
                    sender.sendMessage("§a+" + amount + " shards donnés à §5" + target.getName());
                    target.sendMessage("§5Vous avez reçu §d" + amount + " shards§5 !");
                }
                case "take" -> {
                    if (args.length < 3) { sender.sendMessage("§cUsage: /shard take <player> <amount>"); return true; }
                    int amount = Integer.parseInt(args[2]);
                    Shop.getInstance().getShardManager().setShards(target, Math.max(0, current - amount));
                    sender.sendMessage("§c-" + amount + " shards retirés à §5" + target.getName());
                    target.sendMessage("§c-" + amount + " shards retirés de votre compte.");
                }
                case "set" -> {
                    if (args.length < 3) { sender.sendMessage("§cUsage: /shard set <player> <amount>"); return true; }
                    int amount = Integer.parseInt(args[2]);
                    Shop.getInstance().getShardManager().setShards(target, amount);
                    sender.sendMessage("§aShards de §5" + target.getName() + " §aset à §d" + amount);
                    target.sendMessage("§5Vos shards ont été définis à §d" + amount);
                }
                case "dump" -> {
                    sender.sendMessage("§5" + target.getName() + " §7possède §d" + current + " shards§7.");
                }
                default -> sender.sendMessage("§cUsage: /shard <give|take|set|dump> <player> [amount]");
            }
        } catch (SQLException e) {
            sender.sendMessage("§cErreur base de données !");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cMontant invalide !");
        }
        return true;
    }
}