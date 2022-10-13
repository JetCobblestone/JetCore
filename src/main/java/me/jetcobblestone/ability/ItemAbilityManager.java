package me.jetcobblestone.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAbilityManager implements Listener {

    @Getter private final Map<String, ItemAbility<?>> abilityMap = new HashMap<>();
    @Getter private final BiMap<Class<? extends Event>, NamespacedKey> eventMap = HashBiMap.create();

    public ItemAbilityManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        eventMap.put(PlayerInteractEvent.class, new NamespacedKey(plugin, PlayerInteractEvent.class.getName()));
        eventMap.put(PlayerDropItemEvent.class, new NamespacedKey(plugin, PlayerDropItemEvent.class.getName()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        /** To show Alice
        ItemAbilityWrapper itemAbilityWrapper = wrapItem(item);
        for (ItemAbility<?> itemAbility : itemAbilityWrapper.getAbilities(PlayerInteractEvent.class)) {
            //noinspection unchecked
            ((ItemAbility<PlayerInteractEvent>) itemAbility).onEvent(event);
        }
         **/

        onEvent(event, item);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        onEvent(event, item);
    }

    private <T extends Event> void onEvent(T event, ItemStack item) {
        ItemAbilityWrapper itemAbilityWrapper = wrapItem(item);
        List<ItemAbility<?>> list = itemAbilityWrapper.getAbilities(event.getClass());
        if (list == null) return;
        for (ItemAbility<?> itemAbility : list) {
            //noinspection unchecked
            ((ItemAbility<T>) itemAbility).onEvent(event);
        }
    }


    public ItemAbilityWrapper wrapItem(ItemStack itemStack) {
        return new ItemAbilityWrapper(itemStack, this);
    }

    public void registerAbility(ItemAbility<?> ability) {
        final String key = ability.getKey();
        if (abilityMap.containsKey(key)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Tried to register an ability with the pre-existing key " + key);
            return;
        }
        abilityMap.put(key, ability);
    }

}
