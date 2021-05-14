package org.samo_lego.cuberenderlib.mixin;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class SPNHMixin {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("TAIL"))
    private void onPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener, CallbackInfo ci) {
        if(packet instanceof EntityPositionS2CPacket || packet instanceof PlayerPositionLookS2CPacket ||
                packet instanceof KeepAliveS2CPacket || packet instanceof WorldTimeUpdateS2CPacket || packet instanceof EntitySetHeadYawS2CPacket
        || packet instanceof EntityS2CPacket || packet instanceof PlayerListS2CPacket || packet instanceof EntityVelocityUpdateS2CPacket)
            return;
        System.out.println(packet.getClass());
        if(packet instanceof BlockUpdateS2CPacket) {
            BlockPos pos = ((BlockUpdateS2CPacketAccessor) packet).getPos();
            BlockState state = ((BlockUpdateS2CPacketAccessor) packet).getState();
            System.out.println("<----bupadte---->");
            System.out.println(pos + " " + state.toString());
        } else if(packet instanceof BlockEntityUpdateS2CPacket) {
            BlockPos pos = ((BlockEntityUpdateS2CPacketAccessor) packet).pos();
            CompoundTag state = ((BlockEntityUpdateS2CPacketAccessor) packet).tag();
            System.out.println("<==Caught tag:==>");
            System.out.println(state);
        }
    }
}
