package me.jetcobblestone.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.List;

//The purpose of this class it to make it easy to create the ItemStacks needed for GUIs, as well as call the item loading functions from every GUI.

public class ItemFactory {
	
	public static ItemStack createItem(String name, Material material, List<String> lore) {
		
		final ItemStack item = new ItemStack(material);
		final ItemMeta meta = item.getItemMeta();
		assert meta != null;
		if (name != null) {
			meta.setDisplayName(name);
		}
		if (lore != null) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack createItem(String name, Material material) {
		return createItem(name, material, null);
	}

	public static ItemStack setItemMeta(ItemStack item, List<String> lore) {
		final ItemStack itemStack = item.clone();
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	
}
