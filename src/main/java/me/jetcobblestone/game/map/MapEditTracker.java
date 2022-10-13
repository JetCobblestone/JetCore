package me.jetcobblestone.game.map;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class MapEditTracker {

    @Getter private static final MapEditTracker instance = new MapEditTracker();

    private MapEditTracker() {}


    private final Map<UUID, GameMap<?>> editMap = new HashMap<>();
    private final Set<GameMap<?>> loadedMaps = new HashSet<>();

    public void addPlayer(Player player, GameMap<?> gameMap) {editMap.put(player.getUniqueId(), gameMap);}
    public void removePlayer(Player player) {editMap.remove(player.getUniqueId());}
    public void addLoaded(GameMap<?> map) {loadedMaps.add(map);}
    public void removeLoaded(GameMap<?> map) {loadedMaps.remove(map);}

    public GameMap<?> getEditedMap(Player player) {
        GameMap<?> map = editMap.get(player.getUniqueId());
        if (map != null && map.getMapFile().toString().equals(player.getWorld().getWorldFolder().toString().substring(2))) {
            return map;
        }
        editMap.remove(player.getUniqueId());
        return null;
    }

    public void disable() {
        for (GameMap<?> map : loadedMaps) {
            try {
                map.save();
            } catch (IOException e) {
                Bukkit.getLogger().severe(map.getName() + " could not be saved");
            }
            map.unloadWorld();
        }
    }
}
