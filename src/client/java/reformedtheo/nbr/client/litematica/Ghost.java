package reformedtheo.nbr.client.litematica;

import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.scheduler.TaskScheduler;
import fi.dy.masa.litematica.scheduler.tasks.TaskPasteSchematicPerChunkDirect;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.util.SchematicWorldRefresher;
import fi.dy.masa.malilib.util.position.LayerRange;

/** Um contorno de schematic visível no mundo. Criado por {@link LitematicaBridge#createGhost}. */
public final class Ghost {
	private final LitematicaSchematic schematic;
	private final SchematicPlacement placement;

	Ghost(LitematicaSchematic schematic, SchematicPlacement placement) {
		this.schematic = schematic;
		this.placement = placement;
	}

	public String name() {
		return schematic.getMetadata().getName();
	}

	public long blockCount() {
		return schematic.getMetadata().getTotalBlocks();
	}

	public void moveTo(BlockPos origin, Rotation rotation) {
		placement.setOrigin(origin, LitematicaBridge.NOOP_STRING);
		placement.setRotation(rotation, LitematicaBridge.NOOP_MESSAGE);
	}

	public void remove() {
		DataManager.getSchematicPlacementManager().removeSchematicPlacement(placement);
	}

	/** Agenda a colagem progressiva. Retorna false em servidor remoto, onde o paste direto não existe. */
	public boolean paste(Runnable onComplete) {
		Minecraft mc = Minecraft.getInstance();

		if (!mc.hasSingleplayerServer()) {
			return false;
		}

		LayerRange range = new LayerRange(SchematicWorldRefresher.INSTANCE);
		TaskPasteSchematicPerChunkDirect task =
				new TaskPasteSchematicPerChunkDirect(Collections.singletonList(placement), range, false);

		// O task roda na thread do servidor; a placement só pode ser mexida na do cliente.
		task.setCompletionListener(() -> mc.execute(onComplete));

		TaskScheduler.getServerInstanceIfExistsOrClient().scheduleTask(task, 1);
		return true;
	}
}
