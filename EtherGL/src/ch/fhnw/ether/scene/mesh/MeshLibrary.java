package ch.fhnw.ether.scene.mesh;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.scene.mesh.geometry.VertexGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.geometry.Primitives;

public class MeshLibrary {
	private final static float[] TRIANGLE_CUBE_DATA = Primitives.UNIT_CUBE_TRIANGLES;
	private final static IArrayAttribute[] ONLY_POSITION = new IArrayAttribute[]{new PositionArray()};
	private final static VertexGeometry CUBE_GEOMETRY = new VertexGeometry(
			new float[][]{TRIANGLE_CUBE_DATA}, ONLY_POSITION, PrimitiveType.TRIANGLE);
	private final static IMaterial DEFAULT_MATERIAL = new ColorMaterial(RGBA.WHITE);
		
	public static IMesh getCube() {
		//copy vertex data to prevent violation of original vertex data
		VertexGeometry geo = new VertexGeometry(CUBE_GEOMETRY);
		return new GenericMesh(geo, DEFAULT_MATERIAL);
	}
}
