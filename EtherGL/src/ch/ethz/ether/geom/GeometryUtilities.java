package ch.ethz.ether.geom;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.ether.render.util.FloatList;

public class GeometryUtilities {
	// @formatter:off
	private static final float[] UNIT_CUBE_FACES = {
		1, 1, 0, 0, 1, 0, 1, 1, 1,
		0, 1, 0, 0, 1, 1, 1, 1, 1,

		0, 1, 0, 0, 0, 0, 0, 0, 1,
		0, 1, 0, 0, 0, 1, 0, 1, 1,

		0, 0, 0, 1, 0, 0, 1, 0, 1,
		0, 0, 0, 1, 0, 1, 0, 0, 1,
		
		1, 0, 0, 1, 1, 0, 1, 1, 1,
		1, 0, 0, 1, 1, 1, 1, 0, 1,
		
		0, 0, 1, 1, 0, 1, 1, 1, 1,
		0, 0, 1, 1, 1, 1, 0, 1, 1,
	};
	// @formatter:on

	public static FloatList addCube(FloatList dst, float tx, float ty, float sx, float sy, float sz) {
		dst.ensureCapacity(dst.size() + UNIT_CUBE_FACES.length * 3);
		for (int i = 0; i < UNIT_CUBE_FACES.length; i += 3) {
			dst.add((UNIT_CUBE_FACES[i] * sx) + tx);
			dst.add((UNIT_CUBE_FACES[i + 1] * sy) + ty);
			dst.add((UNIT_CUBE_FACES[i + 2] * sz));
		}
		return dst;
	}
	
	public static float[] calculateNormals(float[] faces) {
		float[] normals = new float[faces.length];
		for (int i = 0; i < faces.length; i += 9) {
			Vector3D n = null;
			try {
				Vector3D a = new Vector3D(faces[i + 3] - faces[i + 0], faces[i + 4] - faces[i + 1], faces[i + 5] - faces[i + 2]);
				Vector3D b = new Vector3D(faces[i + 6] - faces[i + 0], faces[i + 7] - faces[i + 1], faces[i + 8] - faces[i + 2]);
				n = Vector3D.crossProduct(a, b).normalize();
			} catch (Exception e) {
				n = new Vector3D(0, 0, 1);
			}
			normals[i + 0] = normals[i + 3] = normals[i + 6] = (float) n.getX();
			normals[i + 1] = normals[i + 4] = normals[i + 7] = (float) n.getY();
			normals[i + 2] = normals[i + 5] = normals[i + 8] = (float) n.getZ();
		}
		return normals;
	}	
}
