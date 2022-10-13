package me.jetcobblestone.game;

import me.jetcobblestone.JetCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager implements Listener {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
            Bukkit.getPluginManager().registerEvents(instance, JetCore.getPlugin());
        }
        return instance;
    }

    private GameManager(){}

    private final Set<GameType<?,?>> gameTypes = new HashSet<>();
    private final Set<Game<?>> games = new HashSet<>();
    private final HashMap<UUID, Game<?>> playerMap = new HashMap<>();

    public void register(GameType<?,?> gameType) {
        gameTypes.add(gameType);
    }

    public Set<GameType<?,?>> getTypes() {
        return new HashSet<>(gameTypes);
    }

    public void disableGame(Game<?> game) {
        game.destroy();
        game.getGameMap().destroy();
        games.remove(game);
    }

    public void addGame(Game<?> game) {
        games.add(game);
    }

    public void disableAll() {
        for (Game<?> game : games) {
            disableGame(game);
        }
    }

    public void addPlayer(Player player, Game<?> game) {
        playerMap.put(player.getUniqueId(), game);
    }

    public void removePlayer(Player player) {
        playerMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game<?> game = playerMap.get(player.getUniqueId());
        if (game != null) {
            game.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game<?> game = playerMap.get(player.getUniqueId());
        if (game != null) {
            game.removePlayer(player);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getWorldFolder() != player.getWorld().getWorldFolder()) {
            Game<?> game = playerMap.get(player.getUniqueId());
            if (game != null) {
                game.removePlayer(player);
            }
        }
    }
}
