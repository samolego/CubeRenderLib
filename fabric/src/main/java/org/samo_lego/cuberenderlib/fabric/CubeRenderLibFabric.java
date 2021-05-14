package org.samo_lego.cuberenderlib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
        if(player instanceof ServerPlayerEntity) {
            ((SelectionRender) player).renderSelection(hitResult.getBlockPos(), 10, 10, 30);
        }
        return ActionResult.PASS;
    }
}
