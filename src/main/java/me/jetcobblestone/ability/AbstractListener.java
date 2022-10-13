package me.jetcobblestone.ability;

import me.jetcobblestone.JetCore;
import org.bukkit.Bukkit;
import org.bukkit.event.*;

public abstract class AbstractListener<T extends Event> implements Listener {

    public AbstractListener() {
        Bukkit.getPluginManager().registerEvents(this, JetCore.getPlugin());
    }

    public AbstractListener(Class<T> eventClass) {
        this(eventClass, EventPriority.NORMAL);
    }

    public AbstractListener(Class<T> eventClass, EventPriority priority) {

        Bukkit.getPluginManager().registerEvent(
                eventClass,
                this,
                priority,
                (x, evt) -> {
                    try {
                        onEvent(eventClass.cast(evt));
                    }
                    catch (ClassCastException ignored) { }
                },
                JetCore.getPlugin());
    }

    @EventHandler
    public abstract void onEvent(T event);

    public void deRegister() {
        HandlerList.unregisterAll(this);
    }
}
