package main.shop.listeners;

import main.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class MessageListener implements Listener {

    private final Shop plugin;

    public MessageListener(Shop plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String grade = Shop.getInstance().getMessageManager().getGrade(player);
        if (grade == null) return;

        try {
            String msg = Shop.getInstance().getMessageManager().getJoinMessage(player);
            if (msg == null) msg = Shop.getInstance().getMessageManager().getDefaultMessage(grade, "join");
            if (msg == null) return;

            e.setJoinMessage(null); // supprime le message vanilla
            Bukkit.broadcastMessage(msg.replace("%player%", player.getName()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String grade = Shop.getInstance().getMessageManager().getGrade(player);
        if (grade == null) return;

        try {
            String msg = Shop.getInstance().getMessageManager().getQuitMessage(player);
            if (msg == null) msg = Shop.getInstance().getMessageManager().getDefaultMessage(grade, "quit");
            if (msg == null) return;

            e.setQuitMessage(null);
            Bukkit.broadcastMessage(msg.replace("%player%", player.getName()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        if (!(victim.getKiller() instanceof Player killer)) return;

        String grade = Shop.getInstance().getMessageManager().getGrade(killer);
        if (grade == null) return;

        try {
            String msg = Shop.getInstance().getMessageManager().getKillMessage(killer);
            if (msg == null) msg = Shop.getInstance().getMessageManager().getDefaultMessage(grade, "kill");
            if (msg == null) return;

            e.setDeathMessage(null);
            Bukkit.broadcastMessage(msg
                    .replace("%player%", killer.getName())
                    .replace("%victim%", victim.getName()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}