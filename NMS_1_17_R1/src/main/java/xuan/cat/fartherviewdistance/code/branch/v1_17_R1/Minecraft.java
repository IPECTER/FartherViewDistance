package xuan.cat.fartherviewdistance.code.branch.v1_17_R1;

import io.netty.channel.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.*;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkEmpty;
import net.minecraft.world.level.chunk.IChunkAccess;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xuan.cat.fartherviewdistance.api.branch.BranchChunk;
import xuan.cat.fartherviewdistance.api.branch.BranchChunkLight;
import xuan.cat.fartherviewdistance.api.branch.BranchMinecraft;
import xuan.cat.fartherviewdistance.api.branch.BranchNBT;

import java.io.IOException;

public final class Minecraft implements BranchMinecraft {
    public BranchNBT getChunkNBTFromDisk(World world, int chunkX, int chunkZ) throws IOException {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        ChunkProviderServer providerServer = worldServer.getChunkProvider();
        PlayerChunkMap playerChunkMap = providerServer.a;
        NBTTagCompound nbtTagCompound = playerChunkMap.read(new ChunkCoordIntPair(chunkX, chunkZ));
        return nbtTagCompound != null ? new NBT(nbtTagCompound) : null;
    }

    public BranchChunk getChunkFromMemoryCache(World world, int chunkX, int chunkZ) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        ChunkProviderServer providerServer = worldServer.getChunkProvider();
        PlayerChunkMap playerChunkMap = providerServer.a;
        PlayerChunk playerChunk = playerChunkMap.getVisibleChunk(ChunkCoordIntPair.pair(chunkX, chunkZ));
        if (playerChunk != null) {
            IChunkAccess chunk = playerChunk.f();
            if (chunk != null && !(chunk instanceof ChunkEmpty) && chunk instanceof net.minecraft.world.level.chunk.Chunk)
                return new Chunk(worldServer, chunk);
        }
        return null;
    }

    public BranchChunk fromChunk(World world, int chunkX, int chunkZ, BranchNBT nbt, boolean integralHeightmap) {
        return ChunkRegionLoader.loadChunk(((CraftWorld) world).getHandle(), new ChunkCoordIntPair(chunkX, chunkZ), ((NBT) nbt).getNMSTag(), integralHeightmap);
    }

    public BranchChunkLight fromLight(World world, BranchNBT nbt) {
        return ChunkRegionLoader.loadLight(((CraftWorld) world).getHandle(), ((NBT) nbt).getNMSTag());
    }

    public BranchChunkLight fromLight(World world) {
        return new ChunkLight(((CraftWorld) world).getHandle());
    }

    public BranchChunk.Status fromStatus(BranchNBT nbt) {
        return ChunkRegionLoader.loadStatus(((NBT) nbt).getNMSTag());
    }

    public BranchChunk fromChunk(World world, org.bukkit.Chunk chunk) {
        return new Chunk(((CraftChunk) chunk).getCraftWorld().getHandle(), ((CraftChunk) chunk).getHandle());
    }

    public int getPlayerPing(Player player) {
        return ((CraftPlayer) player).getHandle().e;
    }

    public void injectPlayer(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = entityPlayer.b;
        NetworkManager networkManager = connection.a;
        Channel channel = networkManager.k;
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addAfter("packet_handler", "farther_view_distance_write", new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Packet) {
                    if (!ProxyPlayerConnection.write(player, (Packet<?>) msg))
                        return;
                }
                super.write(ctx, msg, promise);
            }
        });
        pipeline.addAfter("encoder", "farther_view_distance_read", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Packet) {
                    if (!ProxyPlayerConnection.read(player, (Packet<?>) msg))
                        return;
                }
                super.channelRead(ctx, msg);
            }
        });
    }
}
