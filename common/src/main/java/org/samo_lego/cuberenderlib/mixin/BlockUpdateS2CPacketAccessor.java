package org.samo_lego.cuberenderlib.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockUpdateS2CPacket.class)
public interface BlockUpdateS2CPacketAccessor {
    @Accessor("pos")
    BlockPos getPos();

    @Accessor("state")
    BlockState getState();
}
