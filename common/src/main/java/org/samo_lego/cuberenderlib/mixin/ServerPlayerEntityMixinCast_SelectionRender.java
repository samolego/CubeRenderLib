package org.samo_lego.cuberenderlib.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.cuberenderlib.util.SelectionRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static org.samo_lego.cuberenderlib.CubeRenderLib.MAX_OFFSET;
import static org.samo_lego.cuberenderlib.CubeRenderLib.MOD_ID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixinCast_SelectionRender implements SelectionRender {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow public abstract ServerWorld getServerWorld();

    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
    @Unique
    private final CompoundTag selectionTag = new CompoundTag();
    private final List<BlockPos> cuberenderlib$renderedRegions = new ArrayList<>();

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, @NotNull BlockPos secondCorner) {
        int x = firstCorner.getX() - secondCorner.getX();
        int y = firstCorner.getY() - secondCorner.getY();
        int z = firstCorner.getZ() - secondCorner.getZ();

        this.renderSelection(firstCorner, x, y, z);
    }

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, @NotNull Vec3i boxSize) {
        this.renderSelection(firstCorner, boxSize.getX(), boxSize.getY(), boxSize.getZ());
    }

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, int sizeX, int sizeY, int sizeZ) {
        System.out.println("Rendering! From " + firstCorner+ " -> "+ sizeX +" "+ sizeY + " "+ sizeZ);

        BlockPos.Mutable fakeBlockPos = firstCorner.mutableCopy();
        fakeBlockPos.setY(firstCorner.getY() - MAX_OFFSET);

        System.out.println("Offset " + (firstCorner.getY() - fakeBlockPos.getY()));

        // https://wiki.vg/Protocol#Block_Entity_Data
        this.selectionTag.putInt("x", fakeBlockPos.getX());
        this.selectionTag.putInt("y", fakeBlockPos.getY());
        this.selectionTag.putInt("z", fakeBlockPos.getZ());

        // Offset
        this.selectionTag.putInt("posX", 0);
        this.selectionTag.putInt("posY", MAX_OFFSET);
        this.selectionTag.putInt("posZ", 0);

        // Selection size
        this.selectionTag.putInt("sizeX", sizeX);
        this.selectionTag.putInt("sizeY", sizeY);
        this.selectionTag.putInt("sizeZ", sizeZ);

        BlockUpdateS2CPacket bPacket = new BlockUpdateS2CPacket(fakeBlockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        this.networkHandler.sendPacket(bPacket);

        this.cuberenderlib$renderedRegions.add(fakeBlockPos.toImmutable());

        BlockEntityUpdateS2CPacket bePacket = new BlockEntityUpdateS2CPacket(fakeBlockPos, 7, this.selectionTag);
        this.networkHandler.sendPacket(bePacket);

        this.cuberenderlib$updateAbilities(true);

        // {mirror:"NONE",powered:0b,z:-107,x:193,seed:0L,integrity:1.0f,sizeY:20,posZ:0,sizeZ:30,showboundingbox:1b,showair:0b,posY:1,posX:0,name:"cuberenderlib:selection",rotation:"NONE",mode:"SAVE",id:"minecraft:structure_block",y:77,sizeX:10,metadata:"",author:"",ignoreEntities:1b}
        // {mirror:"NONE",powered:0b,z:-107,x:193,seed:0L,integrity:1.0f,sizeY:2,posZ:-1,sizeZ:3,showboundingbox:1b,showair:0b,posY:0,posX:-1,name:"minecraft:test",rotation:"NONE",mode:"SAVE",id:"minecraft:structure_block",y:81,sizeX:2,metadata:"",author:"samo_lego",ignoreEntities:1b}
    }

    @Override
    public void stopRenderingSelection(BlockPos firstCorner) {
        BlockPos.Mutable mutable = firstCorner.mutableCopy();
        mutable.setY(firstCorner.getY() - MAX_OFFSET);

        boolean removed = this.cuberenderlib$renderedRegions.remove(mutable.toImmutable());
        System.out.println("Removed " + removed);
        System.out.println(firstCorner + " " + cuberenderlib$renderedRegions);
        if(removed) {
            BlockUpdateS2CPacket bPacket = new BlockUpdateS2CPacket(firstCorner, this.getServerWorld().getBlockState(firstCorner));
            this.networkHandler.sendPacket(bPacket);

            if(this.cuberenderlib$renderedRegions.isEmpty())
                this.cuberenderlib$updateAbilities(false);
        }
    }


    @Override
    public void stopRenderingAll() {
        this.cuberenderlib$renderedRegions.forEach(blockPos -> {
            BlockUpdateS2CPacket bPacket = new BlockUpdateS2CPacket(blockPos, this.getServerWorld().getBlockState(blockPos));
            this.networkHandler.sendPacket(bPacket);
        });
        this.cuberenderlib$updateAbilities(false);
    }

    @Unique
    private void cuberenderlib$updateAbilities(boolean spoofCreative) {
        if(!this.player.abilities.creativeMode) {
            PlayerAbilitiesS2CPacket packet = new PlayerAbilitiesS2CPacket();
            //noinspection ConstantConditions
            ((PlayerAbilitiesS2CPacketAccessor) packet).setCreative(spoofCreative); // :tiny_potato:
            this.networkHandler.sendPacket(packet);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/world/ServerWorld;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/network/ServerPlayerInteractionManager;)V", at = @At("TAIL"))
    private void construcotr(MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager, CallbackInfo ci) {
        StructureBlockBlockEntity fake = new StructureBlockBlockEntity();
        fake.toTag(this.selectionTag);

        this.selectionTag.putString("mode", "SAVE");
        this.selectionTag.putString("name", MOD_ID + ":selection");
        this.selectionTag.putString("author", MOD_ID);
        this.selectionTag.putBoolean("showboundingbox", true);
    }
}
