package me.jetcobblestone.permissions;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PermissionProfile<E extends Entity> {

    @Getter private final UUID entityUUID;
    @Getter private final Map<Class<? extends Event>, Consumer<? extends Event>> permissions = new HashMap<>();

    public PermissionProfile(E entity) {
        entityUUID = entity.getUniqueId();
    }

    public E getEntity() {
        //noinspection unchecked
        return (E) Bukkit.getEntity(entityUUID);
    }

    public <T extends Event> void blockPerm(Class<T> eventClass) {
        permissions.put(eventClass, null);
    }

    public <T extends Event> void blockPerm(Class<T> eventClass, Consumer<T> trigger) {
        permissions.put(eventClass, trigger);
    }

    public <T extends Event> void allowPerm(Class<T> eventClass) {
        permissions.remove(eventClass);
    }

    public void allowAll() {
        permissions.clear();
    }

}
