package me.jetcobblestone.util.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {


	private final Map<UUID, Gui> guiLink = new HashMap<>();

	public GuiManager(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new GuiListener(plugin,this), plugin);
	}

	protected void addGui(HumanEntity humanEntity, Gui gui) {
		guiLink.put(humanEntity.getUniqueId(), gui);
	}
	
	public Gui getGui(HumanEntity humanEntity) {
		return guiLink.get(humanEntity.getUniqueId());
	}
	
	public void clearPlayer(HumanEntity humanEntity) {
		guiLink.remove(humanEntity.getUniqueId());
	}
	
}
