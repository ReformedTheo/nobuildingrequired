package reformedtheo.nbr;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import reformedtheo.nbr.block.ModBlocks;
import reformedtheo.nbr.item.ModItems;

public final class ModCreativeTabs {
	private ModCreativeTabs() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(vanilla("building_blocks"))
				.register(output -> output.accept(ModBlocks.ARCHITECT_TABLE));

		CreativeModeTabEvents.modifyOutputEvent(vanilla("tools_and_utilities"))
				.register(output -> output.accept(ModItems.BLUEPRINT));
	}

	private static ResourceKey<CreativeModeTab> vanilla(String path) {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace(path));
	}
}
