package ch.fhnw.ether.formats.obj;

import java.util.ArrayList;

import ch.fhnw.util.IntList;
import ch.fhnw.util.math.Vec3;

public class Group {

	private String name;
	private Vec3 min = null;
	private Material material;
	private ArrayList<Face> faces = new ArrayList<Face>();

	public IntList indices = new IntList();
	public ArrayList<Vec3> vertices = new ArrayList<Vec3>();
	public ArrayList<Vec3> normals = new ArrayList<Vec3>();
	public ArrayList<TexCoord> texcoords = new ArrayList<TexCoord>();
	public int indexCount;

	public Group(String name) {
		indexCount = 0;
		this.name = name;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void addFace(Face face) {
		faces.add(face);
	}

	public void pack() {
		float minX = 0;
		float minY = 0;
		float minZ = 0;
		Face currentFace = null;
		Vec3 currentVertex = null;
		for (int i = 0; i < faces.size(); i++) {
			currentFace = faces.get(i);
			for (int j = 0; j < currentFace.getVertices().length; j++) {
				currentVertex = currentFace.getVertices()[j];
				if (Math.abs(currentVertex.x) > minX)
					minX = Math.abs(currentVertex.x);
				if (Math.abs(currentVertex.y) > minY)
					minY = Math.abs(currentVertex.y);
				if (Math.abs(currentVertex.z) > minZ)
					minZ = Math.abs(currentVertex.z);
			}
		}

		min = new Vec3(minX, minY, minZ);
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return material;
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public Vec3 getMin() {
		return min;
	}
}
