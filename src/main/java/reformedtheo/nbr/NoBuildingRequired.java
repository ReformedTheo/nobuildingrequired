package reformedtheo.nbr;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reformedtheo.nbr.block.ModBlocks;
import reformedtheo.nbr.block.entity.ModBlockEntities;
import reformedtheo.nbr.component.ModDataComponents;
import reformedtheo.nbr.item.ModItems;
import reformedtheo.nbr.menu.ModMenus;
import reformedtheo.nbr.network.ModNetworking;

public class NoBuildingRequired implements ModInitializer {
	public static final String MOD_ID = "nobuildingrequired";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModDataComponents.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModMenus.initialize();
		ModItems.initialize();
		ModCreativeTabs.initialize();
		ModNetworking.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
