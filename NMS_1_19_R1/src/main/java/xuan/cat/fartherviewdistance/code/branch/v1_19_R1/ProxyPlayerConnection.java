package xuan.cat.fartherviewdistance.code.branch.v1_19_R1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xuan.cat.fartherviewdistance.api.branch.packet.PacketKeepAliveEvent;
import xuan.cat.fartherviewdistance.api.branch.packet.PacketMapChunkEvent;
import xuan.cat.fartherviewdistance.api.branch.packet.PacketUnloadChunkEvent;
import xuan.cat.fartherviewdistance.api.branch.packet.PacketViewDistanceEvent;

import java.lang.reflect.Field;

public final class ProxyPlayerConnection {
    private static Field field_ClientboundForgetLevelChunkPacket_chunkX;
    private static Field field_ClientboundForgetLevelChunkPacket_chunkZ;
    private static Field field_ClientboundSetChunkCacheRadiusPacket_distance;
    private static Field field_ClientboundLevelChunkWithLightPacket_chunkX;
    private static Field field_ClientboundLevelChunkWithLightPacket_chunkZ;

    static {
        try {
            field_ClientboundForgetLevelChunkPacket_chunkX = ClientboundForgetLevelChunkPacket.class.getDeclaredField("a");
            field_ClientboundForgetLevelChunkPacket_chunkZ = ClientboundForgetLevelChunkPacket.class.getDeclaredField("b");
            field_ClientboundSetChunkCacheRadiusPacket_distance = ClientboundSetChunkCacheRadiusPacket.class.getDeclaredField("a");
            field_ClientboundLevelChunkWithLightPacket_chunkX = ClientboundLevelChunkWithLightPacket.class.getDeclaredField("a");
            field_ClientboundLevelChunkWithLightPacket_chunkZ = ClientboundLevelChunkWithLightPacket.class.getDeclaredField("b");
            field_ClientboundForgetLevelChunkPacket_chunkX.setAccessible(true);
            field_ClientboundForgetLevelChunkPacket_chunkZ.setAccessible(true);
            field_ClientboundSetChunkCacheRadiusPacket_distance.setAccessible(true);
            field_ClientboundLevelChunkWithLightPacket_chunkX.setAccessible(true);
            field_ClientboundLevelChunkWithLightPacket_chunkZ.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean read(Player player, Packet<?> packet) {
        if (packet instanceof ServerboundKeepAlivePacket) {
            PacketKeepAliveEvent event = new PacketKeepAliveEvent(player, ((ServerboundKeepAlivePacket) packet).getId());
            Bukkit.getPluginManager().callEvent(event);
            return !event.isCancelled();
        } else {
            return true;
        }
    }

    public static boolean write(Player player, Packet<?> packet) {
        try {
            if (packet instanceof ClientboundForgetLevelChunkPacket) {
                PacketUnloadChunkEvent event = new PacketUnloadChunkEvent(player, field_ClientboundForgetLevelChunkPacket_chunkX.getInt(packet), field_ClientboundForgetLevelChunkPacket_chunkZ.getInt(packet));
                Bukkit.getPluginManager().callEvent(event);
                return !event.isCancelled();
            } else if (packet instanceof ClientboundSetChunkCacheRadiusPacket) {
                PacketViewDistanceEvent event = new PacketViewDistanceEvent(player, field_ClientboundSetChunkCacheRadiusPacket_distance.getInt(packet));
                Bukkit.getPluginManager().callEvent(event);
                return !event.isCancelled();
            } else if (packet instanceof ClientboundLevelChunkWithLightPacket) {
                PacketMapChunkEvent event = new PacketMapChunkEvent(player, field_ClientboundLevelChunkWithLightPacket_chunkX.getInt(packet), field_ClientboundLevelChunkWithLightPacket_chunkZ.getInt(packet));
                Bukkit.getPluginManager().callEvent(event);
                return !event.isCancelled();
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }
}