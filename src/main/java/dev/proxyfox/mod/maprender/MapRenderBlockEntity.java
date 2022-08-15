package dev.proxyfox.mod.maprender;

import dev.proxyfox.mod.maprender.doom.Doom;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.impl.other.FakeWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class MapRenderBlockEntity extends BlockEntity {
	public final PlayerCanvas canvas;
	public final VirtualDisplay display;
	public final Doom doom;
	public MapRenderEntity fakeEntity;
	public MinecartEntity alias;

	public MapRenderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		var dir = blockState.get(MapRenderBlock.property);
		canvas = DrawableCanvas.create();
		display = VirtualDisplay.builder()
				.canvas(canvas)
				.pos(blockPos.offset(dir))
				.direction(blockState.get(MapRenderBlock.property))
				.rotation(BlockRotation.CLOCKWISE_180)
				.build();
		doom = new Doom(this);
	}

	public MapRenderBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MapRenderMod.blockEntity, blockPos, blockState);
	}

	public Set<ServerPlayerEntity> players = new HashSet<>();

	public void tick(World world, BlockPos pos, BlockState state) {
		if (fakeEntity == null) {
			fakeEntity = new MapRenderEntity(world, this);
			alias = new MinecartEntity(EntityType.MINECART, world);
			alias.setNoGravity(true);
			alias.setId(fakeEntity.getId());
			fakeEntity.setNoGravity(true);
			world.spawnEntity(fakeEntity);
		}
		var dir = world.getBlockState(pos).get(MapRenderBlock.property);
		BlockPos entityPos = this.pos.down().offset(dir, 2);
		double x = entityPos.getX() + 0.5;
		double z = entityPos.getZ() + 0.5;
		fakeEntity.updatePosition(x, entityPos.getY(), z);
		if (world instanceof ServerWorld serverWorld) {
			for (ServerPlayerEntity player : serverWorld.getPlayers()) {
				if (Math.sqrt(player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) < 25) {
					if (!players.contains(player)) {
						players.add(player);
						display.addPlayer(player);
						canvas.addPlayer(player);
						player.networkHandler.sendPacket(fakeEntity.createSpawnPacket());
					}
				} else {
					if (players.contains(player)) {
						players.remove(player);
						display.removePlayer(player);
						canvas.removePlayer(player);
						player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(fakeEntity.getId()));
					}
				}
			}
		}

		doom.update();
	}

}
