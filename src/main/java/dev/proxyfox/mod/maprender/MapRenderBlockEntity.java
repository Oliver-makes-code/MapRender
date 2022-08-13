package dev.proxyfox.mod.maprender;

import dev.proxyfox.mod.maprender.doom.Doom;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class MapRenderBlockEntity extends BlockEntity {
	public final PlayerCanvas canvas;
	public final VirtualDisplay display;
	public final Doom doom;

	public MapRenderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		canvas = DrawableCanvas.create();
		display = VirtualDisplay.builder()
				.canvas(canvas)
				.pos(blockPos.offset(blockState.get(MapRenderBlock.property)))
				.direction(blockState.get(MapRenderBlock.property))
				.rotation(BlockRotation.CLOCKWISE_180)
				.build();
		doom = new Doom(this);
	}
	public MapRenderBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MapRenderMod.entity, blockPos, blockState);
	}

	public Set<ServerPlayerEntity> players = new HashSet<>();

	public void tick(World world, BlockPos pos, BlockState state) {
		if (world instanceof ServerWorld serverWorld) {
			for (ServerPlayerEntity player : serverWorld.getPlayers()) {
				if (Math.sqrt(player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) < 25) {
					if (!players.contains(player)) {
						players.add(player);
						display.addPlayer(player);
						canvas.addPlayer(player);
					}
				} else {
					if (players.contains(player)) {
						players.remove(player);
						display.removePlayer(player);
						canvas.removePlayer(player);
					}
				}
			}
		}

		doom.update();
	}

}
