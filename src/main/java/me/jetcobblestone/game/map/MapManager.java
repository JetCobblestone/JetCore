package me.jetcobblestone.game.map;

import lombok.Getter;
import me.jetcobblestone.JetCore;
import me.jetcobblestone.game.map.commands.CreateMap;
import me.jetcobblestone.game.map.commands.EditCommand;
import me.jetcobblestone.game.map.commands.EditMenu;
import me.jetcobblestone.game.map.commands.SetBiome;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapManager {

    private static MapManager instance;

    public static MapManager getInstance() throws IOException{
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    private final File dataFolder = JetCore.getPlugin().getDataFolder();
    @Getter private final File mapsDirectory = new File(dataFolder, "maps");
    private final File clones = new File(dataFolder, "clones");
    private final Map<GameMap<?>, List<MapClone<?>>> clonesMap = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    private MapManager() throws IOException {
        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            throw new IOException("File could not be created",
                    new Throwable("Plugin data folder could not be created"));
        }

        if (!mapsDirectory.exists() && !mapsDirectory.mkdir()) {
            throw new IOException("File could not be created",
                    new Throwable("Maps folder could not be created"));
        }

        JetCore.getPlugin().getCommand("edit").setExecutor(new EditCommand(this));
        JetCore.getPlugin().getCommand("edit").setTabCompleter(new EditCommand.TabCompleter(this));
        JetCore.getPlugin().getCommand("editMenu").setExecutor(new EditMenu());
        JetCore.getPlugin().getCommand("createMap").setExecutor(new CreateMap());
        JetCore.getPlugin().getCommand("createMap").setTabCompleter(new CreateMap.TabCompleter());
        JetCore.getPlugin().getCommand("setBiome").setExecutor(new SetBiome());
        JetCore.getPlugin().getCommand("setBiome").setTabCompleter(new SetBiome.TabComplete());
    }


    public <M extends GameMap<M>> MapClone<M> copyMap(M map) throws IOException {
        List<MapClone<?>> cloneList = clonesMap.get(map);
        int id = 0;
        if (cloneList == null) {
            cloneList = new ArrayList<>();
            clonesMap.put(map, cloneList);
        }
        else {
            boolean found = false;
            while (!found) {
                if (cloneList.get(id).getId() == id) {
                    id++;
                }
                else {
                    found = true;
                }
            }
        }

        String[] path = map.getBukkitPath().split("/");
        int len = path.length;
        File dir = new File(clones, path[len-2] + path[len-1] + id);
        FileUtils.copyDirectory(map.getMapFile(), dir);
        File[] files = dir.listFiles((dir1, name) -> name.equals("uid.dat"));
        for (File file : files) {
            if (!file.delete()) {
                throw new IOException();
            }
        }
        World world = Bukkit.createWorld(new WorldCreator(getBukkitPath(dir)).generator(new VoidChunkGenerator()));

        MapClone<M> clone = new MapClone<>(world, id, map, this);
        cloneList.add(clone);
        cloneList.sort(Comparator.comparingInt(MapClone::getId));
        return clone;
    }

    public String getBukkitPath(File file) {
        if (File.separator.equals("\\")) {
            return file.getPath().replaceAll("\\\\", "/").replaceAll(" ", "");
        }
        return file.getPath().replaceAll(File.separator, "/");
    }

    public void deregisterClone(MapClone<?> clone) {
        clonesMap.get(clone.getParent()).remove(clone);
    }



}
