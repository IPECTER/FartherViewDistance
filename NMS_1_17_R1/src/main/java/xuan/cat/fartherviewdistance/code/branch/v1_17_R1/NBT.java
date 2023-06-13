package xuan.cat.fartherviewdistance.code.branch.v1_17_R1;

import net.minecraft.nbt.NBTTagCompound;
import xuan.cat.fartherviewdistance.api.branch.BranchNBT;

public final class NBT implements BranchNBT {

    protected NBTTagCompound tag;

    public NBT() {
        this.tag = new NBTTagCompound();
    }

    public NBT(NBTTagCompound tag) {
        this.tag = tag;
    }


    public NBTTagCompound getNMSTag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag.toString();
    }
}
