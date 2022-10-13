package me.jetcobblestone.util.gui;


import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonalisedGui {

    private final Map<Player, Gui> guiMap = new HashMap<>();
    private final Gui defaultGui;

    public PersonalisedGui(String name, int rows, GuiManager guiManager) {
        defaultGui = new Gui(name, rows, guiManager);
    }

    public Gui getGui(Player player) {
        final Gui gui = guiMap.get(player);
        if (gui == null) {
            return defaultGui;
        }
        return gui;
    }

    public void open(Player player) {
        getGui(player).open(player);
    }

    public void setSpecific(int slot, GuiItem guiItem, Set<Player> players) {
        if (players != null) {
            for (Player player : players) {
                Gui gui = guiMap.get(player);
                if (gui == null) {
                    gui = defaultGui.clone();
                    guiMap.put(player, gui);
                }
                gui.setItem(slot, guiItem);
                gui.refresh();
            }
        }
    }

    public void setExcept(int slot, GuiItem guiItem, Set<Player> exceptions) {
        if (exceptions != null) {
            for (Map.Entry<Player, Gui> entry : guiMap.entrySet()) {
                if (exceptions.contains(entry.getKey())) {
                    continue;
                }
                entry.getValue().setItem(slot, guiItem);
            }
        }

        defaultGui.setItem(slot, guiItem);
    }

    public void addSpecific(GuiItem guiItem, Set<Player> players) {
        if (players != null) {
            for (Player player : players) {
                Gui gui = guiMap.get(player);
                if (gui == null) {
                    gui = defaultGui.clone();
                    guiMap.put(player, gui);
                }
                gui.addItem(guiItem);
                gui.refresh();
            }
        }
    }

    public void addExcept(GuiItem guiItem, Set<Player> exceptions) {
        if (exceptions != null) {
            for (Map.Entry<Player, Gui> entry : guiMap.entrySet()) {
                if (exceptions.contains(entry.getKey())) {
                    continue;
                }
                entry.getValue().addItem(guiItem);
            }
        }

        defaultGui.addItem(guiItem);
    }

    public void removeItem(GuiItem guiItem, Set<Player> exceptions) {
        for (Map.Entry<Player, Gui> entry : guiMap.entrySet()) {
            if (exceptions != null && exceptions.contains(entry.getKey())) {
                continue;
            }
            entry.getValue().remove(guiItem);
        }

        defaultGui.remove(guiItem);
    }
}
