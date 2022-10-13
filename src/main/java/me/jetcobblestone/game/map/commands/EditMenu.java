package me.jetcobblestone.game.map.commands;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jetcobblestone.JetCore;
import me.jetcobblestone.ability.ItemAbility;
import me.jetcobblestone.ability.ItemAbilityWrapper;
import me.jetcobblestone.game.map.GameMap;
import me.jetcobblestone.game.map.MapEditTracker;
import me.jetcobblestone.util.ItemFactory;
import me.jetcobblestone.util.gui.Gui;
import me.jetcobblestone.util.gui.GuiItem;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class EditMenu implements CommandExecutor {

    private final MapEditTracker mapEditTracker = MapEditTracker.getInstance();
    @Getter private static Gui editMenu;

    public EditMenu() {
        editMenu = new Gui("Edit Menu", 1, JetCore.getGuiManager());
        editMenu.setItem(0,
                new GuiItem(ItemFactory.createItem("Save", Material.LIME_WOOL),
                        event -> {
                            Player player = (Player) event.getWhoClicked();
                            GameMap<?> map = mapEditTracker.getEditedMap(player);
                            try {
                                map.save();
                                player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1, 1);
                                player.sendMessage(ChatColor.GREEN + "Map saved");
                            } catch (IOException e) {
                                player.closeInventory();
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                                player.sendMessage(ChatColor.RED + "Map failed to save");
                            }
                        }));
        editMenu.setItem(1,
                new GuiItem(ItemFactory.createItem("Quit", Material.BARRIER),
                        event -> JetCore.getPlugin().sendToSpawn((Player) event.getWhoClicked())
                ));
        editMenu.setItem(2,
                new GuiItem(ItemFactory.createItem("Unload Map", Material.RED_WOOL),
                        event -> {
                            Player player = (Player) event.getWhoClicked();
                            GameMap<?> map = mapEditTracker.getEditedMap(player);
                            map.unloadWorld();
                        }));
        editMenu.setItem(3,
                new GuiItem(ItemFactory.createItem("Tools", Material.IRON_AXE),
                        event -> {
                            Player player = (Player) event.getWhoClicked();
                            GameMap<?> map = mapEditTracker.getEditedMap(player);
                            Gui toolsMenu = map.getGameType().getToolsMenu();
                            toolsMenu.open(player);
                        }));

        ItemStack fill = ItemFactory.createItem("Fill tool", Material.BUCKET);
        ItemAbilityWrapper fillWrapper = JetCore.getAbilityManager().wrapItem(fill);
        HashMap<UUID, Pair<Location, Location>> fillMap = new HashMap<>();
        Gui fillGui = new Gui("Fill", 1, JetCore.getGuiManager());
        fillGui.setGuiClickEvent(event -> {
            event.setCancelled(false);
        });
        fillGui.setGuiCloseEvent(event -> {
            Material material;
            ItemStack item = event.getInventory().getItem(0);
            if (item == null) {
                material = Material.AIR;
            }
            else {
                material = event.getInventory().getItem(0).getType();
            }
            if (material == Material.WATER_BUCKET) {
                material = Material.WATER;
            }

            Pair<Location, Location> locations = fillMap.get(event.getPlayer().getUniqueId());
            Location l1 = locations.getFirst();
            Location l2 = locations.getSecond();
            int xChange = l1.getX() > l2.getX() ? -1 : 1;
            int yChange = l1.getY() > l2.getY() ? -1 : 1;
            int zChange = l1.getZ() > l2.getZ() ? -1 : 1;
            for (int x = (int) l1.getX(); x != l2.getX() + xChange; x += xChange) {
                for (int y = (int) l1.getY(); y != l2.getY() + yChange; y += yChange) {
                    for (int z = (int) l1.getZ(); z != l2.getZ() + zChange; z += zChange) {
                        event.getPlayer().getWorld().getBlockAt(x, y, z).setType(material);
                    }
                }
            }
            fillMap.put(event.getPlayer().getUniqueId(), null);
            return false;
        });


        fillWrapper.addAbility(PlayerInteractEvent.class, new ItemAbility<>("generic_tools_fillTool_click") {
            @Override
            public void onEvent(PlayerInteractEvent event) {
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Pair<Location, Location> locations = fillMap.get(event.getPlayer().getUniqueId());
                    if (locations == null) {
                        locations = new Pair<>(event.getClickedBlock().getLocation(), null);
                    }
                    else {
                        locations = new Pair<>(event.getClickedBlock().getLocation(), locations.getSecond());
                    }
                    fillMap.put(event.getPlayer().getUniqueId(), locations);
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Pair<Location, Location> locations = fillMap.get(event.getPlayer().getUniqueId());
                    if (locations == null) {
                        locations = new Pair<>(null, event.getClickedBlock().getLocation());
                    }
                    else {
                        locations = new Pair<>(locations.getFirst(), event.getClickedBlock().getLocation());
                    }
                    fillMap.put(event.getPlayer().getUniqueId(), locations);
                }
            }});

        fillWrapper.addAbility(PlayerDropItemEvent.class, new ItemAbility<>("generic_tools_fillTool_drop") {

            @Override
            public void onEvent(PlayerDropItemEvent event) {
                Pair<Location, Location> locations = fillMap.get(event.getPlayer().getUniqueId());
                event.setCancelled(true);
                if (locations == null || locations.getFirst() == null || locations.getSecond() == null) return;
                fillGui.open(event.getPlayer());
            }
        });

        editMenu.setItem(4, new GuiItem(fill, event -> {
            event.getWhoClicked().getInventory().addItem(fill);
        }));


        ItemStack biomeStick = ItemFactory.createItem(ChatColor.GOLD + "Biome stick", Material.STICK);
        HashMap<UUID, Boolean> biomeMap = new HashMap<>();
        ItemAbility<PlayerInteractEvent> biomeStickAbility = new ItemAbility<>("generic_tools_biomeStick_click") {
            @Override
            public void onEvent(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                boolean bool = biomeMap.getOrDefault(uuid, false);
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                    biomeMap.put(uuid, !bool);
                    if (bool) {
                        player.sendMessage(ChatColor.GOLD + "Chunk mode selected");
                    }
                    else {
                        player.sendMessage(ChatColor.GOLD + "Block mode selected");
                    }
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
                else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block clicked = event.getClickedBlock();
                    Biome biome = SetBiome.getBiomeMap().getOrDefault(uuid, Biome.PLAINS);
                    if (bool) {
                        player.sendMessage("Set biome of " + clicked.getLocation() + " to " + biome);
                        clicked.setBiome(biome);
                        clicked.getWorld().chunk
                    }
                    else {
                        Chunk chunk = clicked.getChunk();
                        for (int x = chunk.getX(); x < chunk.getX() + 16; x += 4) {
                            for (int z = chunk.getZ(); z < chunk.getZ() + 16; z += 4) {

                            }
                        }
                    }
                }
            }
        };
        ItemAbilityWrapper biomeStickWrapper = JetCore.getAbilityManager().wrapItem(biomeStick);
        biomeStickWrapper.addAbility(PlayerInteractEvent.class, biomeStickAbility);
        editMenu.setItem(5, new GuiItem(biomeStick, event -> {
            event.getWhoClicked().getInventory().addItem(biomeStick);
        }));

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }
        Player player = (Player) commandSender;
        GameMap<?> map = mapEditTracker.getEditedMap(player);
        if (map == null) {
            player.sendMessage(ChatColor.RED + "You can only use this when editing a map");
            return false;
        }
        editMenu.open(player);
        return true;
    }
}
