package xuan.cat.fartherviewdistance.code.branch.v1_19_R1;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.util.Vector;
import xuan.cat.fartherviewdistance.api.branch.BranchChunk;
import xuan.cat.fartherviewdistance.api.branch.BranchChunkLight;
import xuan.cat.fartherviewdistance.api.branch.BranchNBT;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Chunk implements BranchChunk {
    private static Field field_LevelChunkSection_nonEmptyBlockCount;

    static {
        try {
            field_LevelChunkSection_nonEmptyBlockCount = LevelChunkSection.class.getDeclaredField("f"); // TODO 映射 nonEmptyBlockCount
            field_LevelChunkSection_nonEmptyBlockCount.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }

    private final LevelChunk levelChunk;
    private final ServerLevel worldServer;


    public Chunk(ServerLevel worldServer, LevelChunk levelChunk) {
        this.levelChunk = levelChunk;
        this.worldServer = worldServer;
    }

    public static Status ofStatus(ChunkStatus chunkStatus) {
        if (chunkStatus == ChunkStatus.EMPTY) {
            return Status.EMPTY;
        } else if (chunkStatus == ChunkStatus.STRUCTURE_STARTS) {
            return Status.STRUCTURE_STARTS;
        } else if (chunkStatus == ChunkStatus.STRUCTURE_REFERENCES) {
            return Status.STRUCTURE_REFERENCES;
        } else if (chunkStatus == ChunkStatus.BIOMES) {
            return Status.BIOMES;
        } else if (chunkStatus == ChunkStatus.NOISE) {
            return Status.NOISE;
        } else if (chunkStatus == ChunkStatus.SURFACE) {
            return Status.SURFACE;
        } else if (chunkStatus == ChunkStatus.CARVERS) {
            return Status.CARVERS;
        } else if (chunkStatus == ChunkStatus.LIQUID_CARVERS) {
            return Status.LIQUID_CARVERS;
        } else if (chunkStatus == ChunkStatus.FEATURES) {
            return Status.FEATURES;
        } else if (chunkStatus == ChunkStatus.LIGHT) {
            return Status.LIGHT;
        } else if (chunkStatus == ChunkStatus.SPAWN) {
            return Status.SPAWN;
        } else if (chunkStatus == ChunkStatus.HEIGHTMAPS) {
            return Status.HEIGHTMAPS;
        } else if (chunkStatus == ChunkStatus.FULL) {
            return Status.FULL;
        }
        return Status.EMPTY;
    }

    public BranchNBT toNBT(BranchChunkLight light, List<Runnable> asyncRunnable) {
        return new NBT(ChunkRegionLoader.saveChunk(worldServer, levelChunk, (ChunkLight) light, asyncRunnable));
    }

    LevelChunk getLevelChunk() {
        return levelChunk;
    }

    public org.bukkit.Chunk getChunk() {
        return new CraftChunk(levelChunk);
    }

    public org.bukkit.World getWorld() {
        return worldServer.getWorld();
    }

    public BlockState getIBlockData(int x, int y, int z) {
        int indexY = (y >> 4) - levelChunk.getMinSection();
        LevelChunkSection[] chunkSections = levelChunk.getSections();
        if (indexY >= 0 && indexY < chunkSections.length) {
            LevelChunkSection chunkSection = chunkSections[indexY];
            if (chunkSection != null && !chunkSection.hasOnlyAir())
                return chunkSection.getBlockState(x & 15, y & 15, z & 15);
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void setIBlockData(int x, int y, int z, BlockState iBlockData) {
        int indexY = (y >> 4) - levelChunk.getMinSection();
        LevelChunkSection[] chunkSections = levelChunk.getSections();
        if (indexY >= 0 && indexY < chunkSections.length) {
            LevelChunkSection chunkSection = chunkSections[indexY];
            if (chunkSection == null)
                chunkSection = chunkSections[indexY] = new LevelChunkSection(indexY, worldServer.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), new ChunkPos(levelChunk.locX, levelChunk.locZ), worldServer);
            chunkSection.setBlockState(x & 15, y & 15, z & 15, iBlockData, false);
        }
    }

    public boolean equalsBlockData(int x, int y, int z, BlockData blockData) {
        return equalsBlockData(x, y, z, ((CraftBlockData) blockData).getState());
    }

    public boolean equalsBlockData(int x, int y, int z, BlockState other) {
        BlockState state = getIBlockData(x, y, z);
        return state != null && state.equals(other);
    }

    public BlockData getBlockData(int x, int y, int z) {
        BlockState blockData = getIBlockData(x, y, z);
        return blockData != null ? CraftBlockData.fromData(blockData) : CraftBlockData.fromData(Blocks.AIR.defaultBlockState());
    }

    public void setBlockData(int x, int y, int z, BlockData blockData) {
        BlockState iBlockData = ((CraftBlockData) blockData).getState();
        if (iBlockData != null)
            setIBlockData(x, y, z, iBlockData);
    }

    public Map<Vector, BlockData> getBlockDataMap() {
        Map<Vector, BlockData> vectorBlockDataMap = new HashMap<>();
        int maxHeight = worldServer.getMaxBuildHeight();
        int minHeight = worldServer.getMinBuildHeight();
        for (int x = 0; x < 16; x++) {
            for (int y = minHeight; y < maxHeight; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockData blockData = this.getBlockData(x, y, z);
                    org.bukkit.Material material = blockData.getMaterial();
                    if (material != org.bukkit.Material.AIR && material != org.bukkit.Material.VOID_AIR && material != org.bukkit.Material.CAVE_AIR) {
                        vectorBlockDataMap.put(new Vector(x, y, z), blockData);
                    }
                }
            }
        }

        return vectorBlockDataMap;
    }

    public int getX() {
        return levelChunk.getPos().x;
    }

    public int getZ() {
        return levelChunk.getPos().z;
    }

    public void replaceAllMaterial(BlockData[] target, BlockData to) {
        Map<Block, BlockState> targetMap = new HashMap<>();
        for (BlockData targetData : target) {
            BlockState targetState = ((CraftBlockData) targetData).getState();
            targetMap.put(targetState.getBlock(), targetState);
        }
        BlockState toI = ((CraftBlockData) to).getState();
        for (LevelChunkSection section : levelChunk.getSections()) {
            if (section != null) {
                AtomicInteger counts = new AtomicInteger();
                PalettedContainer<BlockState> blocks = section.getStates();
                List<Integer> conversionLocationList = new ArrayList<>();
                PalettedContainer.CountConsumer<BlockState> forEachLocation = (state, location) -> {
                    if (state == null)
                        return;
                    BlockState targetState = targetMap.get(state.getBlock());
                    if (targetState != null) {
                        conversionLocationList.add(location);
                        state = toI;
                    }
                    if (!state.isAir())
                        counts.incrementAndGet();
                    FluidState fluid = state.getFluidState();
                    if (!fluid.isEmpty())
                        counts.incrementAndGet();
                };
                try {
                    // 適用於 paper
                    blocks.forEachLocation(forEachLocation);
                } catch (NoSuchMethodError noSuchMethodError) {
                    // 適用於 spigot (不推薦)
                    blocks.count(forEachLocation);
                }
                conversionLocationList.forEach(location -> {
                    blocks.getAndSetUnchecked(location & 15, location >> 8 & 15, location >> 4 & 15, toI);

                });
                try {
                    field_LevelChunkSection_nonEmptyBlockCount.set(section, counts.shortValue());
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public org.bukkit.Material getMaterial(int x, int y, int z) {
        return getBlockData(x, y, z).getMaterial();
    }

    public void setMaterial(int x, int y, int z, org.bukkit.Material material) {
        setBlockData(x, y, z, material.createBlockData());
    }

    @Deprecated
    public org.bukkit.block.Biome getBiome(int x, int z) {
        return this.getBiome(x, 0, z);
    }

    public org.bukkit.block.Biome getBiome(int x, int y, int z) {
        return CraftBlock.biomeBaseToBiome(levelChunk.biomeRegistry, levelChunk.getNoiseBiome(x, y, z));
    }

    @Deprecated
    public void setBiome(int x, int z, org.bukkit.block.Biome biome) {
        setBiome(x, 0, z, biome);
    }

    public void setBiome(int x, int y, int z, org.bukkit.block.Biome biome) {
        levelChunk.setBiome(x, y, z, CraftBlock.biomeToBiomeBase(levelChunk.biomeRegistry, biome));
    }

    public boolean hasFluid(int x, int y, int z) {
        return !getIBlockData(x, y, z).getFluidState().isEmpty();
    }

    public boolean isAir(int x, int y, int z) {
        return getIBlockData(x, y, z).isAir();
    }

    public int getHighestY(int x, int z) {
        return levelChunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
    }

    public Status getStatus() {
        return ofStatus(levelChunk.getStatus());
    }
}
