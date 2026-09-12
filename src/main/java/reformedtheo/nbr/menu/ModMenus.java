package reformedtheo.nbr.menu;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

import reformedtheo.nbr.NoBuildingRequired;

public final class ModMenus {
	/** O cliente recebe a posição da mesa e lê o block entity dele, já sincronizado. */
	public static final MenuType<ArchitectTableMenu> ARCHITECT_TABLE = register("architect_table",
			new ExtendedMenuType<>(ArchitectTableMenu::new, BlockPos.STREAM_CODEC));

	private ModMenus() {
	}

	private static <T extends MenuType<?>> T register(String path, T type) {
		return Registry.register(BuiltInRegistries.MENU, NoBuildingRequired.id(path), type);
	}

	public static void initialize() {
	}
}
