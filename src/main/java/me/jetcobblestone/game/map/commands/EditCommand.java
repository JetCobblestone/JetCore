package me.jetcobblestone.game.map.commands;

import me.jetcobblestone.game.GameManager;
import me.jetcobblestone.game.GameType;
import me.jetcobblestone.game.map.GameMap;
import me.jetcobblestone.game.map.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditCommand implements CommandExecutor {
    private static final GameManager gameManager = GameManager.getInstance();
    private final MapManager mapManager;

    public EditCommand(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if (args.length > 3) {
            commandSender.sendMessage(ChatColor.RED + "Too many arguments. Usage: /edit [game] [map] (player)");
        }

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Not enough arguments. Usage: /edit [game] [map] (player)");
            return false;
        }
        GameType<?,?> gameType = null;
        for (GameType<?,?> type : gameManager.getTypes()) {
            if (type.getName().equals(args[0])) {
                gameType = type;
                break;
            }
        }
        if (gameType == null) {
            commandSender.sendMessage(ChatColor.RED + "The provided game does not exist");
            return false;
        }


        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "A map belonging to the provided game must be provided");
            return false;
        }


        GameMap<?> map = gameType.getMap(args[1]);
        if (map == null) {
            commandSender.sendMessage(ChatColor.RED + "That map could not be found");
            return false;
        }


        Player player;
        if (!(commandSender instanceof Player)) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.RED + "A player must be provided");
                return false;
            }
        }

        if (args.length >= 3) {
            player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + "The provided player could not be found");
                return false;
            }
        }
        else {
            player = (Player) commandSender;
        }

        if (!map.loadWorld()) {
            commandSender.sendMessage(ChatColor.RED + "The provided map could not be loaded!");
            return false;
        }
        if (map.edit(player)) {
            player.sendMessage(ChatColor.GOLD + "You are now editing " + args[1]);
            return true;
        }

        commandSender.sendMessage(ChatColor.RED + "Something weird happened, could not teleport after map loaded");
        return false;
    }

    public static class TabCompleter implements org.bukkit.command.TabCompleter {
        private final MapManager mapManager;

        public TabCompleter(MapManager mapManager) {
            this.mapManager = mapManager;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
            List<String> values = new ArrayList<>();
            GameType<?,?> gameType = null;

            for (GameType<?,?> type : gameManager.getTypes()) {
                if (args.length != 0 && !(type.getName().startsWith(args[0]))) {
                    continue;
                }
                if (args.length <= 1) {
                    values.add(type.getName());
                }
                else {
                    gameType = type;
                    break;
                }
            }
            if (args.length <= 1) {
                return values;
            }

            if (gameType == null) return values;
            if (args.length == 2) {
                File dir = new File(mapManager.getMapsDirectory(), gameType.getName());
                for (String name : Objects.requireNonNull(dir.list())) {
                    if (name.startsWith(args[1])) {
                        values.add(name);
                    }
                }
                return values;
            }

            if (args.length == 3) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().startsWith(args[2])) {
                        values.add(player.getName());
                    }
                    return values;
                }
            }

            return values;
        }
    }
}
