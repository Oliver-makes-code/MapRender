package dev.proxyfox.mod.maprender;

import eu.pb4.polymer.api.entity.PolymerEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MapRenderEntity extends Entity implements PolymerEntity {
	public MapRenderBlockEntity be;
	public MapRenderEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}
	public MapRenderEntity(World world, MapRenderBlockEntity be) {
		this(MapRenderMod.entity, world);
		this.be = be;
	}

	@Override
	public EntityType<?> getPolymerEntityType() {
		return EntityType.MINECART;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (this.hasPassengers()) return super.interact(player, hand);
		player.startRiding(this);
		return ActionResult.SUCCESS;
	}

	@Override
	public void initDataTracker() {

	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return new ArrayList<>();
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {

	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
