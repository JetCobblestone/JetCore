package me.jetcobblestone.game;

import me.jetcobblestone.game.map.GameMap;
import org.bukkit.entity.Player;

public abstract class Lobby<M extends GameMap<M>> {

    public abstract void finishTimer();

    public abstract void resetTimer();

    public abstract void addPlayer(Player player);

    public abstract void close();

}
