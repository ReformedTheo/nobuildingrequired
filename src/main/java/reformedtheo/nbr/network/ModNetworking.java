package reformedtheo.nbr.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import reformedtheo.nbr.component.ModDataComponents;
import reformedtheo.nbr.item.ModItems;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(ConsumeBlueprintPayload.TYPE, ConsumeBlueprintPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ConsumeBlueprintPayload.TYPE, ModNetworking::onConsumeBlueprint);
	}

	private static void onConsumeBlueprint(ConsumeBlueprintPayload payload, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();

		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);

			if (stack.is(ModItems.BLUEPRINT) && payload.schematic().equals(stack.get(ModDataComponents.SCHEMATIC))) {
				stack.shrink(1);
				return;
			}
		}
	}
}
