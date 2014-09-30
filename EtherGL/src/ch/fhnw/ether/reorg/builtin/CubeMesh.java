package ch.fhnw.ether.reorg.builtin;

import java.util.Arrays;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.util.Primitives;
import ch.fhnw.ether.reorg.base.GenericGeometry;
import ch.fhnw.ether.reorg.base.SimpleMesh;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class CubeMesh extends SimpleMesh {
	
	private final static int len = Primitives.UNIT_CUBE_TRIANGLES.length;
	private final static float[] positions = Primitives.UNIT_CUBE_TRIANGLES;
	private final static float[] colors = generateColors(RGB.WHITE);

	private final static IArrayAttribute[] attributes = {new PositionArray(), new ColorArray()};
	
	public CubeMesh() {
		this(Vec3.ZERO, Vec3.ZERO, Vec3.ONE);
	}
	
	public CubeMesh(Vec3 position, Vec3 rotation, Vec3 scale) {
		super(new GenericGeometry(new float[][]{Arrays.copyOf(positions, len), Arrays.copyOf(colors, len)}, attributes), position, rotation, scale);
	}
	
	private static float[] generateColors(RGB rgb){
		float[] ret = new float[len];
		for(int i=0; i<ret.length; i+=3) {
			ret[i+0] = rgb.x;
			ret[i+1] = rgb.y;
			ret[i+2] = rgb.z;
		}
		return ret;
	}

}
