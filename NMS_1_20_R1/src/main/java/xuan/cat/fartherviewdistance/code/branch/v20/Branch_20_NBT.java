package xuan.cat.fartherviewdistance.code.branch.v20;

import net.minecraft.nbt.CompoundTag;
import xuan.cat.fartherviewdistance.api.branch.BranchNBT;

public final class Branch_20_NBT implements BranchNBT {

    protected CompoundTag tag;

    public Branch_20_NBT() {
        this.tag = new CompoundTag();
    }

    public Branch_20_NBT(CompoundTag tag) {
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
