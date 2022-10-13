package me.jetcobblestone;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Position implements ConfigurationSerializable {

        private Vector vector;
        private float pitch;
        private float yaw;

        public Position(Vector vector, float pitch, float yaw) {
                this.vector = vector;
                this.pitch = pitch;
                this.yaw = yaw;
        }

        public static Position deserialize(Map<String, Object> map) {
                Vector vector = (Vector) map.get("vector");
                float pitch = (float) (double) map.get("pitch");
                float yaw = (float) (double) map.get("yaw");
                return new Position(vector, pitch, yaw);
        }

        public static Position fromLocation(Location location) {
                return new Position(location.toVector(), location.getPitch(), location.getYaw());
        }

        @NotNull
        @Override
        public Map<String, Object> serialize() {
                Map<String, Object> map = new HashMap<>();
                map.put("vector", vector);
                map.put("pitch", pitch);
                map.put("yaw", yaw);
                return map;
        }

        public Location toLocation(World world) {
                return vector.toLocation(world, yaw, pitch);
        }
}