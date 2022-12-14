package me.jetcobblestone.util.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;

import me.jetcobblestone.util.packets.WrapperPlayServerEntityMetadata;
import me.jetcobblestone.util.packets.WrapperPlayServerNamedEntitySpawn;
import me.jetcobblestone.util.packets.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TabManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<Integer, PlayerInfoData> updateMap = new HashMap<>();

    private int size = 0;

    public TabManager(JavaPlugin plugin) {
        this.plugin = plugin;
        final WrapperPlayServerPlayerInfo removePlayersPacket = new WrapperPlayServerPlayerInfo();
        removePlayersPacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        final List<PlayerInfoData> toRemove = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            toRemove.add(getPlayerInfo(p));
        }

        removePlayersPacket.setData(toRemove);
        removePlayersPacket.sendPacketAll();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getLogger().info("Packet triggered");
                final WrapperPlayServerNamedEntitySpawn wrapper = new WrapperPlayServerNamedEntitySpawn(event.getPacket());
                final Player packetReceiver = event.getPlayer();
                final Player spawningIn = Bukkit.getPlayer(wrapper.getPlayerUUID());
                assert spawningIn != null;


                final WrapperPlayServerPlayerInfo addPlayerPacket = new WrapperPlayServerPlayerInfo();
                addPlayerPacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                final List<PlayerInfoData> dataList = new ArrayList<>();
                dataList.add(getPlayerInfo(spawningIn));
                addPlayerPacket.setData(dataList);
                addPlayerPacket.sendPacket(packetReceiver);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    final WrapperPlayServerPlayerInfo removePlayerPacket = new WrapperPlayServerPlayerInfo();
                    removePlayerPacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    removePlayerPacket.setData(dataList);
                    removePlayerPacket.sendPacket(packetReceiver);
                }, 1);

            }
        });
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            final Player player = event.getPlayer();

            WrapperPlayServerPlayerInfo removePlayersPacket = new WrapperPlayServerPlayerInfo();
            removePlayersPacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

            final List<PlayerInfoData> toRemove = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                toRemove.add(getPlayerInfo(p));
            }

            removePlayersPacket.setData(toRemove);
            removePlayersPacket.sendPacket(player);

            removePlayersPacket = new WrapperPlayServerPlayerInfo();
            removePlayersPacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            toRemove.clear();
            toRemove.add(getPlayerInfo(player));
            removePlayersPacket.setData(toRemove);
            removePlayersPacket.sendPacketAll();

            final WrapperPlayServerPlayerInfo addPlayers = new WrapperPlayServerPlayerInfo();
            addPlayers.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            final List<PlayerInfoData> toAdd = new ArrayList<>();

            for (int i = 1; i <= size; i++) {
                final PlayerInfoData mapData = updateMap.get(i);
                if (mapData != null) {
                    toAdd.add(mapData);
                    continue;
                }
                toAdd.add(getFromColour(" ", i, ChatColor.GRAY));
            }

            addPlayers.setData(toAdd);
            addPlayers.sendPacket(player);
        }, 1);
    }


    public void setSize(int i) {
        if (i > 80 || i < 0) {
            Bukkit.getLogger().severe("Tab menu can not be set to a size greater than 80 or less than 0 (" + i + ")");
            Thread.dumpStack();
            return;
        }
        if (i == size) return;
        if (i < size) {
            final WrapperPlayServerPlayerInfo removePacket = new WrapperPlayServerPlayerInfo();
            removePacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            final List<PlayerInfoData> toRemove = new ArrayList<>();

            for (int x = i + 1; x <= size; x++) {
                final PlayerInfoData mapData = updateMap.get(x);
                toRemove.add(mapData);
                updateMap.put(x, null);
            }

            removePacket.setData(toRemove);
            removePacket.sendPacketAll();
        }
        else {
            final WrapperPlayServerPlayerInfo addPacket = new WrapperPlayServerPlayerInfo();
            addPacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            final List<PlayerInfoData> toAdd = new ArrayList<>();
            for (int x = size + 1; x <= i; x++) {
                final PlayerInfoData data = getFromColour(" ", x, ChatColor.GRAY);
                updateMap.put(x, data);
                toAdd.add(data);
            }
            addPacket.setData(toAdd);
            addPacket.sendPacketAll();
        }

        size = i;
    }

    public void set(int i, PlayerInfoData data) {
        if (i > size || i <= 0) {
            Bukkit.getLogger().severe("Attempted to set player in tab menu outside of the tab menu's range (" + i + ", " + size + ")");
            Thread.dumpStack();
            return;
        }

        final WrapperPlayServerPlayerInfo removePacket = new WrapperPlayServerPlayerInfo();
        removePacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        final List<PlayerInfoData> toRemove = new ArrayList<>();
        toRemove.add(updateMap.get(i));
        removePacket.setData(toRemove);
        removePacket.sendPacketAll();

        final WrapperPlayServerPlayerInfo updatePacket = new WrapperPlayServerPlayerInfo();
        updatePacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        final List<PlayerInfoData> toAdd = new ArrayList<>();
        toAdd.add(data);
        updatePacket.setData(toAdd);
        updatePacket.sendPacketAll();

        updateMap.put(i, data);
    }

    public void set(String name, int i, Player player) {
        set(i, createFakePlayer(name, i, player));

        final WrapperPlayServerEntityMetadata metaDataPacket = new WrapperPlayServerEntityMetadata();
        final WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(player);
        metaDataPacket.setMetadata(dataWatcher.getWatchableObjects());
        metaDataPacket.setEntityID(player.getEntityId());
        metaDataPacket.sendPacketAll();
    }

    public void clear(int i) {
        set(i, getFromColour(" ", i, ChatColor.GRAY));
    }


    public PlayerInfoData get(int i) {
        return updateMap.get(i);
    }


    public static PlayerInfoData getFromColour(String name, int i, ChatColor colour) {
        final Pair<String, String> skin = ColourMapper.getSkinfromColour(colour);
        if (skin == null) {
            Bukkit.getLogger().severe("Could not get skin data of " + colour.name());
            Thread.dumpStack();
            return null;
        }
        return createFakeData(name, i, skin.getFirst(), skin.getSecond());
    }

    public static PlayerInfoData createFakePlayer(String name, int i, Player player) {
        return createFakeData(name, i, WrappedGameProfile.fromPlayer(player).getProperties().get("textures").iterator().next());
    }

    public static PlayerInfoData createFakeData(String name, int i, String texture, String signature) {
        return createFakeData(name, i, new WrappedSignedProperty("textures", texture, signature));
    }

    public static PlayerInfoData createFakeData(String display, int i, WrappedSignedProperty property) {

        final UUID uuid = UUID.randomUUID();

        final WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, formatNum(i));
        gameProfile.getProperties().put("textures", property);

        return new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(display));
    }

    private static PlayerInfoData getPlayerInfo(Player player) {
        int latency = 0;

        try {
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            latency = (int) handle.getClass().getDeclaredField("ping")
                    .get(handle);
        } catch (IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException
                | NoSuchFieldException ignored) {
        }

        return new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                latency,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getDisplayName())
        );
    }

    public static String formatNum(int num) {
        final StringBuilder ret = new StringBuilder(Integer.toString(num));
        for (int i = 0; i < (2 - ret.length()); i++) {
            ret.insert(0, "0");
        }
        ret.insert(0, "!");
        return ret.toString();
    }
}
