package me.jetcobblestone;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.jetcobblestone.ability.AbstractListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public abstract class NPC extends ServerPlayer {

    public NPC(String name, Location loc, String texture, String signature) {
        super(((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld)loc.getWorld()).getHandle(),
                createProfile(name, texture, signature));

        //Set location and add the entity to the world
        setPos(loc.getX(), loc.getY(), loc.getZ());
        new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND), this);
        level.addFreshEntity(this);

        //Display on load
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(JetCore.getPlugin(), PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacket().getIntegers().getValues().get(0) == getId()) {
                    display(event.getPlayer());
                }
            }
        });

        for (Player player : Bukkit.getOnlinePlayers()) {
            addEntity(player);
        }

        //For players who join
        new AbstractListener<PlayerJoinEvent>(){
            @Override
            public void onEvent(PlayerJoinEvent event) {
                addEntity(event.getPlayer());
            }
        };

        //On right click
        new AbstractListener<PlayerInteractEntityEvent>() {
            @Override
            public void onEvent(PlayerInteractEntityEvent event) {
                Entity entity = event.getRightClicked();
                if (event.getHand().equals(EquipmentSlot.HAND) && uuid.equals(entity.getUniqueId())) {
                    onRightClick(event);
                }
            }
        };
    }

    public abstract void onRightClick(PlayerInteractEntityEvent event);

    private static GameProfile createProfile(String name, String texture, String signature) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        PropertyMap propertyMap = profile.getProperties();
        propertyMap.removeAll("textures");
        propertyMap.put("textures", new Property("textures", texture, signature));
        return profile;
    }

    public void addEntity(Player player) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final ServerPlayerConnection connection = serverPlayer.connection;
        connection.send(new ClientboundAddPlayerPacket(NPC.this));
    }

    public void display(Player player) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final ServerPlayerConnection connection = serverPlayer.connection;
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER,  NPC.this));
        Bukkit.getScheduler().scheduleSyncDelayedTask(JetCore.getPlugin(),
                () -> connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,  NPC.this)),
                1);


        final byte skinFixByte = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        final EntityDataSerializer<Byte> registry = EntityDataSerializers.BYTE;
        entityData.set(registry.createAccessor(17), skinFixByte);

        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(
                getId(),
                entityData,
                false
        );
        connection.send(metadataPacket);
    }
}
