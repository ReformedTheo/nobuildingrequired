package reformedtheo.nbr.menu;

import java.util.Map;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import reformedtheo.nbr.block.entity.ArchitectTableBlockEntity;
import reformedtheo.nbr.catalog.Catalog;
import reformedtheo.nbr.component.ModDataComponents;
import reformedtheo.nbr.item.ModItems;

public class ArchitectTableMenu extends AbstractContainerMenu {
	/** Ids de botão: 0..N-1 selecionam a entrada do catálogo; este gera o blueprint. */
	public static final int BUTTON_GENERATE = -1;

	private static final int TABLE_SLOTS = ArchitectTableBlockEntity.SLOTS;
	private static final int COLUMNS = 9;

	private final Container table;
	private final DataSlot selected;

	/** Lado do cliente: o conteúdo e a seleção chegam do servidor depois. */
	public ArchitectTableMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(TABLE_SLOTS), DataSlot.standalone());
	}

	public ArchitectTableMenu(int containerId, Inventory playerInventory, ArchitectTableBlockEntity table) {
		this(containerId, playerInventory, table, selectionOf(table));
	}

	private ArchitectTableMenu(int containerId, Inventory playerInventory, Container table, DataSlot selected) {
		super(ModMenus.ARCHITECT_TABLE, containerId);
		checkContainerSize(table, TABLE_SLOTS);
		this.table = table;
		this.selected = addDataSlot(selected);
		table.startOpen(playerInventory.player);

		for (int index = 0; index < TABLE_SLOTS; index++) {
			int column = index % COLUMNS;
			int row = index / COLUMNS;
			addSlot(new Slot(table, index, 8 + column * 18, 18 + row * 18));
		}

		addStandardInventorySlots(playerInventory, 8, 84);
	}

	/** O block entity guarda o nome; o menu sincroniza o índice no catálogo. */
	private static DataSlot selectionOf(ArchitectTableBlockEntity table) {
		return new DataSlot() {
			@Override
			public int get() {
				return Catalog.current().indexOf(table.schematic());
			}

			@Override
			public void set(int index) {
				table.setSchematic(Catalog.current().get(index).schematic());
			}
		};
	}

	/** Índice no catálogo, ou -1 se nada selecionado. */
	public int selectedIndex() {
		return selected.get();
	}

	public Catalog.Entry selectedEntry() {
		int index = selectedIndex();
		Catalog catalog = Catalog.current();
		return index >= 0 && index < catalog.size() ? catalog.get(index) : null;
	}

	public int countInTable(Item item) {
		int total = 0;

		for (int index = 0; index < TABLE_SLOTS; index++) {
			ItemStack stack = table.getItem(index);

			if (stack.is(item)) {
				total += stack.getCount();
			}
		}

		return total;
	}

	public boolean canGenerate() {
		Catalog.Entry entry = selectedEntry();

		if (entry == null) {
			return false;
		}

		for (Map.Entry<Item, Integer> ingredient : entry.ingredients().entrySet()) {
			if (countInTable(ingredient.getKey()) < ingredient.getValue()) {
				return false;
			}
		}

		return true;
	}

	/** Chamado no servidor quando o cliente clica num botão. */
	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == BUTTON_GENERATE) {
			return generate(player);
		}

		if (id < 0 || id >= Catalog.current().size()) {
			return false;
		}

		selected.set(id);
		return true;
	}

	private boolean generate(Player player) {
		if (!canGenerate()) {
			return false;
		}

		Catalog.Entry entry = selectedEntry();
		entry.ingredients().forEach(this::removeFromTable);

		ItemStack blueprint = new ItemStack(ModItems.BLUEPRINT);
		blueprint.set(ModDataComponents.SCHEMATIC, entry.schematic());
		player.getInventory().placeItemBackInInventory(blueprint);
		return true;
	}

	private void removeFromTable(Item item, int amount) {
		int remaining = amount;

		for (int index = 0; index < TABLE_SLOTS && remaining > 0; index++) {
			if (table.getItem(index).is(item)) {
				remaining -= table.removeItem(index, remaining).getCount();
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);

		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();
		boolean fromTable = index < TABLE_SLOTS;

		boolean moved = fromTable
				? moveItemStackTo(stack, TABLE_SLOTS, slots.size(), true)
				: moveItemStackTo(stack, 0, TABLE_SLOTS, false);

		if (!moved) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}

		return original;
	}

	@Override
	public boolean stillValid(Player player) {
		return table.stillValid(player);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		table.stopOpen(player);
	}
}
