package dev.proxyfox.mod.maprender;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MapRenderBlockEntity extends BlockEntity {
	public final CombinedPlayerCanvas canvas;
	public final VirtualDisplay display;

	public MapRenderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		canvas = DrawableCanvas.create(1,1);
		display = VirtualDisplay.builder()
				.canvas(canvas)
				.pos(blockPos)
				.build();
		MapRenderMod.LOGGER.debug("owo?");
	}
	public MapRenderBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MapRenderMod.entity, blockPos, blockState);
	}


}
