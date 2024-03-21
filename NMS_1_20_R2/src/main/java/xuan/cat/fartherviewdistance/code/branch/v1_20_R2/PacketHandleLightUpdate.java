package xuan.cat.fartherviewdistance.code.branch.v1_20_R2;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class PacketHandleLightUpdate {
    public PacketHandleLightUpdate() {
    }

    private static void saveBitSet(byte[][] nibbleArrays, int index, BitSet notEmpty, BitSet isEmpty, List<byte[]> list) {
        byte[] nibbleArray = nibbleArrays[index];
        if (nibbleArray != ChunkLight.EMPTY) {
            if (nibbleArray == null) {
                isEmpty.set(index);
            } else {
                notEmpty.set(index);
                list.add(nibbleArray);
            }
        }
    }

    public void write(FriendlyByteBuf serializer, ChunkLight light) {
        List<byte[]> dataSky = new ArrayList<>();
        List<byte[]> dataBlock = new ArrayList<>();
        BitSet notSkyEmpty = new BitSet();
        BitSet notBlockEmpty = new BitSet();
        BitSet isSkyEmpty = new BitSet();
        BitSet isBlockEmpty = new BitSet();
        System.out.println(String.valueOf(light.getBlockLights()));
        for (int index = 0; index < light.getArrayLength(); ++index) {
            saveBitSet(light.getSkyLights(), index, notSkyEmpty, isSkyEmpty, dataSky);
            saveBitSet(light.getBlockLights(), index, notBlockEmpty, isBlockEmpty, dataBlock);
        }

        serializer.writeBitSet(notSkyEmpty);
        serializer.writeBitSet(notBlockEmpty);
        serializer.writeBitSet(isSkyEmpty);
        serializer.writeBitSet(isBlockEmpty);
        serializer.writeCollection(dataSky, FriendlyByteBuf::writeByteArray);
        serializer.writeCollection(dataBlock, FriendlyByteBuf::writeByteArray);
    }
}
