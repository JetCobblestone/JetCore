package me.jetcobblestone.game.map;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

public class MapClone<T extends GameMap<T>> {
    @Getter private final World world;
    @Getter private final int id;
    @Getter private final T parent;
    private final MapManager mapManager;

    public MapClone(World world, int id, T parent, MapManager mapManager) {
        this.world = world;
        this.id = id;
        this.parent = parent;
        this.mapManager = mapManager;
    }

    public void destroy() {
        Bukkit.unloadWorld(world, true);
        File file = world.getWorldFolder();
        if (forceDelete(file)) {
            mapManager.deregisterClone(MapClone.this);
            return;
        }
        if (file.exists()) {
            Bukkit.getLogger().severe("Could not delete map clone: " + file.getPath());
        }
    }

    public boolean forceDelete(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                forceDelete(child);
            }
        }
        return file.delete();
    }

}
