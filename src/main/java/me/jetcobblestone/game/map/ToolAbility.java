package me.jetcobblestone.game.map;

import me.jetcobblestone.ability.ItemAbility;
import me.jetcobblestone.game.GameType;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public abstract class ToolAbility<T extends Event & Cancellable> extends ItemAbility<T> {

    private final Function<T, Player> getPlayer;
    private final Function<T, ItemStack> getItem;
    private final GameType<?,?> gameType;

    public ToolAbility(String key, GameType<?,?> gameType, Function<T, Player> playerFunction, Function<T, ItemStack> itemFunction) {
        super(key);
        this.gameType = gameType;
        this.getPlayer = playerFunction;
        this.getItem = itemFunction;
    }

    @Override
    public final void onEvent (T event) {
        Player player = getPlayer.apply(event);
        GameMap<?> gameMap = MapEditTracker.getInstance().getEditedMap(player);
        if (gameMap == null || gameMap.getGameType() != gameType) {
            event.setCancelled(true);
            player.getInventory().removeItem(getItem.apply(event));
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
            player.sendMessage(ChatColor.RED + "That can only be used on " + gameType.getName());
            return;
        }
        onTrigger(event);
    }

    public abstract void onTrigger (T event);

}
