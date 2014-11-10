package ch.fhnw.util.math.geometry;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.util.math.MathUtil;
import ch.fhnw.util.math.Vec3;

// #faces = 20 * f^2
// #edges = 30 * f^2
// #point = 10 * f^2 + 2
// http://wackel.home.comcast.net/~wackel/Geometry/GeoSubdivision.html

// FIXME: we still create too many points when correspondance is not set
// TODO: support for normals and tex coords

public class GeodesicSphere {
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
        { 0, 4, 1}, { 0, 9, 4 }, { 9, 5, 4 }, { 4, 5, 8 }, { 4, 8, 1 },
        { 8, 10, 1 }, { 8, 3, 10 }, { 5, 3, 8 }, { 5, 2, 3 }, { 2, 7, 3 },
        
        { 7, 10, 3 }, { 7, 6, 10 }, { 7, 11, 6 }, { 11, 0, 6 }, { 0, 1, 6 },
        { 6, 1, 10 }, { 9, 0, 11 }, { 9, 11, 2 }, { 9, 2, 5 }, { 7, 2, 11 }
    };
    
    private static final boolean[][] LFLAGS = {
    	{ true,  true,  true  }, { true,  true,  false }, { true,  true,  false }, { false, true, true  },  { false, true,  false }, 
    	{ true,  true,  false }, { true,  true,  false }, { true,  false, false }, { true,  true, false },  { true,  true,  false }, 
    	
    	{ true,  false, false }, { true,  true,  false }, { true,  true,  false }, { true,  true, false },  { false, true, false }, 
    	{ false, false, false }, { false, false, true  }, { false, true,  true  }, { false, false, false }, { false, false, false }, 
    };

    private static final boolean[][] PFLAGS = {
    	{ true,  true,  true  }, { false, true,  false }, { false, true,  false }, { false, false, true, }, { false, false, false }, 
    	{ false, true,  false }, { false, true,  false }, { false, false, false }, { false, true,  false }, { false, true,  false },
    	
    	{ false, false, false }, { false, true,  false }, { false, true,  false }, { false, false, false }, { false, false, false }, 
    	{ false, false, false }, { false, false, false }, { false, false, false }, { false, false, false }, { false, false, false }, 
    };
    //@formatter:on

	private final int depth;
	private final boolean vertexCoherence;

	private float[] points;
	private float[] lines;
	private float[] triangles;
	private float[] normals;
	private float[] texCoords;

	public GeodesicSphere(int depth) {
		this(depth, false);
	}

	public GeodesicSphere(int depth, boolean vertexCoherence) {
		this.depth = depth;
		this.vertexCoherence = vertexCoherence;
	}

	public float[] getPoints() {
		if (points == null) {
			List<Vec3> vertices = new ArrayList<>();

			if (vertexCoherence) {
				for (int i = 0; i < 20; i++)
					subdividePoints(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], vertices, true, true, true, depth);
			} else {
				for (int i = 0; i < 20; i++)
					subdividePoints(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], vertices, PFLAGS[i][0], PFLAGS[i][1],
							PFLAGS[i][2], depth);
			}
			System.out.println("# points:" + vertices.size());

			points = Vec3.toArray(vertices);
		}
		return points;
	}

	public float[] getLines() {
		if (lines == null) {
			List<Vec3> vertices = new ArrayList<>();

			if (vertexCoherence) {
				for (int i = 0; i < 20; i++)
					subdivideLines(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], vertices, true, true, true, depth);
			} else {
				for (int i = 0; i < 20; i++)
					subdivideLines(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], vertices, LFLAGS[i][0], LFLAGS[i][1],
							LFLAGS[i][2], depth);
			}
			System.out.println("# lines:" + vertices.size() / 2);

			lines = Vec3.toArray(vertices);
		}
		return lines;
	}

	public float[] getTriangles() {
		if (triangles == null) {
			List<Vec3> vertices = new ArrayList<>();

			for (int i = 0; i < 20; i++)
				subdivideTriangles(VERTICES[INDICES[i][0]], VERTICES[INDICES[i][1]], VERTICES[INDICES[i][2]], vertices, depth);

			System.out.println("# triangles:" + vertices.size() / 3);

			triangles = Vec3.toArray(vertices);
		}
		return triangles;
	}

	public float[] getNormals() {
		if (normals == null) {
			normals = getTriangles().clone();
			for (int i = 0; i < normals.length; i += 3) {
				float l = MathUtil.length(normals[i], normals[i + 1], normals[i + 2]);
				normals[i] /= l;
				normals[i + 1] /= l;
				normals[i + 2] /= l;
			}
		}
		return normals;
	}

	public float[] getTexCoords() {
		if (texCoords == null) {
			float[] vertices = getTriangles();
			float r = SCALE;
			texCoords = new float[vertices.length / 3 * 2];
			int j = 0;
			for (int i = 0; i < vertices.length;) {
				float x0 = vertices[i++];
				float y0 = vertices[i++];
				float z0 = vertices[i++];
				float s0 = toS(x0, y0);
				float t0 = toT(z0, r);
				float x1 = vertices[i++];
				float y1 = vertices[i++];
				float z1 = vertices[i++];
				float s1 = toS(x1, y1);
				float t1 = toT(z1, r);
				float x2 = vertices[i++];
				float y2 = vertices[i++];
				float z2 = vertices[i++];
				float s2 = toS(x2, y2);
				float t2 = toT(z2, r);

				boolean p0 = Math.abs(x0) == 0 && Math.abs(y0) == 0;
				boolean p1 = Math.abs(x1) == 0 && Math.abs(y1) == 0;
				boolean p2 = Math.abs(x2) == 0 && Math.abs(y2) == 0;

				if (p0) {
					if (s1 < s2 - 0.5f)
						s1 += 1;
					else if (s1 > s2 + 0.5f)
						s1 -= 1;
					s0 = (s1 + s2) / 2;
				} else if (p1) {
					if (s0 < s2 - 0.5f)
						s0 += 1;
					else if (s0 > s2 + 0.5f)
						s0 -= 1;
					s1 = (s0 + s2) / 2;
				} else if (p2) {
					if (s0 < s1 - 0.5f)
						s0 += 1;
					else if (s0 > s1 + 0.5f)
						s0 -= 1;
					s2 = (s0 + s1) / 2;
				} else {
					if (s1 < s0 - 0.5f)
						s1 += 1;
					else if (s1 > s0 + 0.5f)
						s1 -= 1;
					if (s2 < s0 - 0.5f)
						s2 += 1;
					else if (s2 > s0 + 0.5f)
						s2 -= 1;

				}

				texCoords[j++] = s0;
				texCoords[j++] = t0;
				texCoords[j++] = s1;
				texCoords[j++] = t1;
				texCoords[j++] = s2;
				texCoords[j++] = t2;
			}
		}
		return texCoords;
	}

	private float toS(float x, float y) {
		float s = (float) (0.5 * Math.atan2(y, x) / Math.PI);
		if (s < 0)
			s += 1;
		return s;
	}

	private float toT(float z, float r) {
		return 1.0f - (float) (Math.acos(z / r) / Math.PI);
	}

	private void subdividePoints(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> vertices, boolean d1, boolean d2, boolean d3, int depth) {
		if (depth == 0) {
			if (d1)
				vertices.add(v1);
			if (d2)
				vertices.add(v2);
			if (d3)
				vertices.add(v3);
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdividePoints(v1, v12, v31, vertices, d1, false, false, depth - 1);
		subdividePoints(v2, v23, v12, vertices, d2, false, false, depth - 1);
		subdividePoints(v3, v31, v23, vertices, d3, false, false, depth - 1);
		subdividePoints(v12, v23, v31, vertices, true, true, true, depth - 1);
	}

	private void subdivideLines(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> vertices, boolean d1, boolean d2, boolean d3, int depth) {
		if (depth == 0) {
			if (d1) {
				vertices.add(v1);
				vertices.add(v2);
			}
			if (d2) {
				vertices.add(v2);
				vertices.add(v3);
			}
			if (d3) {
				vertices.add(v3);
				vertices.add(v1);
			}
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdivideLines(v1, v12, v31, vertices, d1, true, d3, depth - 1);
		subdivideLines(v2, v23, v12, vertices, d2, true, d1, depth - 1);
		subdivideLines(v3, v31, v23, vertices, d3, true, d2, depth - 1);
		subdivideLines(v12, v23, v31, vertices, false, false, false, depth - 1);
	}

	private void subdivideTriangles(Vec3 v1, Vec3 v2, Vec3 v3, List<Vec3> vertices, int depth) {
		if (depth == 0) {
			vertices.add(v1);
			vertices.add(v2);
			vertices.add(v3);
			return;
		}
		Vec3 v12 = v1.add(v2).normalize().scale(SCALE);
		Vec3 v23 = v2.add(v3).normalize().scale(SCALE);
		Vec3 v31 = v3.add(v1).normalize().scale(SCALE);
		subdivideTriangles(v1, v12, v31, vertices, depth - 1);
		subdivideTriangles(v2, v23, v12, vertices, depth - 1);
		subdivideTriangles(v3, v31, v23, vertices, depth - 1);
		subdivideTriangles(v12, v23, v31, vertices, depth - 1);
	}
}
