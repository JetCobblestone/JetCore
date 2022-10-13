package me.jetcobblestone.projectile;

import lombok.Getter;
import me.jetcobblestone.JetCore;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class StraightProjectile extends Projectile{

    @Getter private final double step;

    public StraightProjectile(Location loc, Vector velocity, double step) {
        super(loc, velocity);
        this.step = step;
        project();
    }

    protected abstract boolean filterEntities(Entity entity);

    @Override
    protected void project() {
        new BukkitRunnable(){
            RayTraceResult result = null;
            float distance = 0;
            float remainder = 0;

            @Override
            public void run() {
                final Vector stepVector = velocity.clone().normalize().multiply(step);
                remainder += velocity.length() / (20 * step);
                int numSteps = (int) remainder;
                remainder -= numSteps;

                while (numSteps > 0) {
                    result = loc.getWorld().rayTrace(
                            loc,
                            stepVector,
                            step,
                            FluidCollisionMode.NEVER,
                            true,
                            0.1,
                            entity -> filterEntities(entity));
                    if (result == null) {
                        move();
                        loc.add(stepVector);
                        numSteps--;
                        distance += step;
                    }
                    else {
                        numSteps = 0;
                        loc.setX(result.getHitPosition().getX());
                        loc.setY(result.getHitPosition().getY());
                        loc.setZ(result.getHitPosition().getZ());
                        distance += result.getHitPosition().subtract(loc.toVector()).length();
                        cancel();
                    }
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                collide();
            }

        }.runTaskTimer(JetCore.getPlugin(), 0, 1);
    }

}
