package ch.fhnw.ether.formats.obj;

import ch.fhnw.util.math.Vec3;

public class Face {
	public int[] vertIndices;
	public int[] normIndices;
	public int[] texIndices;
	private Vec3[] vertices;
	private Vec3[] normals;
	private TexCoord[] textures;

	public int[] getIndices() {
		return vertIndices;
	}

	public Vec3[] getVertices() {
		return vertices;
	}

	public void setIndices(int[] indices) {
		this.vertIndices = indices;
	}

	public void setVertices(Vec3[] vertices) {
		this.vertices = vertices;
	}

	public Vec3[] getNormals() {
		return normals;
	}

	public void setNormals(Vec3[] normals) {
		this.normals = normals;
	}

	public TexCoord[] getTextures() {
		return textures;
	}

	public void setTextures(TexCoord[] textures) {
		this.textures = textures;
	}
}
