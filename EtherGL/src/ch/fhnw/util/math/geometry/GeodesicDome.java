package ch.fhnw.util.math.geometry;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.util.math.Vec3;

// adapted from here: http://stackoverflow.com/questions/17705621/algorithm-for-a-geodesic-sphere
public class GeodesicDome {
	private static final float SCALE = 0.5f;
	
	private static final float X = SCALE * 0.525731112119133606f;
	private static final float Z = SCALE * 0.850650808352039932f;

	//@formatter:off
    private static final Vec3[] VERTICES = {
        new Vec3(-X, 0.0, Z), new Vec3(X, 0.0, Z), new Vec3(-X, 0.0, -Z ), new Vec3(X, 0.0, -Z),
        new Vec3( 0.0, Z, X ), new Vec3(0.0, Z, -X), new Vec3(0.0, -Z, X ), new Vec3(0.0, -Z, -X),
        new Vec3( Z, X, 0.0 ), new Vec3(-Z, X, 0.0), new Vec3(Z, -X, 0.0 ), new Vec3(-Z, -X, 0.0)
    };
    
    private static final int[][] INDICES = {
        {0, 4, 1}, { 0, 9, 4 }, { 9, 5, 4 }, { 4, 5, 8 }, { 4, 8, 1 },
        { 8, 10, 1 }, { 8, 3, 10 }, { 5, 3, 8 }, { 5, 2, 3 }, { 2, 7, 3 },
        { 7, 10, 3 }, { 7, 6, 10 }, { 7, 11, 6 }, { 11, 0, 6 }, { 0, 1, 6 },
        { 6, 1, 10 }, { 9, 0, 11 }, { 9, 11, 2 }, { 9, 2, 5 }, { 7, 2, 11 }
    };
    //@formatter:on

	private final int depth;

	public GeodesicDome(int depth) {
		this.depth = depth;
	}

	public float[] getPoints() {
		List<Vec3> points = new ArrayList<>();

		for (int i = 0; i < 20; i++)
			subdividePoints(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], points, depth);

		return Vec3.toArray(points);
	}

	public float[] getLines() {
		List<Vec3> points = new ArrayList<>();

		for (int i = 0; i < 20; i++)
			subdivideLines(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], points, depth);

		return Vec3.toArray(points);
	}

	public float[] getTriangles() {
		List<Vec3> points = new ArrayList<>();

		for (int i = 0; i < 20; i++)
			subdivideTriangles(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], points, depth);

		return Vec3.toArray(points);
	}

	// FIXME: we create too many points
	private void subdividePoints(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> points, int depth) {
		if (depth == 0) {
			points.add(v1);
			points.add(v2);
			//points.add(v3);
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdividePoints(v1, v12, v31, points, depth - 1);
		subdividePoints(v2, v23, v12, points, depth - 1);
		subdividePoints(v3, v31, v23, points, depth - 1);
		subdividePoints(v12, v23, v31, points, depth - 1);
	}

	// FIXME: we create too many lines
	private void subdivideLines(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> points, int depth) {
		if (depth == 0) {
			points.add(v1);
			points.add(v2);
			points.add(v2);
			points.add(v3);
			//points.add(v3);
			//points.add(v1);
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdivideLines(v1, v12, v31, points, depth - 1);
		subdivideLines(v2, v23, v12, points, depth - 1);
		subdivideLines(v3, v31, v23, points, depth - 1);
		subdivideLines(v12, v23, v31, points, depth - 1);
	}

	private void subdivideTriangles(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> points, int depth) {
		if (depth == 0) {
			points.add(v1);
			points.add(v2);
			points.add(v3);
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdivideTriangles(v1, v12, v31, points, depth - 1);
		subdivideTriangles(v2, v23, v12, points, depth - 1);
		subdivideTriangles(v3, v31, v23, points, depth - 1);
		subdivideTriangles(v12, v23, v31, points, depth - 1);
	}
}
