package me.jetcobblestone.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

//This class is the listener responsible for managing the crate creator GUI
public class GuiListener implements Listener{

	private final JavaPlugin plugin;
	private final GuiManager guiManager;

	public GuiListener(JavaPlugin plugin, GuiManager guiManager) {
		this.plugin = plugin;
		this.guiManager = guiManager;
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {
		final Inventory clickedInventory = event.getClickedInventory();
		final HumanEntity humanEntity = event.getWhoClicked();
		final ClickType click = event.getClick();
		final Gui gui = guiManager.getGui(humanEntity);
		if (clickedInventory == null) return;
		if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT || click == ClickType.DOUBLE_CLICK) {
			if (gui != null) {
				event.setCancelled(true);
			}
		}
		if (clickedInventory.equals(event.getView().getBottomInventory())) return;
		if (gui != null) {
			event.setCancelled(true);
			
			gui.clickItem(event.getSlot(), event);
			gui.runGuiAction(event);
		}

	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if (guiManager.getGui(event.getWhoClicked()) != null) {
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		final HumanEntity player = event.getPlayer();
		final Gui gui = guiManager.getGui(player);
		if (gui == null) return;
		//returns if the function returns true meaning the closing has been cancelled
		final Function<InventoryCloseEvent, Boolean> func = gui.getGuiCloseEvent();
		if (func != null && func.apply(event)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				player.openInventory(event.getView().getTopInventory());
			}, 1);
			return;
		}
		removePlayer(event.getPlayer());

	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}

	private void removePlayer(HumanEntity humanEntity) {
		final Gui gui = guiManager.getGui(humanEntity);
		if (gui == null) return;
		gui.removeViewer(humanEntity);
		guiManager.clearPlayer(humanEntity);
	}
}

