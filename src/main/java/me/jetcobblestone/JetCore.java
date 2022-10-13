package me.jetcobblestone;

import lombok.Getter;
import me.jetcobblestone.ability.ItemAbility;
import me.jetcobblestone.ability.ItemAbilityManager;
import me.jetcobblestone.ability.ItemAbilityWrapper;
import me.jetcobblestone.game.GameManager;
import me.jetcobblestone.game.games.boat_race.BoatType;
import me.jetcobblestone.game.map.MapEditTracker;
import me.jetcobblestone.game.map.MapManager;
import me.jetcobblestone.permissions.PermissionProfile;
import me.jetcobblestone.permissions.PermissionsManager;
import me.jetcobblestone.util.gui.GuiManager;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** @noinspection unused*/
public class JetCore extends JavaPlugin implements Listener {

    @Getter private static JetCore plugin;
    @Getter private static ItemAbilityManager abilityManager;
    @Getter protected static MapManager mapManager;
    @Getter protected static GuiManager guiManager;

    public JetCore() {
        plugin = this;
    }


    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        ConfigurationSerialization.registerClass(Position.class);
        ConfigurationSerialization.registerClass(SerializablePair.class);
        guiManager = new GuiManager(this);
        abilityManager = new ItemAbilityManager(this);
        PermissionsManager.getInstance();

        try {
            mapManager = MapManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        //TEST BEGINS
        BoatType boatType;
        try {
            boatType = new BoatType("BoatRace", new ItemStack(Material.OAK_BOAT));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ItemAbility<PlayerInteractEvent> ability = new ItemAbility<>("test") {
            @Override
            public void onEvent(PlayerInteractEvent event) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    boatType.getGame(boatType.getGameMaps().get(1)).startGame(new ArrayList<>(Bukkit.getOnlinePlayers()));
                }
            }
        };

        final ItemStack itemStack = new ItemStack(Material.STICK, 1);
        final ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Special stick");
        itemStack.setItemMeta(meta);
        ItemAbilityWrapper abilityWrapper = abilityManager.wrapItem(itemStack);

