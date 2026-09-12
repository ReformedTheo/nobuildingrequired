package reformedtheo.nbr.block;

import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import reformedtheo.nbr.NoBuildingRequired;

public class ModBlockItemIds {
    public static final BlockItemId ARCHITECT_TABLE = create("architect_table");

    private ModBlockItemIds(){

    }

    private static BlockItemId create(String path) {
        Identifier id = NoBuildingRequired.id(path);
        return BlockItemId.create(id, id);
    }
}
