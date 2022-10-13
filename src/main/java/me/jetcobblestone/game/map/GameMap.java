package me.jetcobblestone.game.map;

import lombok.Getter;
import lombok.Setter;
import me.jetcobblestone.JetCore;
import me.jetcobblestone.game.GameType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class GameMap<M extends GameMap<M>> {

    private final MapManager mapManager;
    @Getter private final String name;
    @Getter private final GameType<M,?> gameType;
    @Getter private final File mapFile;
    @Getter private final String bukkitPath;

    private final WorldCreator worldCreator;
    @Setter private ChunkGenerator generator = new VoidChunkGenerator();
    private final FileConfiguration metaData;
    private final File metaDataFile;

    private boolean loaded = false;
    private World world;

    private final List<UUID> editing = new ArrayList<>();

    public GameMap(String name, GameType<M,?> gameType) throws IOException, InvalidConfigurationException {
        this.name = name;
        this.gameType = gameType;
        this.mapManager = MapManager.getInstance();

        mapFile = new File(gameType.getGameFolder(), name);
        if (!mapFile.exists()) {
            if (!mapFile.mkdir()) {
                throw new IOException("Map folder could not be created");
            }
        }

        bukkitPath = mapManager.getBukkitPath(mapFile);
        worldCreator = new WorldCreator(bukkitPath);

        metaDataFile = new File(mapFile, "metadata.yml");
        if (!metaDataFile.exists()) {
            if (!metaDataFile.createNewFile()) {
                throw new IOException("Metadata file could not be created");
            }
        }
        metaData = new YamlConfiguration();
        metaData.load(metaDataFile);
        loadMetaData(metaData);
    }

    protected abstract void loadMetaData(FileConfiguration file);

    protected abstract void saveMetaData(FileConfiguration file);

    public boolean loadWorld() {
        if (!loaded) {
            world = Bukkit.createWorld(worldCreator.generator(generator));
            if (world != null) {
                loaded = true;
                MapEditTracker.getInstance().addLoaded(this);
                return true;
            }
            return false;
        }
        return true;
    }

    public void unloadWorld() {
        if (loaded) {
            for (Player player : world.getPlayers()) {
                JetCore.getPlugin().sendToSpawn(player);
            }
            loaded = !Bukkit.unloadWorld(world, false);
            MapEditTracker.getInstance().removeLoaded(this);
        }

    }

    public void save() throws IOException {
        saveMetaData(metaData);
        metaData.save(metaDataFile);
        world.save();
    }

    public boolean edit(Player player) {
        if (!loaded) return false;
        player.teleport(world.getSpawnLocation());
        MapEditTracker.getInstance().addPlayer(player, this);
        return true;
    }
}
