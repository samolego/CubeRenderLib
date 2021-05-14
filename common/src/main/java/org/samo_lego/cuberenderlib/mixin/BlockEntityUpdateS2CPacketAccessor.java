package org.samo_lego.cuberenderlib.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityUpdateS2CPacket.class)
public interface BlockEntityUpdateS2CPacketAccessor {
    @Accessor("pos")
    BlockPos pos();
    @Accessor("blockEntityType")
    int blockEntityType();
    @Accessor("tag")
    CompoundTag tag();
}
