package reformedtheo.nbr.block.entity;

import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import reformedtheo.nbr.NoBuildingRequired;
import reformedtheo.nbr.block.ModBlocks;

public final class ModBlockEntities {
	public static final BlockEntityType<ArchitectTableBlockEntity> ARCHITECT_TABLE = register("architect_table",
			new BlockEntityType<>(ArchitectTableBlockEntity::new, Set.of(ModBlocks.ARCHITECT_TABLE)));

	private ModBlockEntities() {
	}

	private static <T extends BlockEntityType<?>> T register(String path, T type) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, NoBuildingRequired.id(path), type);
	}

	public static void initialize() {
	}
}
