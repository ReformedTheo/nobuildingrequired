package reformedtheo.nbr.block.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import reformedtheo.nbr.catalog.Catalog;
import reformedtheo.nbr.menu.ArchitectTableMenu;

/**
 * Os slots são só zona de depósito: o que serve pra receita selecionada é absorvido
 * pro ledger ({@code delivered}) e some do slot. O resto fica lá pro jogador pegar de volta.
 */
public class ArchitectTableBlockEntity extends BaseContainerBlockEntity implements ExtendedMenuProvider<BlockPos> {
	public static final int SLOTS = 27;

	private static final Codec<Map<Item, Integer>> LEDGER_CODEC = Codec
			.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT);

	private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
	private String schematic = "";
	private Map<Item, Integer> delivered = new HashMap<>();

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

	public Catalog.Entry selectedEntry() {
		return Catalog.current().entry(schematic);
	}

	public Map<Item, Integer> delivered() {
		return Collections.unmodifiableMap(delivered);
	}

	public int delivered(Item item) {
		return delivered.getOrDefault(item, 0);
	}

	/** Desconta do ledger o que a receita consumiu. */
	public void consume(Map<Item, Integer> ingredients) {
		ingredients.forEach((item, amount) -> delivered.merge(item, -amount, Integer::sum));
		delivered.values().removeIf(count -> count <= 0);
		setChanged();
	}

	@Override
	public void setChanged() {
		absorb();
		super.setChanged();

		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	private void absorb() {
		Catalog.Entry entry = selectedEntry();

		if (entry == null) {
			return;
		}

		for (ItemStack stack : items) {
			if (stack.isEmpty()) {
				continue;
			}

			Item item = stack.getItem();
			int missing = entry.ingredients().getOrDefault(item, 0) - delivered(item);

			if (missing <= 0) {
				continue;
			}

			int taken = Math.min(missing, stack.getCount());
			delivered.merge(item, taken, Integer::sum);
			stack.shrink(taken);
		}
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
		return new ArchitectTableMenu(containerId, playerInventory, this);
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putString("Schematic", schematic);
		output.store("Delivered", LEDGER_CODEC, delivered);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, items);
		schematic = input.getStringOr("Schematic", "");
		delivered = new HashMap<>(input.read("Delivered", LEDGER_CODEC).orElse(Map.of()));
	}

	// O cliente recebe schematic + ledger pra tela mostrar have/need
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
