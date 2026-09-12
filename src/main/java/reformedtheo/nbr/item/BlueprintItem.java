package reformedtheo.nbr.item;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import reformedtheo.nbr.component.ModDataComponents;

public class BlueprintItem extends Item {
	public BlueprintItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);

		String schematic = stack.get(ModDataComponents.SCHEMATIC);

		if (schematic != null) {
			tooltip.accept(Component.translatable("item.nobuildingrequired.blueprint.schematic", schematic));
		}
	}
}
