package reformedtheo.nbr.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import reformedtheo.nbr.catalog.Catalog;
import reformedtheo.nbr.client.litematica.Ghost;
import reformedtheo.nbr.client.placement.PlacementSession;
import reformedtheo.nbr.client.placement.Rotations;

/** Ferramenta de desenvolvimento. Espelha o item blueprint em forma de comando. */
public final class NbrDebugCommand {
	private NbrDebugCommand() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
				ClientCommands.literal("nbr")
						.then(ClientCommands.literal("list")
								.executes(ctx -> list(ctx.getSource())))
						.then(ClientCommands.literal("ghost")
								.then(ClientCommands.argument("schematic", StringArgumentType.string())
										.executes(ctx -> ghost(
												ctx.getSource(),
												StringArgumentType.getString(ctx, "schematic"),
												0))
										.then(ClientCommands.argument("graus", IntegerArgumentType.integer(0, 270))
												.executes(ctx -> ghost(
														ctx.getSource(),
														StringArgumentType.getString(ctx, "schematic"),
														IntegerArgumentType.getInteger(ctx, "graus"))))))
						.then(ClientCommands.literal("paste")
								.executes(ctx -> paste(ctx.getSource())))
						.then(ClientCommands.literal("clear")
								.executes(ctx -> clear(ctx.getSource())))));
	}

	private static int list(FabricClientCommandSource source) {
		Catalog catalog = Catalog.current();

		if (catalog.size() == 0) {
			say(source, "Catálogo vazio.");
			return 0;
		}

		say(source, catalog.size() + " schematic(s) no jar:");
		catalog.entries().forEach(e -> say(source, "  " + e.schematic()));
		return catalog.size();
	}

	private static int ghost(FabricClientCommandSource source, String name, int degrees) {
		PlacementSession session = PlacementSession.get();
		BlockPos origin = source.getPlayer().blockPosition();

		if (!session.place(name, origin)) {
			say(source, "'" + name + "' não vem no jar. Veja /nbr list.");
			return 0;
		}

		session.setRotation(Rotations.fromDegrees(degrees));

		Ghost ghost = session.ghost();
		say(source, ghost.name() + " (" + ghost.blockCount() + " blocos) em " + origin.toShortString() + ", " + degrees + " graus");
		return 1;
	}

	private static int paste(FabricClientCommandSource source) {
		PlacementSession session = PlacementSession.get();

		if (!session.isActive()) {
			say(source, "Nenhum ghost ativo. Use /nbr ghost <schematic> primeiro.");
			return 0;
		}

		if (!session.confirm()) {
			say(source, "Só funciona em mundo local por enquanto.");
			return 0;
		}

		say(source, "Colando...");
		return 1;
	}

	private static int clear(FabricClientCommandSource source) {
		PlacementSession session = PlacementSession.get();

		if (!session.isActive()) {
			say(source, "Nenhum ghost ativo.");
			return 0;
		}

		session.cancel();
		say(source, "Ghost removido.");
		return 1;
	}

	private static void say(FabricClientCommandSource source, String text) {
		source.sendFeedback(Component.literal(text));
	}
}
