package reformedtheo.nbr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.minecraft.client.gui.screens.MenuScreens;

import reformedtheo.nbr.client.command.NbrDebugCommand;
import reformedtheo.nbr.client.placement.BlueprintInteractions;
import reformedtheo.nbr.client.placement.PlacementSession;
import reformedtheo.nbr.client.screen.ArchitectTableScreen;
import reformedtheo.nbr.menu.ModMenus;

public class NoBuildingRequiredClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.ARCHITECT_TABLE, ArchitectTableScreen::new);
		BlueprintInteractions.register();
		NbrDebugCommand.register();

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PlacementSession.get().reset());
	}
}
