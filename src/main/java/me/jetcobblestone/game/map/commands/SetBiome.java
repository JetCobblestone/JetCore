package me.jetcobblestone.game.map.commands;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SetBiome implements CommandExecutor {

    @Getter private static final List<String> biomeStrings = Arrays.stream(Biome.values()).map(Enum::toString).toList();
   @Getter private static final Map<UUID, Biome> biomeMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can use this command");
            return false;
        }

        if (!commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if (args.length != 1) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /setbiome (biome)");
            return false;
        }
        if (!biomeStrings.contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + args[0] + " biome could not be found");
        }
        Player player = (Player) commandSender;
        Biome biome = Biome.valueOf(args[0]);
        biomeMap.put(player.getUniqueId(), biome);
        player.sendMessage(ChatColor.GOLD + "You selected " + args[0]);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        return true;
    }

    public static class TabComplete implements TabCompleter {

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            if (args.length > 1) return null;
            List<String> values = new ArrayList<>();
            for (String biome : biomeStrings) {
                if (biome.contains(args[0])) {
                    values.add(biome);
                }
            }
            return values;
        }
    }
}
