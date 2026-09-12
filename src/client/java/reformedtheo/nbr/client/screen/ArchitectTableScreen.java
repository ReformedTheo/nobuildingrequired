package reformedtheo.nbr.client.screen;

import java.util.Map;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import reformedtheo.nbr.catalog.Catalog;
import reformedtheo.nbr.menu.ArchitectTableMenu;

public class ArchitectTableScreen extends AbstractContainerScreen<ArchitectTableMenu> {
	// Textura do baú da vanilla: 3 fileiras em cima, inventário do jogador a partir de v=126.
	private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
	private static final int ROWS = 3;
	private static final int TOP_HEIGHT = ROWS * 18 + 17;
	private static final int BOTTOM_HEIGHT = 96;

	private static final int PANEL_GAP = 4;
	private static final int PANEL_WIDTH = 120;
	private static final int PANEL_PADDING = 6;
	private static final int LINE_HEIGHT = 18;

	private static final int WHITE = 0xFFFFFFFF;
	private static final int GREEN = 0xFF55FF55;
	private static final int RED = 0xFFFF5555;
	private static final int PANEL_BACKGROUND = 0xC0101010;

	private int panelLeft;
	private Button generate;

	public ArchitectTableScreen(ArchitectTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, TOP_HEIGHT + BOTTOM_HEIGHT);
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		panelLeft = leftPos + imageWidth + PANEL_GAP;

		int y = topPos + PANEL_PADDING;
		addRenderableWidget(Button.builder(Component.literal("<"), b -> select(-1))
				.bounds(panelLeft + PANEL_PADDING, y, 20, 20).build());
		addRenderableWidget(Button.builder(Component.literal(">"), b -> select(+1))
				.bounds(panelLeft + PANEL_WIDTH - PANEL_PADDING - 20, y, 20, 20).build());

		generate = addRenderableWidget(Button.builder(Component.translatable("screen.nobuildingrequired.architect_table.generate"), b -> generate())
				.bounds(panelLeft + PANEL_PADDING, topPos + imageHeight - PANEL_PADDING - 20, PANEL_WIDTH - 2 * PANEL_PADDING, 20).build());
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		generate.active = menu.canGenerate();
	}

	private void generate() {
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, ArchitectTableMenu.BUTTON_GENERATE);
	}

	/** Avança ou volta no catálogo. O servidor valida e sincroniza de volta pelo DataSlot. */
	private void select(int delta) {
		int size = Catalog.current().size();

		if (size == 0) {
			return;
		}

		int next = Math.floorMod(menu.selectedIndex() + delta, size);
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, next);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, TOP_HEIGHT, 256, 256);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos + TOP_HEIGHT, 0, 126, imageWidth, BOTTOM_HEIGHT, 256, 256);
		extractPanel(graphics);
	}

	private void extractPanel(GuiGraphicsExtractor graphics) {
		graphics.fill(panelLeft, topPos, panelLeft + PANEL_WIDTH, topPos + imageHeight, PANEL_BACKGROUND);

		Catalog catalog = Catalog.current();
		int x = panelLeft + PANEL_PADDING;
		int y = topPos + PANEL_PADDING + 6;

		if (catalog.size() == 0) {
			graphics.text(font, Component.translatable("screen.nobuildingrequired.architect_table.no_catalog"), x, y, RED);
			return;
		}

		int index = menu.selectedIndex();

		if (index < 0 || index >= catalog.size()) {
			graphics.text(font, Component.translatable("screen.nobuildingrequired.architect_table.none_selected"), x + 22, y, WHITE);
			return;
		}

		Catalog.Entry entry = catalog.get(index);
		graphics.text(font, entry.schematic(), x + 22, y, WHITE);

		y += 24;

		for (Map.Entry<Item, Integer> ingredient : entry.ingredients().entrySet()) {
			int have = menu.countInTable(ingredient.getKey());
			int need = ingredient.getValue();

			graphics.item(new ItemStack(ingredient.getKey()), x, y);
			graphics.text(font, have + " / " + need, x + 20, y + 4, have >= need ? GREEN : RED);
			y += LINE_HEIGHT;
		}
	}
}
