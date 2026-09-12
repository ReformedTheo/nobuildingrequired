package reformedtheo.nbr.menu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import reformedtheo.nbr.NoBuildingRequired;

public final class ModMenus {
	public static final MenuType<ArchitectTableMenu> ARCHITECT_TABLE = register("architect_table",
			new MenuType<>(ArchitectTableMenu::new, FeatureFlags.VANILLA_SET));

	private ModMenus() {
	}

	private static <T extends MenuType<?>> T register(String path, T type) {
		return Registry.register(BuiltInRegistries.MENU, NoBuildingRequired.id(path), type);
	}

	public static void initialize() {
	}
}
