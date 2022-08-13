package dev.proxyfox.mod.maprender;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MapRenderBlock extends BlockWithEntity implements PolymerBlock {
	public MapRenderBlock(Settings settings) {
		super(settings);
	}

	@Override
	public Block getPolymerBlock(BlockState blockState) {
		return Blocks.BEDROCK;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MapRenderBlockEntity(pos, state);
	}
}
