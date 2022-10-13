package me.jetcobblestone;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Area {
    @Getter private final Vector loc1;
    @Getter private final Vector loc2;
    private final Location centre;
    private final World world;

    public Area(Vector loc1, Vector loc2) {
        this(loc1, loc2, Bukkit.getWorlds().get(0));
    }

    public Area(Vector loc1, Vector loc2, World world) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.world = world;
        centre = loc1.clone().add(loc2).multiply(0.5).toLocation(world);
    }

    public Area clone(World world) {
        return new Area(loc1.clone(), loc2.clone(), world);
    }

    public boolean contains(Entity entity) {
        Location loc = entity.getLocation();
        //noinspection ConstantConditions
        if (!loc.getWorld().equals(world)) return false;
        if (loc.getX() < loc1.getX() && loc.getX() < loc2.getX() || loc.getX() > loc1.getX() && loc.getX() > loc2.getX()) return false;
        if (loc.getY() < loc1.getY() && loc.getY() < loc2.getY() || loc.getY() > loc1.getY() && loc.getY() > loc2.getY()) return false;
        if (loc.getZ() < loc1.getZ() && loc.getZ() < loc2.getZ() || loc.getZ() > loc1.getZ() && loc.getZ() > loc2.getZ()) return false;
        return true;
    }

    public double getDistance(Entity entity) {
        return entity.getLocation().distance(centre);
    }

}
