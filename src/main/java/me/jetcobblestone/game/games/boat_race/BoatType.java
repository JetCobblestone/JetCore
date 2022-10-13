package me.jetcobblestone.game.games.boat_race;

import com.mojang.datafixers.util.Pair;
import me.jetcobblestone.JetCore;
import me.jetcobblestone.Position;
import me.jetcobblestone.SerializablePair;
import me.jetcobblestone.ability.ItemAbilityManager;
import me.jetcobblestone.ability.ItemAbilityWrapper;
import me.jetcobblestone.game.GameManager;
import me.jetcobblestone.game.GameType;
import me.jetcobblestone.game.Lobby;
import me.jetcobblestone.game.map.MapEditTracker;
import me.jetcobblestone.game.map.MapManager;
import me.jetcobblestone.game.map.ToolAbility;
import me.jetcobblestone.game.map.commands.EditMenu;
import me.jetcobblestone.util.ItemFactory;
import me.jetcobblestone.util.gui.Gui;
import me.jetcobblestone.util.gui.GuiItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoatType extends GameType<BoatMap, BoatRace> {

    private final GameManager gameManager = GameManager.getInstance();
    private final MapManager mapManager = MapManager.getInstance();
    private final ItemAbilityManager abilityManager = JetCore.getAbilityManager();
    private final MapEditTracker mapEditTracker = MapEditTracker.getInstance();
    private final Gui tools;

    public BoatType(String name, ItemStack icon) throws IOException {
        super(name, icon);

        tools = new Gui("Tools", 1, JetCore.getGuiManager());


        //CHECKPOINT CREATOR
        ItemStack checkpointCreator = ItemFactory.createItem("Checkpoint Creator", Material.IRON_AXE);
        ItemAbilityWrapper checkpointCreatorW = abilityManager.wrapItem(checkpointCreator);

        final Map<UUID, Pair<Location, Location>> locationsMap = new HashMap<>();
        checkpointCreatorW.addAbility(PlayerInteractEvent.class, new ToolAbility<>("boatgame_tools_checkpointCreator_click",
                this,
                PlayerInteractEvent::getPlayer,
                PlayerInteractEvent::getItem) {

            @Override
            public void onTrigger(PlayerInteractEvent event) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();

                    Pair<Location, Location> locations = locationsMap.get(player.getUniqueId());
                    Location l1 = event.getClickedBlock().getLocation();
                    Location l2;
                    if (locations == null) {
                        l2 = null;
                    } else {
                        l2 = locations.getSecond();
                    }
                    locationsMap.put(player.getUniqueId(), new Pair<>(l1, l2));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.9f);
                }
                else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();

                    Pair<Location, Location> locations = locationsMap.get(player.getUniqueId());
                    Location l1;
                    Location l2 = event.getClickedBlock().getLocation();
                    if (locations == null) {
                        l1 = null;
                    } else {
                        l1 = locations.getFirst();
                    }
                    locationsMap.put(player.getUniqueId(), new Pair<>(l1, l2));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.1f);
                }
            }
        });
        checkpointCreatorW.addAbility(PlayerDropItemEvent.class, new ToolAbility<>("boatgame_tools_checkpointCreator_drop",
                this,
                PlayerDropItemEvent::getPlayer,
                event -> event.getItemDrop().getItemStack()) {
            @Override
            public void onTrigger(PlayerDropItemEvent event) {
                event.setCancelled(true);

                Player player = event.getPlayer();
                Pair<Location, Location> locations = locationsMap.get(player.getUniqueId());

                if (locations == null) {
                    player.sendMessage(ChatColor.RED + "2 locations have not been set");
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                    event.setCancelled(true);
                    return;
                }

                Location l1 = locations.getFirst();
                Location l2 = locations.getSecond();
                if (l1 == null || l2 == null) {
                    player.sendMessage(ChatColor.RED + "2 locations have not been set");
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
                else {
                    Vector dif = l1.toVector().subtract(l2.toVector());
                    if (dif.getX() >= 0) {
                        l1.add(1, 0, 0);
                    }
                    else {
                        l2.add(1, 0 , 0);
                    }

                    if (dif.getY() >= 0) {
                        l1.add(0, 1, 0);
                    }
                    else {
                        l2.add(0, 1, 0);
                    }

                    if (dif.getZ() >= 0) {
                        l1.add(0, 0, 1);
                    }
                    else {
                        l2.add(0, 0, 1);
                    }


                    BoatMap boatMap = (BoatMap) mapEditTracker.getEditedMap(player);
                    boatMap.getCheckpoints().add(new SerializablePair<>(l1.toVector(), l2.toVector()));
                    player.sendMessage(ChatColor.GREEN + "Checkpoint created: " + ChatColor.GOLD + l1.toVector() + " - " + l2.toVector());
                    player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1, 1);
                    locationsMap.remove(player.getUniqueId());
                }
            }

        });

        tools.setItem(0, new GuiItem(checkpointCreator,
                event -> event.getWhoClicked().getInventory().addItem(checkpointCreator)
        ));


        //SPAWNPOINT CREATOR
        ItemStack spawnpointCreator = ItemFactory.createItem("Spawnpoint Creator", Material.IRON_SHOVEL);
        ItemAbilityWrapper spawnpointCreatorW = abilityManager.wrapItem(spawnpointCreator);

        final Map<UUID, Location> spawnpointMap = new HashMap<>();

        spawnpointCreatorW.addAbility(PlayerInteractEvent.class, new ToolAbility<>("boatgame_tools_spawnpointCreator_lClick",
                this,
                PlayerInteractEvent::getPlayer,
                PlayerInteractEvent::getItem) {
            @Override
            public void onTrigger(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    spawnpointMap.put(player.getUniqueId(), player.getLocation());
                    event.setCancelled(true);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    spawnpointMap.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                    event.setCancelled(true);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
                else {
                    Location location = spawnpointMap.get(player.getUniqueId());
                    if (location == null) return;

                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());
                        player.playSound(player, Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1, 1);
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Location blockLocation = event.getClickedBlock().getLocation();
                        blockLocation.add(0.5, 0.5, 0.5);
                        location.setDirection(blockLocation.toVector().subtract(location.toVector()));
                        player.playSound(player, Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1, 1);
                        event.setCancelled(true);
                    }
                }
            }
        });
        spawnpointCreatorW.addAbility(PlayerDropItemEvent.class, new ToolAbility<>("boatgame_tools_spawnpointCreator_drop",
                this,
                PlayerDropItemEvent::getPlayer,
                (event -> event.getItemDrop().getItemStack())) {

            @Override
            public void onTrigger(PlayerDropItemEvent event) {

                Player player = event.getPlayer();
                BoatMap map = (BoatMap) mapEditTracker.getEditedMap(player);

                Location loc = spawnpointMap.get(player.getUniqueId());
                loc.add(0.5, 1, 0.5);
                map.getSpawns().add(Position.fromLocation(loc));
                player.sendMessage(ChatColor.GREEN + "Spawnpoint created at " + ChatColor.GOLD + loc.toVector());
                player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1, 1);

                event.setCancelled(true);
            }
        });

        tools.setItem(1, new GuiItem(spawnpointCreator,
                event -> event.getWhoClicked().getInventory().addItem(spawnpointCreator)
        ));


        //BACK ARROW
        tools.setItem(8, new GuiItem(ItemFactory.createItem("back", Material.ARROW),
                event -> {
                    EditMenu.getEditMenu().open(event.getWhoClicked());
                }));
    }

    @Override
    public BoatMap createMap(String name) throws IOException, InvalidConfigurationException {
        return new BoatMap(name, this);
    }

    @Override
    public Lobby<BoatMap> getLobby() {

        return null;
    }

    @Override
    public Gui getToolsMenu() {
        return tools;
    }

    @Override
    public BoatRace getGame(BoatMap map) {
        try {
            return new BoatRace(gameManager, mapManager.copyMap(map));
        } catch (IOException e) {
            return null;
        }
    }
}
