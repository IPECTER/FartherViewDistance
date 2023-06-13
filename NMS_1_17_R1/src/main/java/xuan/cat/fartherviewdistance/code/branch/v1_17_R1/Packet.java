package xuan.cat.fartherviewdistance.code.branch.v1_17_R1;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xuan.cat.fartherviewdistance.api.branch.BranchChunk;
import xuan.cat.fartherviewdistance.api.branch.BranchChunkLight;
import xuan.cat.fartherviewdistance.api.branch.BranchPacket;

import java.util.function.Consumer;

public final class Packet implements BranchPacket {
    private final PacketHandleChunk handleChunk = new PacketHandleChunk();
    private final PacketHandleLightUpdate handleLightUpdate = new PacketHandleLightUpdate();

    public void sendPacket(Player player, net.minecraft.network.protocol.Packet<?> packet) {
        try {
            PlayerConnection container = ((CraftPlayer) player).getHandle().b;
            container.sendPacket(packet);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void sendViewDistance(Player player, int viewDistance) {
        sendPacket(player, new PacketPlayOutViewDistance(viewDistance));
    }

    public void sendUnloadChunk(Player player, int chunkX, int chunkZ) {
        sendPacket(player, new PacketPlayOutUnloadChunk(chunkX, chunkZ));
    }

    public Consumer<Player> sendChunkAndLight(Player player, BranchChunk chunk, BranchChunkLight light, boolean needTile, Consumer<Integer> consumeTraffic) {
        PacketPlayOutMapChunk packetChunk = handleChunk.createMapChunkPacket(chunk.getChunk(), needTile, consumeTraffic);
        PacketPlayOutLightUpdate packetLight = handleLightUpdate.createLightUpdatePacket(chunk.getX(), chunk.getZ(), (ChunkLight) light, true, consumeTraffic);
        try {
            // 適用於 paper
            packetChunk.setReady(true);
        } catch (NoSuchMethodError noSuchMethodError) {
            // 適用於 spigot (不推薦)
        }
        return (p) -> {
            sendPacket(p, packetLight);
            sendPacket(p, packetChunk);
        };
    }

    public void sendKeepAlive(Player player, long id) {
        sendPacket(player, new PacketPlayOutKeepAlive(id));
    }

}
