package reformedtheo.nbr.item;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import reformedtheo.nbr.NoBuildingRequired;

public final class ModItems {
	public static final Item BLUEPRINT = register("blueprint", BlueprintItem::new, new Item.Properties().stacksTo(1));

	private ModItems() {
	}

	private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, NoBuildingRequired.id(path));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	public static void initialize() {
	}
}
