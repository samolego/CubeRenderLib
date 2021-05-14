package org.samo_lego.cuberenderlib.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.cuberenderlib.util.SelectionRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static org.samo_lego.cuberenderlib.CubeRenderLib.MOD_ID;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixinCast_SelectionRender implements SelectionRender {
    @Shadow public ServerPlayNetworkHandler networkHandler;
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
    private static final int MAX_STRUCTURE_SIZE = 48;

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, @NotNull BlockPos secondCorner) {

    }

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, @NotNull Vec3i boxSize) {

    }

    @Override
    public void renderSelection(@NotNull BlockPos firstCorner, int sizeX, int sizeY, int sizeZ) {
        System.out.println("Rendering! From " + firstCorner+ " -> "+ sizeX +" "+ sizeY + " "+ sizeZ);

        BlockPos.Mutable fakeBlockPos = firstCorner.mutableCopy();
        fakeBlockPos.setY(firstCorner.getY() - MAX_STRUCTURE_SIZE);

        System.out.println("Offset " + fakeBlockPos.getY() + firstCorner.getY());

        // https://wiki.vg/Protocol#Block_Entity_Data
        CompoundTag compoundTag = new CompoundTag();

        StructureBlockBlockEntity fake = new StructureBlockBlockEntity();
        fake.toTag(compoundTag);

        compoundTag.putInt("x", fakeBlockPos.getX());
        compoundTag.putInt("y", fakeBlockPos.getY());
        compoundTag.putInt("z", fakeBlockPos.getZ());

        // Offset
        compoundTag.putInt("posX", 0);
        compoundTag.putInt("posY", MAX_STRUCTURE_SIZE);
        compoundTag.putInt("posZ", 0);

        compoundTag.putInt("sizeX", sizeX);
        compoundTag.putInt("sizeY", sizeY);
        compoundTag.putInt("sizeZ", sizeZ);

        compoundTag.putString("mode", "SAVE");
        compoundTag.putString("name", MOD_ID + ":selection");
        compoundTag.putString("author", MOD_ID);
        compoundTag.putBoolean("showboundingbox", true);

        System.out.println(compoundTag);

        BlockUpdateS2CPacket bPacket = new BlockUpdateS2CPacket(fakeBlockPos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        this.networkHandler.sendPacket(bPacket);

        BlockEntityUpdateS2CPacket bePacket = new BlockEntityUpdateS2CPacket(fakeBlockPos, 7, compoundTag);
        this.networkHandler.sendPacket(bePacket);

        System.out.println("Sent info!");
        if(!this.player.abilities.creativeMode) {
            PlayerAbilitiesS2CPacket packet = new PlayerAbilitiesS2CPacket();
            //noinspection ConstantConditions
            ((PlayerAbilitiesS2CPacketAccessor) packet).setCreative(true); // :tiny_potato:
            this.networkHandler.sendPacket(packet);

            /*packet = new PlayerAbilitiesS2CPacket();
            //noinspection ConstantConditions
            ((PlayerAbilitiesS2CPacketAccessor) packet).setCreative(false); // :tiny_potato:
            this.networkHandler.sendPacket(packet);*/
        }

        // {mirror:"NONE",powered:0b,z:-107,x:193,seed:0L,integrity:1.0f,sizeY:20,posZ:0,sizeZ:30,showboundingbox:1b,showair:0b,posY:1,posX:0,name:"cuberenderlib:selection",rotation:"NONE",mode:"SAVE",id:"minecraft:structure_block",y:77,sizeX:10,metadata:"",author:"",ignoreEntities:1b}
        // {mirror:"NONE",powered:0b,z:-107,x:193,seed:0L,integrity:1.0f,sizeY:2,posZ:-1,sizeZ:3,showboundingbox:1b,showair:0b,posY:0,posX:-1,name:"minecraft:test",rotation:"NONE",mode:"SAVE",id:"minecraft:structure_block",y:81,sizeX:2,metadata:"",author:"samo_lego",ignoreEntities:1b}
    }

    @Unique
    private void cuberenderlib$renderSelection() {

    }
}
