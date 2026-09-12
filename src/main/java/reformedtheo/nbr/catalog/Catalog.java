package reformedtheo.nbr.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import reformedtheo.nbr.NoBuildingRequired;

/** As schematics que vêm no jar e o que cada uma custa. Ordenado por nome, então o índice é o mesmo nos dois lados. */
public record Catalog(List<Entry> entries) {
	public record Entry(String schematic, Map<Item, Integer> ingredients) {
	}

	public static final String RESOURCE = "/nobuildingrequired/catalog.json";

	// Ids ficam como String: item desconhecido só gera aviso, não derruba o catálogo inteiro
	public static final Codec<Catalog> CODEC = Codec
			.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, Codec.INT))
			.xmap(Catalog::fromMap, Catalog::toMap);

	private static Catalog current;

	public static Catalog current() {
		if (current == null) {
			current = load();
		}

		return current;
	}

	public int size() {
		return entries.size();
	}

	public Entry get(int index) {
		return entries.get(index);
	}

	public int indexOf(String schematic) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).schematic().equals(schematic)) {
				return i;
			}
		}

		return -1;
	}

	/** Entrada pelo nome, ou null se não existe. */
	public Entry entry(String schematic) {
		int index = indexOf(schematic);
		return index >= 0 ? entries.get(index) : null;
	}

	private static Catalog load() {
		try (InputStream in = Catalog.class.getResourceAsStream(RESOURCE)) {
			if (in == null) {
				NoBuildingRequired.LOGGER.error("{} não está no jar", RESOURCE);
				return new Catalog(List.of());
			}

			JsonElement json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));

			return CODEC.parse(JsonOps.INSTANCE, json)
					.resultOrPartial(error -> NoBuildingRequired.LOGGER.error("{} inválido: {}", RESOURCE, error))
					.orElse(new Catalog(List.of()));
		} catch (IOException e) {
			NoBuildingRequired.LOGGER.error("Não consegui ler {}", RESOURCE, e);
			return new Catalog(List.of());
		}
	}

	private static Catalog fromMap(Map<String, Map<String, Integer>> map) {
		List<Entry> entries = map.entrySet().stream()
				.map(e -> new Entry(e.getKey(), Map.copyOf(resolveItems(e.getKey(), e.getValue()))))
				.sorted(Comparator.comparing(Entry::schematic))
				.toList();

		return new Catalog(entries);
	}

	/** Resolve ids no registro; desconhecido (rename da Mojang, mod removido) é descartado com aviso. */
	public static Map<Item, Integer> resolveItems(String context, Map<String, Integer> counts) {
		Map<Item, Integer> items = new LinkedHashMap<>();

		counts.forEach((id, count) -> {
			Identifier key = Identifier.tryParse(id);
			Item item = key == null ? null : BuiltInRegistries.ITEM.getOptional(key).orElse(null);

			if (item == null) {
				NoBuildingRequired.LOGGER.warn("{}: item desconhecido '{}', ignorando", context, id);
			} else {
				items.merge(item, count, Integer::sum);
			}
		});

		return items;
	}

	public static Map<String, Integer> toIds(Map<Item, Integer> items) {
		Map<String, Integer> ids = new LinkedHashMap<>();
		items.forEach((item, count) -> ids.put(BuiltInRegistries.ITEM.getKey(item).toString(), count));
		return ids;
	}

	private static Map<String, Map<String, Integer>> toMap(Catalog catalog) {
		Map<String, Map<String, Integer>> map = new LinkedHashMap<>();
		catalog.entries().forEach(e -> map.put(e.schematic(), toIds(e.ingredients())));
		return map;
	}
}
