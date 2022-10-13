package me.jetcobblestone;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SerializablePair<L extends ConfigurationSerializable, R extends ConfigurationSerializable> implements ConfigurationSerializable {

    @Getter @Setter private L left;
    @Getter @Setter private R right;

    public SerializablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L extends ConfigurationSerializable,R extends ConfigurationSerializable> SerializablePair<L,R> deserialize(Map<String, Object> map) {
        L left = (L) map.get("left");
        R right = (R) map.get("right");
        return new SerializablePair<>(left, right);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("left", left);
        map.put("right", right);
        return map;
    }
}
