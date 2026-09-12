package reformedtheo.nbr.block;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
    public static final Block ARCHITECT_TABLE = register(
            ModBlockItemIds.ARCHITECT_TABLE,
            ArchitectTableBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(SoundType.BAMBOO_WOOD)
                    .strength(0.5F)
    );

    private ModBlocks() {
    }

    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        Block block = register(id.block(), factory, properties);
        BlockItem blockItem = new BlockItem(block, new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return block;
    }

    private static Block register(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    public static void initialize() {
    }
}
