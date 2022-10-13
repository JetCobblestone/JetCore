package me.jetcobblestone.util.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BoardListener implements Listener {

    private final JavaPlugin plugin;

    public BoardListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PacketBoard board = PacketBoard.getPlayerMap().get(player.getUniqueId());
        if (board != null) {
            board.initPlayer(player);
        }
    }
}
