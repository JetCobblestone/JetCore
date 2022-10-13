package me.jetcobblestone.ability;

import lombok.Getter;
import me.jetcobblestone.JetCore;
import org.bukkit.event.Event;

public abstract class ItemAbility<T extends Event>{

    @Getter private final String key;

    public ItemAbility(String key) {
        this.key = key;
        JetCore.getAbilityManager().registerAbility(this);
    }

    public abstract void onEvent(T event);

}
