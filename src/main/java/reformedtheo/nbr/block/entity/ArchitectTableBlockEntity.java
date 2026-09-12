package reformedtheo.nbr.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import reformedtheo.nbr.menu.ArchitectTableMenu;

public class ArchitectTableBlockEntity extends BaseContainerBlockEntity {
	public static final int SLOTS = 27;

	private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
	private String schematic = "";

	public ArchitectTableBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ARCHITECT_TABLE, pos, state);
	}

	@Override
	public int getContainerSize() {
		return SLOTS;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.nobuildingrequired.architect_table");
	}

	public String schematic() {
		return schematic;
	}

	public void setSchematic(String schematic) {
		this.schematic = schematic;
		setChanged();
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
		return new ArchitectTableMenu(containerId, playerInventory, this);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putString("Schematic", schematic);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, items);
		schematic = input.getStringOr("Schematic", "");
	}
}
