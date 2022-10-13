package me.jetcobblestone.game;

import lombok.Getter;
import me.jetcobblestone.game.map.GameMap;
import me.jetcobblestone.game.map.MapClone;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Game<T extends GameMap<T>> {

    @Getter private final MapClone<T> gameMap;

    public Game(MapClone<T> gameMap, GameManager manager) {
        this.gameMap = gameMap;
        manager.addGame(this);
    }

    public abstract void startGame(List<Player> players);

    public abstract void destroy();

    public abstract void addPlayer(Player player);

    public abstract void removePlayer(Player player);
}
