package me.jetcobblestone.game.games.boat_race;

import me.jetcobblestone.Area;
import me.jetcobblestone.JetCore;
import me.jetcobblestone.Position;
import me.jetcobblestone.SerializablePair;
import me.jetcobblestone.game.Game;
import me.jetcobblestone.game.GameManager;
import me.jetcobblestone.game.map.MapClone;
import me.jetcobblestone.permissions.PermissionProfile;
import me.jetcobblestone.permissions.PermissionsManager;
import me.jetcobblestone.util.scoreboard.PacketBoard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BoatRace extends Game<BoatMap> {

    private final int maxLaps = 3;
    private final List<Location> spawns = new ArrayList<>();
    private final List<Area> checkpoints = new ArrayList<>();
    private final World world;
    private final List<BoatPlayer> finished = new ArrayList<>();
    private final List<BoatPlayer> participants = new ArrayList<>();
    private int ticks = 0;
    private int timeLeft = -1;
    private final GameManager manager;
    private BukkitRunnable runnable;

    private GameState gameState = GameState.START;

    private enum GameState {
        START,
        MAIN,
        FINISH
    }

    public BoatRace(GameManager manager, MapClone<BoatMap> boatMap) {
        super(boatMap, manager);
        this.manager = manager;
        world = boatMap.getWorld();

        List<Position> positions = boatMap.getParent().getSpawns();
        positions.forEach(position -> spawns.add(position.toLocation(world)));

        List<SerializablePair<Vector,Vector>> areas = boatMap.getParent().getCheckpoints();
        areas.forEach(pair -> checkpoints.add(new Area(pair.getLeft(), pair.getRight(), world)));
    }


    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private void calculateScoreboard() {

        participants.sort((o1, o2) -> {
            if (o1.finished && o2.finished) return Double.compare(o2.time, o1.time);
            if (!((!o1.finished) && (!o2.finished))) {
                if (o1.finished) return 1;
                return -1;
            }
            if (o1.lap != o2.lap) return o1.lap - o2.lap;
            if (o1.checkpoint != o2.checkpoint) return o1.checkpoint - o2.checkpoint;
            Area area = checkpoints.get(Math.floorMod(o1.checkpoint + 1, checkpoints.size()));
            double distance1 = area.getDistance(o1.getPlayer());
            double distance2 = area.getDistance(o2.getPlayer());
            return Double.compare(distance2, distance1);
        });

        for (BoatPlayer player : participants) {

            PacketBoard board = player.packetBoard;
            int size = 5 + participants.size();
            if (size > 16) {
                size = 16;
            }
            board.setSize(size);

            if (player.finished) {
                board.setScore(2, "Lap: Finished!");
            } else {
                board.setScore(1, "Time: " + decimalFormat.format(((double) ticks) / 20d) + "s");
                board.setScore(2, "Lap: " + player.lap + "/" + maxLaps);
            }


            if (participants.size() < 16) {
                for (int i = 0; i < participants.size(); i++) {
                    board.setScore(i+4, (participants.size() - i ) + ") " + participants.get(i).getPlayer().getDisplayName());
                }
            }
            else {
                int i = participants.indexOf(player);
                if (i <= 5) {
                    for (int x = 0; x < 11; x++) {
                        board.setScore(x + 4, (participants.size() - x) + ") " + participants.get(x).getPlayer().getDisplayName());
                    }
                }
                else if (i >= participants.size() - 6) {
                    for (int x = participants.size() - 1; x > participants.size() - 12; x--) {
                        board.setScore(x + 4, (participants.size() - x) + ") " + participants.get(x).getPlayer().getDisplayName());
                    }
                }
                else {
                    for (int x = i-5; x <= i+5; x++) {
                        board.setScore(x + 4, (participants.size() - x) + ") " + participants.get(x).getPlayer().getDisplayName());
                    }
                }
            }
        }
    }

    @Override
    public void startGame(List<Player> players) {

        runnable = new BukkitRunnable() {

            @Override
            public void run() {

                if (gameState == GameState.START) {
                    switch (ticks) {
                        case 0 -> {
                            participants.clear();
                            participants.addAll(players.stream().map(BoatPlayer::new).toList());

                            List<Location> clone = new ArrayList<>(spawns);

                            for (BoatPlayer boatPlayer : participants) {
                                Player player = boatPlayer.getPlayer();
                                PacketBoard board = boatPlayer.packetBoard;
                                board.setSize(0);


                                int index = new Random().nextInt(spawns.size()-1);
                                Location spawn = clone.get(index);
                                clone.remove(index);

                                Boat boat = (Boat) world.spawnEntity(spawn, EntityType.BOAT);
                                boatPlayer.boat = boat;
                                player.teleport(boat);
                                boat.addPassenger(player);

                                PermissionProfile<Player> profile = PermissionsManager.getInstance().getProfile(player);
                                profile.allowAll();
                                profile.blockPerm(EntityDamageByEntityEvent.class);
                                profile.blockPerm(VehicleExitEvent.class);
                                profile.blockPerm(VehicleMoveEvent.class);
                                profile.blockPerm(BlockBreakEvent.class);

                                player.setFoodLevel(20);
                                player.setHealth(20);
                                player.getInventory().clear();

                                player.sendTitle(ChatColor.GOLD + "Welcome to " + ChatColor.RED + "Boat Race", null,0, 10, 40);
                                player.sendMessage(ChatColor.YELLOW + "Boat race starting in 10s...");
                                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.RECORDS, 1, 1);
                            }
                            calculateScoreboard();
                        }


                        case 100 -> participants.forEach(boatPlayer -> {
                            if (!boatPlayer.inGame) return;
                            Player player = boatPlayer.getPlayer();
                            player.sendMessage(ChatColor.YELLOW + "Boat race starting in 5s...");
                        });
                        case 140 -> participants.forEach(boatPlayer -> {
                            if (!boatPlayer.inGame) return;
                            Player player = boatPlayer.getPlayer();
                            player.sendTitle(ChatColor.RED + "3", null, 0, 0, 20);
                            player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.RECORDS, 1, 2);
                        });
                        case 160 -> participants.forEach(boatPlayer -> {
                            if (!boatPlayer.inGame) return;
                            Player player = boatPlayer.getPlayer();
                            player.sendTitle(ChatColor.YELLOW + "2", null, 0, 0, 20);
                            player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.RECORDS, 1, 2);
                        });
                        case 180 -> participants.forEach(boatPlayer -> {
                            if (!boatPlayer.inGame) return;
                            Player player = boatPlayer.getPlayer();
                            player.sendTitle(ChatColor.YELLOW + "1", null, 0, 0, 20);
                            player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.RECORDS, 1, 2);
                        });
                        case 200 -> {
                            participants.forEach(boatPlayer -> {
                                Player player = boatPlayer.getPlayer();
                                PermissionProfile<Player> profile = PermissionsManager.getInstance().getProfile(player);
                                profile.allowPerm(VehicleMoveEvent.class);

                                if (!boatPlayer.inGame) return;
                                player.sendTitle(ChatColor.GREEN + "GO!", null, 0, 0, 20);
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 1);
                            });

                            ticks = 0;
                            gameState = GameState.MAIN;
                        }
                    }
                }

                if (gameState == GameState.MAIN) {

                    for (BoatPlayer boatPlayer : participants) {

                        if (boatPlayer.finished || !boatPlayer.inGame) continue;

                        //Check if they go to next checkpoint
                        int nextIndex = Math.floorMod(boatPlayer.checkpoint + 1, checkpoints.size());
                        Area next = checkpoints.get(nextIndex);
                        Player player = boatPlayer.getPlayer();

                        if (next.contains(player)) {
                            boatPlayer.checkpoint = nextIndex;

                            if (boatPlayer.checkpoint == 0) {
                                if (boatPlayer.lap == maxLaps) {
                                    boatPlayer.finished = true;
                                    boatPlayer.time = ticks / 20d;

                                    finished.add(boatPlayer);
                                    player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.RECORDS, 1, 1);
                                    player.sendMessage(ChatColor.YELLOW + "You finished " + ChatColor.GOLD + "#" + finished.size());
                                }
                                else {
                                    boatPlayer.lap++;
                                }
                            }

                        }

                        //Check if they go backwards
                        else {
                            int prevIndex = Math.floorMod(boatPlayer.checkpoint - 1, checkpoints.size());
                            Area prev = checkpoints.get(prevIndex);
                            if (prev.contains(player)) {
                                if (boatPlayer.checkpoint == 0 && boatPlayer.lap > 0) {
                                    boatPlayer.lap--;
                                }

                                boatPlayer.checkpoint = prevIndex;
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.8f);
                                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "Wrong way", null, 10, 10, 20);
                            }
                        }
                    }

                    calculateScoreboard();

                    if (finished.size() == participants.size()) {
                        ticks = 0;
                        gameState = GameState.FINISH;
                    }
                    else {
                        if (finished.size() == 3 && timeLeft == -1) {
                            timeLeft = 600;
                        }
                        if (timeLeft != -1) {
                            switch (timeLeft) {
                                case 600 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GREEN + "3 players have finished the game, " + ChatColor.GOLD + " 30s " + ChatColor.GREEN + "left!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 300 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 15s " + ChatColor.GREEN + "left to finish!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 100 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 5!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 80 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 4!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 60 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 3!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 40 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 2!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 20 -> {
                                    for (BoatPlayer boatPlayer : participants) {
                                        if (!boatPlayer.inGame) return;
                                        Player player = boatPlayer.getPlayer();
                                        player.sendMessage(ChatColor.GOLD + " 1!");
                                        player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                                    }
                                }
                                case 0 -> {
                                    ticks = 0;
                                    gameState = GameState.FINISH;
                                }
                            }
                        }
                    }
                }

                if (gameState == GameState.FINISH) {
                    switch (timeLeft) {
                        case 0 -> {
                            for (BoatPlayer boatPlayer : participants) {
                                if (!boatPlayer.inGame) return;
                                Player player = boatPlayer.getPlayer();
                                player.sendTitle(ChatColor.RED + "Game Over", null, 0, 3, 1);
                                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                                player.sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Results\n" + ChatColor.RESET +
                                        ChatColor.GOLD + "1) " + ChatColor.RESET + participants.get(participants.size()-1).getPlayer().getDisplayName()
                                        + ChatColor.RESET + " - " + participants.get(participants.size()-1).time + "s");
                                if (participants.size() >= 2) player.sendMessage(
                                        ChatColor.GRAY + "2) " + ChatColor.RESET + participants.get(participants.size()-2).getPlayer().getDisplayName()
                                                + ChatColor.RESET + " - " + participants.get(participants.size()-2).time + "s");
                                if (participants.size() >= 3) player.sendMessage(
                                        ChatColor.of(new Color(205, 127, 50)) + "3) " + ChatColor.RESET + participants.get(participants.size()-3).getPlayer().getDisplayName()
                                                + ChatColor.RESET + " - " + participants.get(participants.size()-3).time +"s");
                            }
                        }
                        case 200 -> manager.disableGame(BoatRace.this);
                    }
                    timeLeft++;
                }

                ticks++;
            }
        };
        runnable.runTaskTimer(JetCore.getPlugin(), 0, 1);

    }

    @Override
    public void destroy() {
        participants.forEach(boatPlayer -> {
            boatPlayer.packetBoard.removePlayer(boatPlayer.getPlayer());
            boatPlayer.boat.remove();
            JetCore.getPlugin().sendToSpawn(boatPlayer.getPlayer());
        });
        runnable.cancel();
    }

    @Override
    public void addPlayer(Player player) {
        boolean found = false;
        for (BoatPlayer boatPlayer : participants) {
            if (boatPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                found = true;
                Boat boat = world.spawn(boatPlayer.lastLocation, Boat.class);
                boatPlayer.boat = boat;
                boatPlayer.inGame = true;
                player.teleport(world.getSpawnLocation());
                boat.addPassenger(player);
                boatPlayer.packetBoard.addPlayer(player);
            }
        }
        if (!found) {
            player.teleport(world.getSpawnLocation());
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void removePlayer(Player player) {
        for (BoatPlayer boatPlayer : participants) {
            if (boatPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                boatPlayer.lastLocation = boatPlayer.boat.getLocation();
                boatPlayer.boat.remove();
                boatPlayer.inGame = false;
                boatPlayer.packetBoard.removePlayer(player);
            }
        }
    }


    private static class BoatPlayer {

        private final UUID playerUUID;
        private final PacketBoard packetBoard = new PacketBoard();
        private int lap = 1;
        private int checkpoint = 0;
        private boolean finished = false;
        private double time = -1;
        private Boat boat = null;
        private Location lastLocation = null;
        private boolean inGame = true;

        public BoatPlayer(Player player) {
            this.playerUUID = player.getUniqueId();
            packetBoard.addPlayer(player);
            packetBoard.setDisplayName(ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "BoatRace board");
        }

        public Player getPlayer() {
            return Bukkit.getPlayer(playerUUID);
        }
    }
}
