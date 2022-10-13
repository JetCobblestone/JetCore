package me.jetcobblestone.permissions;

import me.jetcobblestone.ability.AbstractListener;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftVehicle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PermissionsManager {

    private static PermissionsManager instance;

    public static PermissionsManager getInstance() {
        if (instance == null) {
            instance = new PermissionsManager();
        }
        return instance;
    }

    private final Map<UUID, PermissionProfile<?>> profileMap = new HashMap<>();

    public <T extends Entity> PermissionProfile<T> getProfile(T entity) {
        //noinspection unchecked
        PermissionProfile<T> profile = (PermissionProfile<T>) profileMap.get(entity.getUniqueId());
        if (profile == null) {
            profile = new PermissionProfile<>(entity);
            profileMap.put(entity.getUniqueId(), profile);
        }
        return profile;
    }

    private PermissionsManager(){

        addPermission(EntityDamageByEntityEvent.class, (event -> {
            if (event.getEntityType() == EntityType.PLAYER) return event.getDamager();
            return null;
        }));
        addPermission(FoodLevelChangeEvent.class, (event -> {
            if (event.getEntityType() == EntityType.PLAYER) return event.getEntity();
            return null;
        }));
        addPermission(BlockBreakEvent.class, BlockBreakEvent::getPlayer);
        addPermission(EntityDismountEvent.class, EntityDismountEvent::getEntity);


        addPermission(VehicleExitEvent.class, VehicleExitEvent::getExited);

        addPermission(PlayerMoveEvent.class, PlayerMoveEvent::getPlayer, event -> {
            Location location = event.getFrom().setDirection(event.getTo().getDirection());
            event.getPlayer().teleport(location);
        });

        addPermission(VehicleMoveEvent.class, event -> {
            if (event.getVehicle().getPassengers().size() < 1) return null;
            return event.getVehicle().getPassengers().get(0);
        }, event -> {
            Location loc = event.getFrom();
            net.minecraft.world.entity.Entity vehicle = ((CraftVehicle) event.getVehicle()).getHandle();
            vehicle.setPos(loc.getX(), loc.getY(), loc.getZ());
            vehicle.setYRot(loc.getYaw());

            List<Entity> passengers = event.getVehicle().getPassengers();
            if (passengers.size() < 1) return;

            Entity entity = passengers.get(0);
            if (!(entity instanceof Player player)) return;

            final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            final ServerPlayerConnection connection = serverPlayer.connection;
            connection.send(new ClientboundMoveVehiclePacket(vehicle));
        });

    }


    public <T extends Event & Cancellable> void addPermission(Class<T> eventClass, Function<T, Entity> getEntity) {
        addPermission(eventClass, getEntity, EventPriority.NORMAL);
    }

    public <T extends Event & Cancellable> void addPermission(Class<T> eventClass, Function<T, Entity> getEntity, EventPriority eventPriority) {
        addPermission(eventClass, getEntity, (event) -> event.setCancelled(true), eventPriority);
    }

    public <T extends Event> void addPermission(Class<T> eventClass, Function<T, Entity> getEntity, Consumer<T> onCancel) {
        addPermission(eventClass, getEntity, onCancel, EventPriority.NORMAL);
    }

    public <T extends Event> void addPermission(Class<T> eventClass, Function<T, Entity> getEntity, Consumer<T> onCancel, EventPriority priority) {

        new AbstractListener<>(eventClass, priority) {
            @Override
            @EventHandler
            public void onEvent(T event) {
                Entity entity = getEntity.apply(event);
                if (entity == null) return;
                Map<Class<? extends Event>, Consumer<? extends Event>> permissions = getProfile(getEntity.apply(event)).getPermissions();

                if (!permissions.containsKey(event.getClass())) return;

                //noinspection unchecked
                Consumer<T> consumer = (Consumer<T>) permissions.get(event.getClass());
                if (consumer != null) {
                    consumer.accept(event);
                    return;
                }
                onCancel.accept(event);
            }
        };
    }
}
