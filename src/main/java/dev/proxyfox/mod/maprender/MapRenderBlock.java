package dev.proxyfox.mod.maprender;

import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MapRenderBlock extends BlockWithEntity implements PolymerBlock {
	public static final DirectionProperty property = DirectionProperty.of("direction");

	public MapRenderBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(property);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(property, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public Block getPolymerBlock(BlockState blockState) {
		return Blocks.NETHERITE_BLOCK;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MapRenderBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return (world2, pos, state2, entity) -> ((MapRenderBlockEntity)entity).tick(world2, pos, state2);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world instanceof ServerWorld serverWorld) {
			var entity = ((MapRenderBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(pos)));
			entity.display.destroy();
			MapIdManager.freeMapId(entity.canvas.getId());
			for (var p : entity.players) {
				entity.canvas.removePlayer(p);
			}
			entity.fakeEntity.remove(Entity.RemovalReason.DISCARDED);
		}
		super.onBreak(world, pos, state, player);
	}
}
