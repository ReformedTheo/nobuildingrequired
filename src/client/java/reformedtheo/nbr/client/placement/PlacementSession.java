package reformedtheo.nbr.client.placement;

import java.util.HashSet;
import java.util.Set;

import java.nio.file.Path;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

import reformedtheo.nbr.client.litematica.Ghost;
import reformedtheo.nbr.client.litematica.LitematicaBridge;
import reformedtheo.nbr.client.schematic.BundledSchematics;

/** O ghost que o jogador está posicionando agora. Existe no máximo um por vez. */
public final class PlacementSession {
	private static final PlacementSession INSTANCE = new PlacementSession();

	private String schematicName;
	private Ghost ghost;
	private BlockPos origin;
	private Rotation rotation = Rotation.NONE;

	// Ghosts já confirmados cuja colagem ainda não terminou.
	private final Set<Ghost> pasting = new HashSet<>();

	private PlacementSession() {
	}

	public static PlacementSession get() {
		return INSTANCE;
	}

	public boolean isActive() {
		return ghost != null;
	}

	public Ghost ghost() {
		return ghost;
	}

	public String schematicName() {
		return schematicName;
	}

	public Rotation rotation() {
		return rotation;
	}

	/** Mostra o ghost em {@code origin}. Se já houver um da mesma schematic, só o move. */
	public boolean place(String name, BlockPos origin) {
		if (isActive() && name.equals(schematicName)) {
			moveTo(origin);
			return true;
		}

		cancel();

		Path file = BundledSchematics.resolve(name);

		if (file == null) {
			return false;
		}

		Ghost created = LitematicaBridge.createGhost(file, origin, Rotation.NONE);

		this.ghost = created;
		this.schematicName = name;
		this.origin = origin;
		this.rotation = Rotation.NONE;
		return true;
	}

	public void moveTo(BlockPos origin) {
		if (!isActive()) {
			return;
		}

		this.origin = origin;
		ghost.moveTo(origin, rotation);
	}

	public void rotate() {
		setRotation(rotation.getRotated(Rotation.CLOCKWISE_90));
	}

	public void setRotation(Rotation rotation) {
		if (!isActive()) {
			return;
		}

		this.rotation = rotation;
		ghost.moveTo(origin, rotation);
	}

	/** Cola no mundo e encerra a sessão. O contorno some sozinho quando a colagem termina. */
	public boolean confirm() {
		if (!isActive()) {
			return false;
		}

		Ghost confirmed = ghost;

		if (!confirmed.paste(() -> finishPaste(confirmed))) {
			return false;
		}

		pasting.add(confirmed);
		clear();
		return true;
	}

	public void cancel() {
		if (!isActive()) {
			return;
		}

		ghost.remove();
		clear();
	}

	/** Ao sair do mundo: derruba o ghost ativo e os que ainda estavam colando, senão a Litematica os persiste. */
	public void reset() {
		cancel();
		pasting.forEach(Ghost::remove);
		pasting.clear();
	}

	private void finishPaste(Ghost finished) {
		if (pasting.remove(finished)) {
			finished.remove();
		}
	}

	private void clear() {
		ghost = null;
		schematicName = null;
		origin = null;
		rotation = Rotation.NONE;
	}
}
