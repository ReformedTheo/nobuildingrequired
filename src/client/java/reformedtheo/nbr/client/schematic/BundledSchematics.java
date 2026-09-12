package reformedtheo.nbr.client.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import reformedtheo.nbr.NoBuildingRequired;
import reformedtheo.nbr.client.litematica.LitematicaBridge;

/**
 * As schematics que vêm dentro do jar. A Litematica só lê de arquivo, então cada uma é
 * extraída para a pasta de schematics do jogador na primeira vez que é usada na sessão.
 */
public final class BundledSchematics {
	private static final String RESOURCE_DIR = "/nobuildingrequired/schematics/";
	private static final String EXTENSION = ".litematic";
	private static final String TARGET_DIR = NoBuildingRequired.MOD_ID;

	private static final Set<String> extracted = new HashSet<>();

	private BundledSchematics() {
	}

	/** Caminho em disco da schematic, ou null se ela não vem no jar. */
	public static Path resolve(String name) {
		Path target = LitematicaBridge.schematicsDirectory().resolve(TARGET_DIR).resolve(name + EXTENSION);

		if (extracted.contains(name)) {
			return target;
		}

		try (InputStream in = BundledSchematics.class.getResourceAsStream(RESOURCE_DIR + name + EXTENSION)) {
			if (in == null) {
				return null;
			}

			Files.createDirectories(target.getParent());
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			extracted.add(name);
			return target;
		} catch (IOException e) {
			NoBuildingRequired.LOGGER.error("Não consegui extrair a schematic {}", name, e);
			return null;
		}
	}
}
