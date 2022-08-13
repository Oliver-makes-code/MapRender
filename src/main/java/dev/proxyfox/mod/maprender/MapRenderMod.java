package dev.proxyfox.mod.maprender;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapRenderMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Map Render");
	public static final MapRenderBlock block = new MapRenderBlock(QuiltBlockSettings.copyOf(Blocks.BEDROCK));
	public static final Item item = new PolymerBlockItem(block, new QuiltItemSettings(), Items.MAP);
	public static final BlockEntityType<MapRenderBlockEntity> entity = QuiltBlockEntityTypeBuilder.create(MapRenderBlockEntity::new, block).build();

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.BLOCK, id("block"), block);
		Registry.register(Registry.ITEM, id("item"), item);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("entity"), entity);
		PolymerBlockUtils.registerBlockEntity(entity);
	}

	public static Identifier id(String path) {
		return new Identifier("maprender", path);
	}
}
