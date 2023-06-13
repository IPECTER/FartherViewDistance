package xuan.cat.fartherviewdistance.code.branch.v1_19_R3;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xuan.cat.fartherviewdistance.api.branch.BranchChunk;
import xuan.cat.fartherviewdistance.api.branch.BranchChunkLight;
import xuan.cat.fartherviewdistance.api.branch.BranchPacket;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.function.Consumer;

public final class Packet implements BranchPacket {
    private final PacketHandleLightUpdate handleLightUpdate = new PacketHandleLightUpdate();
    private final LevelLightEngine noOpLevelLightEngine = new LevelLightEngine(new LightChunkGetter() {
        public BlockGetter getChunkForLighting(int chunkX, int chunkZ) {
            return null;
        }

        public BlockGetter getLevel() {
            return null;
        }
    }, false, false) {
        public int getLightSectionCount() {
            return 0;
        }
    };

    private Field chunkPacketLightDataField;

    {
        try {
            chunkPacketLightDataField = ClientboundLevelChunkWithLightPacket.class.getDeclaredField("d");
            chunkPacketLightDataField.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException | InaccessibleObjectException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Player player, net.minecraft.network.protocol.Packet<?> packet) {
        try {
            Connection container = ((CraftPlayer) player).getHandle().connection.connection;
            container.send(packet);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void sendViewDistance(Player player, int viewDistance) {
        sendPacket(player, new ClientboundSetChunkCacheRadiusPacket(viewDistance));
    }

    public void sendUnloadChunk(Player player, int chunkX, int chunkZ) {
        sendPacket(player, new ClientboundForgetLevelChunkPacket(chunkX, chunkZ));
    }

    public Consumer<Player> sendChunkAndLight(Player player, BranchChunk chunk, BranchChunkLight light, boolean needTile, Consumer<Integer> consumeTraffic) {
        FriendlyByteBuf serializer = new FriendlyByteBuf(Unpooled.buffer().writerIndex(0));
        this.handleLightUpdate.write(serializer, (ChunkLight) light, true);
        consumeTraffic.accept(serializer.readableBytes());
        ClientboundLightUpdatePacketData lightData = new ClientboundLightUpdatePacketData(serializer, chunk.getX(), chunk.getZ());
        LevelChunk levelChunk = ((Chunk) chunk).getLevelChunk();
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(levelChunk, noOpLevelLightEngine, null, null, false, levelChunk.getLevel().chunkPacketBlockController.shouldModify(serverPlayer, levelChunk));
        try {
            chunkPacketLightDataField.set(packet, lightData);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (p) -> sendPacket(p, packet);
    }

    public void sendKeepAlive(Player player, long id) {
        sendPacket(player, new ClientboundKeepAlivePacket(id));
    }
}
