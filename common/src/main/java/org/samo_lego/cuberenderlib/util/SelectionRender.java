package org.samo_lego.cuberenderlib.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

public interface SelectionRender {
    void renderSelection(@NotNull BlockPos firstCorner, @NotNull BlockPos secondCorner);

    void renderSelection(@NotNull BlockPos firstCorner, @NotNull Vec3i boxSize);

    void renderSelection(@NotNull BlockPos firstCorner, int x, int y, int z);
}
