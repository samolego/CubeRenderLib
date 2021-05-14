package org.samo_lego.cuberenderlib.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

public interface SelectionRender {

    /**
     * Renders selection box from first corner of size x, y and z
     * @param firstCorner corner to start rendering selection in (included)
     * @param secondCorner second corner of selection (max 48 * 48 * 48)
     */
    void renderSelection(@NotNull BlockPos firstCorner, @NotNull BlockPos secondCorner);

    /**
     * Renders selection box from first corner of size x, y and z
     * @param firstCorner corner to start rendering selection in (included)
     * @param boxSize size of the selection box (max 48 * 48 * 48)
     */
    void renderSelection(@NotNull BlockPos firstCorner, @NotNull Vec3i boxSize);

    /**
     * Renders selection box from first corner of size x, y and z
     * @param firstCorner corner to start rendering selection in (included)
     * @param x x size (max 48)
     * @param y y size (max 48)
     * @param z z size (max 48)
     */
    void renderSelection(@NotNull BlockPos firstCorner, int x, int y, int z);

    /**
     * Stops rendering the selection which starts at given parameter.
     * @param firstCorner start of the render position.
     */
    void stopRenderingSelection(BlockPos firstCorner);

    /**
     * Stops rendering all selections.
     */
    void stopRenderingAll();
}
