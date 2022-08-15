package dev.proxyfox.mod.maprender.mixin;

import dev.proxyfox.mod.maprender.MapRenderEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class Mixin_ServerPlayNetworkHandler {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "onPlayerInput", at = @At("RETURN"))
	private void MapRender$onPlayerInput(PlayerInputC2SPacket packet, CallbackInfo ci) {
		if (player.hasVehicle() && player.getVehicle() instanceof MapRenderEntity entity && entity.be != null) {
			entity.be.doom.forward = packet.getForward();
			entity.be.doom.sideways = packet.getSideways();
		}
	}
}
