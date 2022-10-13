package me.jetcobblestone.ability;

import com.google.common.collect.BiMap;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.nio.charset.Charset;
import java.util.*;

public class ItemAbilityWrapper {

    private final ItemStack item;
    private final ItemAbilityManager itemAbilityManager;
    private final Map<Class<? extends Event>, List<ItemAbility<?>>> eventAbilityMap = new HashMap<>();

    protected ItemAbilityWrapper(ItemStack item, ItemAbilityManager abilityManager) {
        this.item = item;
        this.itemAbilityManager = abilityManager;

        final Map<String, ItemAbility<?>> stringAbilityMap = itemAbilityManager.getAbilityMap();
        final BiMap<NamespacedKey, Class<? extends Event>> keyEventMap = itemAbilityManager.getEventMap().inverse();

        final PersistentDataContainer container = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();

        for (NamespacedKey key : container.getKeys()) {
            Class<? extends Event> eventClass = keyEventMap.get(key);
            if (eventClass == null) return;

            final String[] tags = container.get(key, new StringArrayItemTagType(Charset.defaultCharset()));
            if (tags != null) {
                for (String tag : tags) {
                    if (!stringAbilityMap.containsKey(tag)) continue;
                    List<ItemAbility<?>> abilityList = eventAbilityMap.computeIfAbsent(eventClass, k -> new ArrayList<>());
                    abilityList.add(stringAbilityMap.get(tag));
                }
            }
        }

    }

    public <T extends Event> void addAbility(Class<T> eventClass, ItemAbility<T> ability) {

        if (!eventAbilityMap.containsKey(eventClass)) {
            eventAbilityMap.put(eventClass, new ArrayList<>());
        }

        List<ItemAbility<?>> abilityList = eventAbilityMap.get(eventClass);
        abilityList.add(ability);

        final ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        final String[] tags = new String[abilityList.size()];

        NamespacedKey key = itemAbilityManager.getEventMap().get(eventClass);

        for (int i = 0; i < abilityList.size(); i++) {
            tags[i] = abilityList.get(i).getKey();
        }
        container.set(key, new StringArrayItemTagType(Charset.defaultCharset()), tags);

        item.setItemMeta(itemMeta);
    }

    public List<ItemAbility<?>> getAbilities(Class<? extends Event> eventClass) {
        return eventAbilityMap.get(eventClass);
    }
}
