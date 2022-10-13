package me.jetcobblestone.util.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

public class GuiItem {
	private final ItemStack item;
	private final Consumer<InventoryClickEvent> clickEvent;

	public GuiItem(ItemStack item, Consumer<InventoryClickEvent> clickEvent) {
		this.item = item;
		this.clickEvent = clickEvent;
	}
	
	public ItemStack getItem() {
		return item.clone();
	}
	
	public void click(InventoryClickEvent event) {
		if (clickEvent == null || event == null) return;
		clickEvent.accept(event);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GuiItem guiItem = (GuiItem) o;
		return item.equals(guiItem.item) && clickEvent.equals(guiItem.clickEvent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(item, clickEvent);
	}

	public GuiItem clone() {
		return new GuiItem(item.clone(), clickEvent);
	}
}
