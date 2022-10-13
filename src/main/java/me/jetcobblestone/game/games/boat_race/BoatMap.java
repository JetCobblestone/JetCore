package me.jetcobblestone.game.games.boat_race;

import lombok.Getter;
import me.jetcobblestone.Position;
import me.jetcobblestone.SerializablePair;
import me.jetcobblestone.game.map.GameMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoatMap extends GameMap<BoatMap> {

    @Getter private List<Position> spawns;
    @Getter private List<SerializablePair<Vector, Vector>> checkpoints;

    public BoatMap(String name, BoatType boatType) throws IOException, InvalidConfigurationException {
        super(name, boatType);
    }

    @Override
    public void loadMetaData(FileConfiguration file) {
        List<?> list = file.getList("spawnpoints");
        spawns = new ArrayList<>();
        if (list != null) {
            //noinspection unchecked
            spawns.addAll((List<Position>) list);
        }

        list = file.getList("checkpoints");
        checkpoints = new ArrayList<>();
        if (list != null) {
            //noinspection unchecked
            checkpoints.addAll((List< SerializablePair<Vector, Vector>>) list);
        }
    }

    @Override
    public void saveMetaData(FileConfiguration file) {
        file.set("spawnpoints", spawns);
        file.set("checkpoints", checkpoints);
    }
}
