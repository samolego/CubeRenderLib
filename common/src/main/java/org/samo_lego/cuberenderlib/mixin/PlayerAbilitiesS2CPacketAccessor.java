package org.samo_lego.cuberenderlib.mixin;

import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerAbilitiesS2CPacket.class)
public interface PlayerAbilitiesS2CPacketAccessor {
    @Accessor("creativeMode")
    void setCreative(boolean creative);
}
