package reformedtheo.nbr.client.placement;

import net.minecraft.world.level.block.Rotation;

public final class Rotations {
	private Rotations() {
	}

	public static Rotation fromDegrees(int degrees) {
		return switch (degrees) {
			case 90 -> Rotation.CLOCKWISE_90;
			case 180 -> Rotation.CLOCKWISE_180;
			case 270 -> Rotation.COUNTERCLOCKWISE_90;
			default -> Rotation.NONE;
		};
	}

	public static int toDegrees(Rotation rotation) {
		return switch (rotation) {
			case CLOCKWISE_90 -> 90;
			case CLOCKWISE_180 -> 180;
			case COUNTERCLOCKWISE_90 -> 270;
			case NONE -> 0;
		};
	}
}
