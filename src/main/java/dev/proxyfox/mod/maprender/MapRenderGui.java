package dev.proxyfox.mod.maprender;

import eu.pb4.sgui.api.gui.HotbarGui;
import net.minecraft.server.network.ServerPlayerEntity;

// So we can get mouse button controls
public class MapRenderGui extends HotbarGui {
	public MapRenderGui(ServerPlayerEntity player) {
		super(player);
	}
}
