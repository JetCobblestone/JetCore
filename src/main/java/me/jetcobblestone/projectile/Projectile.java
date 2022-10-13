package me.jetcobblestone.projectile;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Projectile {

    protected Location loc;
    protected Vector velocity;

    public Projectile(Location loc, Vector velocity) {
        this.loc = loc;
        this.velocity = velocity;
    }

    //<editor-fold desc="Setters">
    public Projectile setLoc(Location loc) {
        this.loc = loc;
        return this;
    }

    public Projectile setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }
    //</editor-fold>

    protected abstract void project();
    protected abstract void move();
    protected abstract void collide();

}
