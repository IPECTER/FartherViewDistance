package xuan.cat.fartherviewdistance.code.branch.v1_19_R2;

import net.minecraft.nbt.CompoundTag;
import xuan.cat.fartherviewdistance.api.branch.BranchNBT;

public final class NBT implements BranchNBT {

    protected CompoundTag tag;

    public NBT() {
        this.tag = new CompoundTag();
    }

    public NBT(CompoundTag tag) {
        this.tag = tag;
    }


    public CompoundTag getNMSTag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag.toString();
    }
}
