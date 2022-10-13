package me.jetcobblestone.game.map.commands;

import me.jetcobblestone.game.GameManager;
import me.jetcobblestone.game.GameType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateMap implements CommandExecutor {

    private final GameManager gameManager = GameManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /createMap [game] [name]");
            return false;
        }

        Set<GameType<?,?>> gameTypes = gameManager.getTypes();
        GameType<?,?> gameType = null;

        for (GameType<?,?> g : gameTypes) {
            if (g.getName().equals(args[0])) {
                gameType = g;
            }
        }

        if (gameType == null) {
            commandSender.sendMessage(ChatColor.RED + "Supplied game could not be found");
            return false;
        }

        try {
            if (!gameType.addMap(args[1])) {
                commandSender.sendMessage(ChatColor.RED + "That map already exists");
                return false;
            }
        } catch (IOException | InvalidConfigurationException e ) {
            commandSender.sendMessage(ChatColor.RED + "An error occured whilst trying to create map");
            return false;
        }

        commandSender.sendMessage(ChatColor.GREEN + "Map has been created");
        return true;
    }


    public static class TabCompleter implements org.bukkit.command.TabCompleter {

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            List<String> values = new ArrayList<>();

            if (args.length == 1) {
                GameManager.getInstance().getTypes().forEach(gameType -> {
                    if (gameType.getName().startsWith(args[0])) values.add(gameType.getName());
                });
            }
            return values;
        }
    }
}
