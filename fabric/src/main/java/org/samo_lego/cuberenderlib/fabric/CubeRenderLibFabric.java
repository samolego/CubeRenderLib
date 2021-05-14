package org.samo_lego.cuberenderlib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.samo_lego.cuberenderlib.CubeRenderLib;
import org.samo_lego.cuberenderlib.util.SelectionRender;

public class CubeRenderLibFabric implements ModInitializer, UseBlockCallback {
    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register(this);
        CubeRenderLib.init();
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos up = hitResult.getBlockPos().up();
        if(player instanceof ServerPlayerEntity) {
            if(player.getStackInHand(hand).getItem() == Items.STONE) {
                ((SelectionRender) player).stopRenderingAll();
            } else if(player.getStackInHand(hand).isEmpty()) {
                ((SelectionRender) player).stopRenderingSelection(up);
            } else
                ((SelectionRender) player).renderSelection(up, 10, 12, 11);
        }

        return ActionResult.PASS;
    }
}
