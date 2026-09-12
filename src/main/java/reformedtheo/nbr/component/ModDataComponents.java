package reformedtheo.nbr.component;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

import reformedtheo.nbr.NoBuildingRequired;

public final class ModDataComponents {
	public static final DataComponentType<String> SCHEMATIC = register("schematic",
			DataComponentType.<String>builder()
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
					.build());

	private ModDataComponents() {
	}

	private static <T> DataComponentType<T> register(String path, DataComponentType<T> type) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, NoBuildingRequired.id(path), type);
	}

	public static void initialize() {
	}
}
