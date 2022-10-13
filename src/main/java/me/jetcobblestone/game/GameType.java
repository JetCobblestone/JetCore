package me.jetcobblestone.game;

import lombok.Getter;
import me.jetcobblestone.game.map.GameMap;
import me.jetcobblestone.game.map.MapManager;
import me.jetcobblestone.util.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GameType<M extends GameMap<M>, G extends Game<M>> {
    @Getter private final String name;
    @Getter private final ItemStack icon;
    @Getter private final List<M> gameMaps = new ArrayList<>();
    @Getter private final File gameFolder;

    public GameType(String name, ItemStack icon) throws IOException {
        this.name = name;
        this.icon = icon;

        gameFolder = new File(MapManager.getInstance().getMapsDirectory(), name);
        if (!gameFolder.exists() && !gameFolder.mkdir()) {
            throw new FileNotFoundException("Game folder not found");
        }

        //noinspection ConstantConditions
        for (String mapName : gameFolder.list()) {
            try {
                if (!addMap(mapName)) {
                    Bukkit.getLogger().warning("Tried to load map " + mapName + " for " + name + ", but this map already exists");
                }
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Map " + mapName + " for " + name + " failed to load: InvalidConfiguration");
            }
        }

        GameManager.getInstance().register(this);
    }

    protected abstract M createMap(String name) throws IOException, InvalidConfigurationException;

    public abstract Lobby<M> getLobby();

    public abstract Gui getToolsMenu();

    public abstract G getGame(M map);

    public boolean addMap(String name) throws IOException, InvalidConfigurationException {
        for (M map : gameMaps) {
            if (map.getName().equals(name)) return false;
        }
        M map = createMap(name);
        gameMaps.add(map);
        return true;
    }

    public M getMap(String name) {
        for (M gameMap : gameMaps) {
            if (gameMap.getName().equals(name)) {
                return gameMap;
            }
        }
        return null;
    }

}