        abilityWrapper.addAbility(PlayerInteractEvent.class, ability);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().setItem(0, itemStack);
        }
        //TEST ENDS

        /**
        new AbstractListener<PlayerJoinEvent>() {
            @Override
            @EventHandler
            public void onEvent(PlayerJoinEvent event) {
                if (!event.getPlayer().getName().equals("JetCobblestone")) {
                    PermissionProfile<Player> profile = PermissionsManager.getInstance().getProfile(event.getPlayer());
                    profile.blockPerm(EntityDamageByEntityEvent.class);
                }

                new NPC("Dream", event.getPlayer().getLocation(),
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0NjkwODc5NTAyNSwKICAicHJvZmlsZUlkIiA6ICIyMWUzNjdkNzI1Y2Y0ZTNiYjI2OTJjNGEzMDBhNGRlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZXlzZXJNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDU4ZWM3YzI0NGEzMjVkMzQyMjYyNjdjOGRhZjViMzJkZjkxYTZhNzY0NWU1NmY1MTllZjU4MWU0YTM5MWMzIgogICAgfQogIH0KfQ==",
                        "Jfhn8QliZD0iN43PeLulGHqU9c8mXTaN2W911LJ/dZVK4QJP51+UPMvw2mqvt4q1FwhUCQgWFqgD/7rRLzFR4nsphYjvjNSmlaRf0mw8MHkrHjTNOQS+VBo0u1A0YrSQmSh3nXBw1ofH52F1PG2+JXbnEYc7WLOKPC+AHoHEbyNokzy6ZOsFnyy1su8MxPYzAZVswRZO8VEB84c8oO1MBT6YG9lJBJkh5latBmtgnHwAaDdpVQmCb2hiSbx9mspoKIhg1kbo6Go1TiyNPUndmf/PrWi7d+bAkf96KGtciABj9VGhwUkSNKq8vWCL6739MwAD1EDXmvAJMV0N5/iVLFMvV6uDgbxEGThV6SoMiL3sPF+HGjYs1xfU12mmrQjo/yVTv7Dey9dBN/3XTnf3YovPPnYNylPc5EUt8FHpUMKGvIOAI57TdA75rfaMJRPpJLeqq44s3aLnc48TjQN8jaDXeHaauXRWLxQcVEt8faa+HqAOZtVyLV/3gNrbEzJYnEp7W++GhiFLe8QYKajFqBH3MRu++22oI1hO7lOXM9sYRYNjPrPXnNFhzFKOyTCanyiSKOU6x3loSVyVVfikqfLlSkdKWTBToS0DjN44K0LPnwkgWJxQl93aIR8QzUn489g0pXUlpUpaXyq4Kvp/OFdrI58uFy1WfZ1EY7RkgVI=")
                {

                    @Override
                    public void onRightClick(PlayerInteractEntityEvent event) {
                        event.getPlayer().sendMessage("<Dream> Nyaaa");
                    }
                };
            }
        };
         **/
    }


    @Override
    public void onDisable() {
        GameManager.getInstance().disableAll();
        MapEditTracker.getInstance().disable();
    }

    public void sendToSpawn(Player player) {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        PermissionProfile<Player> profile = PermissionsManager.getInstance().getProfile(player);
        profile.allowAll();
    }

    private final Set<Integer> boatSet = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if (!player.isInsideVehicle()) return;

        Entity vehicle = player.getVehicle();


        World world = player.getWorld();
        assert event.getTo() != null;
        Location location = vehicle.getLocation();
        Vector velocity = location.clone().subtract(event.getFrom()).toVector().setY(0);

        if (world.getBlockAt(vehicle.getLocation().subtract(0, 1, 0)).isEmpty()) return;

        //Performs raytracing
        RayTraceResult result;

        if (velocity.length() < 0.1) {
            result = world.rayTraceBlocks(location, location.getDirection(), 0.7, FluidCollisionMode.NEVER, true);
        } else {
            result = world.rayTraceBlocks(location, velocity, velocity.length() + 0.8, FluidCollisionMode.NEVER, true);
            if (result == null) {
                double similarSide = Math.abs(velocity.getX()) > Math.abs(velocity.getZ()) ? velocity.getX() : velocity.getZ();
                Vector offset = velocity.clone().rotateAroundY(90).multiply(0.675 / similarSide);
                result = world.rayTraceBlocks(location.clone().add(offset), velocity, velocity.length() + 0.8, FluidCollisionMode.NEVER, true);
                if (result == null) {
                    result = world.rayTraceBlocks(location.clone().subtract(offset), velocity, velocity.length() + 0.8, FluidCollisionMode.NEVER, true);
                }

            }
        }

        if (result != null) {
            if (result.getHitBlock().getType() != Material.PACKED_ICE && result.getHitBlock().getType() != Material.BLUE_ICE) return;
            if (!(world.getBlockAt(result.getHitBlock().getLocation().add(0, 1, 0)).getType().isAir())) return;

            net.minecraft.world.entity.Entity nmsVehicle = ((CraftEntity) vehicle).getHandle();
            nmsVehicle.setPos(location.getX(), location.getY() + 1.1, location.getZ());

            final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            final ServerPlayerConnection connection = serverPlayer.connection;
            connection.send(new ClientboundMoveVehiclePacket(nmsVehicle));


            if (boatSet.contains(vehicle.getEntityId())) return;
            BukkitRunnable runnable = new BukkitRunnable() {

                Location prevLoc = location;
                Vector oldVelocity = velocity;
                int count = 0;

                @Override
                public void run() {
                    Vector velocity = vehicle.getLocation().subtract(prevLoc).toVector().setY(0);
                    prevLoc = vehicle.getLocation();
                    if (velocity.length() < oldVelocity.length() * 0.7) {
                        vehicle.setVelocity(oldVelocity);
                    }
                    else {
                        oldVelocity = velocity;
                    }
                    count++;
                    if (count == 10) {
                        cancel();
                        boatSet.remove(vehicle.getEntityId());
                    }
                }
            };
            runnable.runTaskTimer(this, 1, 1);
            boatSet.add(vehicle.getEntityId());
        }

    }
}
