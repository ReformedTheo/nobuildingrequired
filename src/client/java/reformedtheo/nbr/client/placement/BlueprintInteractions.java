package reformedtheo.nbr.client.placement;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import reformedtheo.nbr.component.ModDataComponents;
import reformedtheo.nbr.item.ModItems;
import reformedtheo.nbr.network.ConsumeBlueprintPayload;

/**
 * Gestos do blueprint:
 *   clique em bloco → posiciona o ghost
 *   shift + clique em bloco → gira 90°
 *   clique no ar → cola
 *   shift + clique no ar → cancela
 */
public final class BlueprintInteractions {
	private BlueprintInteractions() {
	}

	public static void register() {
		UseBlockCallback.EVENT.register(BlueprintInteractions::onUseBlock);
		UseItemCallback.EVENT.register(BlueprintInteractions::onUseItem);
	}

	private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		String schematic = heldSchematic(player, hand);

		if (schematic == null) {
			return InteractionResult.PASS;
		}

		// O servidor integrado dispara o mesmo evento na thread dele. Lá só bloqueia a vanilla;
		// o ghost é coisa do cliente.
		if (!level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		PlacementSession session = PlacementSession.get();

		if (player.isShiftKeyDown()) {
			if (!session.isActive()) {
				tell(player, "Clique num bloco primeiro para posicionar");
				return InteractionResult.SUCCESS;
			}

			session.rotate();
			tell(player, "Girado para " + Rotations.toDegrees(session.rotation()) + "°");
			return InteractionResult.SUCCESS;
		}

		BlockPos origin = hit.getBlockPos().relative(hit.getDirection());

		if (!session.place(schematic, origin)) {
			tell(player, "Schematic não vem no jar: " + schematic);
			return InteractionResult.FAIL;
		}

		tell(player, session.ghost().name() + " em " + origin.toShortString());
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
		String schematic = heldSchematic(player, hand);

		if (schematic == null) {
			return InteractionResult.PASS;
		}

		// O servidor integrado dispara o mesmo evento na thread dele. Lá só bloqueia a vanilla;
		// o ghost é coisa do cliente.
		if (!level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		PlacementSession session = PlacementSession.get();

		if (!session.isActive()) {
			tell(player, "Clique num bloco primeiro para posicionar");
			return InteractionResult.SUCCESS;
		}

		if (!schematic.equals(session.schematicName())) {
			tell(player, "O ghost ativo é de outra schematic: " + session.schematicName());
			return InteractionResult.FAIL;
		}

		if (player.isShiftKeyDown()) {
			session.cancel();
			tell(player, "Cancelado");
			return InteractionResult.SUCCESS;
		}

		String name = session.ghost().name();

		if (!session.confirm()) {
			tell(player, "Só funciona em mundo local por enquanto");
			return InteractionResult.FAIL;
		}

		ClientPlayNetworking.send(new ConsumeBlueprintPayload(schematic));
		tell(player, "Construindo " + name + "...");
		return InteractionResult.SUCCESS;
	}

	private static String heldSchematic(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.is(ModItems.BLUEPRINT)) {
			return null;
		}

		return stack.get(ModDataComponents.SCHEMATIC);
	}

	private static void tell(Player player, String text) {
		player.sendOverlayMessage(Component.literal(text));
	}
}
