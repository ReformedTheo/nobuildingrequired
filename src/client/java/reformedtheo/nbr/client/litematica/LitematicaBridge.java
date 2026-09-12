package reformedtheo.nbr.client.litematica;

import java.nio.file.Path;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;

/**
 * Porta de entrada para a Litematica. Este pacote é o único do mod que importa fi.dy.masa.*;
 * quando a Litematica mudar, o conserto é aqui.
 */
public final class LitematicaBridge {
	static final IStringConsumer NOOP_STRING = s -> {
	};

	static final IMessageConsumer NOOP_MESSAGE = new IMessageConsumer() {
		@Override
		public void addMessage(Message.MessageType type, String message, Object... args) {
		}

		@Override
		public void addMessage(Message.MessageType type, int lifeTime, String message, Object... args) {
		}
	};

	private LitematicaBridge() {
	}

	public static Path schematicsDirectory() {
		return DataManager.getSchematicsBaseDirectory();
	}

	/** Carrega o arquivo e mostra o contorno no mundo. */
	public static Ghost createGhost(Path file, BlockPos origin, Rotation rotation) {
		LitematicaSchematic schematic = SchematicHolder.getInstance().getOrLoad(file);
		SchematicPlacement placement = SchematicPlacement.createFor(schematic, origin, "nbr-placement", true, true);
		placement.setRotation(rotation, NOOP_MESSAGE);
		DataManager.getSchematicPlacementManager().addSchematicPlacement(placement, false);

		return new Ghost(schematic, placement);
	}
}
