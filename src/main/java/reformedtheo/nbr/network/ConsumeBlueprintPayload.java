package reformedtheo.nbr.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import reformedtheo.nbr.NoBuildingRequired;

/** Cliente → servidor: "comecei a construir esta schematic, consome o blueprint". */
public record ConsumeBlueprintPayload(String schematic) implements CustomPacketPayload {
	public static final Type<ConsumeBlueprintPayload> TYPE =
			new Type<>(NoBuildingRequired.id("consume_blueprint"));

	public static final StreamCodec<io.netty.buffer.ByteBuf, ConsumeBlueprintPayload> STREAM_CODEC =
			ByteBufCodecs.STRING_UTF8.map(ConsumeBlueprintPayload::new, ConsumeBlueprintPayload::schematic);

	@Override
	public Type<ConsumeBlueprintPayload> type() {
		return TYPE;
	}
}
