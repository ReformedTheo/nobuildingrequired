package reformedtheo.nbr.menu;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

	/** Um ingrediente da receita selecionada e quanto já foi entregue. */
	public record Requirement(Item item, int have, int need) {
		public boolean done() {
			return have >= need;
		}

		public int remaining() {
			return need - have;
		}
	}

	private final ArchitectTableBlockEntity table;

	/** Lado do cliente: o block entity já está sincronizado nessa posição. */
	public ArchitectTableMenu(int containerId, Inventory playerInventory, BlockPos pos) {
		this(containerId, playerInventory, tableAt(playerInventory, pos));
	}

	public ArchitectTableMenu(int containerId, Inventory playerInventory, ArchitectTableBlockEntity table) {
		super(ModMenus.ARCHITECT_TABLE, containerId);
		this.table = table;
		table.startOpen(playerInventory.player);

		for (int index = 0; index < TABLE_SLOTS; index++) {
			int column = index % COLUMNS;
			int row = index / COLUMNS;
			addSlot(new Slot(table, index, 8 + column * 18, 18 + row * 18));
		}

		addStandardInventorySlots(playerInventory, 8, 84);
	}

	private static ArchitectTableBlockEntity tableAt(Inventory playerInventory, BlockPos pos) {
		if (playerInventory.player.level().getBlockEntity(pos) instanceof ArchitectTableBlockEntity table) {
			return table;
		}

		throw new IllegalStateException("Não tem Architect Table em " + pos);
	}

	/** Índice no catálogo, ou -1 se nada selecionado. */
	public int selectedIndex() {
		return Catalog.current().indexOf(table.schematic());
	}

	public Catalog.Entry selectedEntry() {
		return table.selectedEntry();
	}

	/** Faltantes primeiro (maior falta no topo), depois os completos. */
	public List<Requirement> requirements() {
		Catalog.Entry entry = selectedEntry();

		if (entry == null) {
			return List.of();
		}

		return entry.ingredients().entrySet().stream()
				.map(e -> new Requirement(e.getKey(), table.delivered(e.getKey()), e.getValue()))
				.sorted(Comparator.comparing(Requirement::done).thenComparing(Requirement::remaining, Comparator.reverseOrder()))
				.toList();
	}

	public boolean canGenerate() {
		Catalog.Entry entry = selectedEntry();
		return entry != null && requirements().stream().allMatch(Requirement::done);
	}

	/** Chamado no servidor quando o cliente clica num botão. */
	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == BUTTON_GENERATE) {
			return generate(player);
		}

		Catalog catalog = Catalog.current();

		if (id < 0 || id >= catalog.size()) {
			return false;
		}

		table.setSchematic(catalog.get(id).schematic());
		return true;
	}

	private boolean generate(Player player) {
		if (!canGenerate()) {
			return false;
		}

		Catalog.Entry entry = selectedEntry();
		table.consume(entry.ingredients());

		ItemStack blueprint = new ItemStack(ModItems.BLUEPRINT);
		blueprint.set(ModDataComponents.SCHEMATIC, entry.schematic());
		player.getInventory().placeItemBackInInventory(blueprint);
		return true;
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
