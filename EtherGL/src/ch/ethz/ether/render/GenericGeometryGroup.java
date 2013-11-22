package ch.ethz.ether.render;

import java.util.EnumSet;

import ch.ethz.util.FloatList;

public class GenericGeometryGroup extends AbstractGeometryGroup {
	private float[] pointVertices;
	private float[] pointColors;
	private float pointSize;
	private float[] lineVertices;
	private float[] lineColors;
	private float lineWidth;
	private float[] triangleVertices;
	private float[] triangleColors;
	private float[] triangleNormals;
	private float[] triangleTexCoords;
	private ITextureData triangleTexData;
	
	public GenericGeometryGroup() {
	}

	public GenericGeometryGroup(EnumSet<Element> elements, EnumSet<Appearance> appearances) {
		super(elements, appearances);
	}
	
	@Override
	public void getPointVertices(FloatList dst) {
		dst.addAll(pointVertices);
	}
	
	@Override
	public void getPointColors(FloatList dst) {
		dst.addAll(pointColors);
	}
	
	@Override
	public float getPointSize() {
		return pointSize;
	}
	
	public void setPoints(float[] vertices, float[] colors, float size) {
		this.pointVertices = vertices;
		this.pointColors = colors;
		this.pointSize = size;
		requestUpdate();
	}

	@Override
	public void getLineVertices(FloatList dst) {
		dst.addAll(lineVertices);
	}
	
	@Override
	public void getLineColors(FloatList dst) {
		dst.addAll(lineColors);
	}
	
	@Override
	public float getLineWidth() {
		return lineWidth;
	}
	
	public void setLines(float[] vertices, float[] colors, float width) {
		this.lineVertices = vertices;
		this.lineColors = colors;
		this.lineWidth = width;
		requestUpdate();
	}

	@Override
	public void getTriangleVertices(FloatList dst) {
		dst.addAll(triangleVertices);
	}

	@Override
	public void getTriangleColors(FloatList dst) {
		dst.addAll(triangleColors);
	}

	@Override
	public void getTriangleNormals(FloatList dst) {
		dst.addAll(triangleNormals);
	}

	@Override
	public void getTriangleTexCoords(FloatList dst) {
		dst.addAll(triangleTexCoords);
	}
	
	@Override
	public ITextureData getTriangleTexData() {
		return triangleTexData;
	}
	
	public void setTriangles(float[] vertices, float[] colors, float[] normals, float[] texCoords, ITextureData texData) {
		this.triangleVertices = vertices;
		this.triangleColors = colors;
		this.triangleNormals = normals;
		this.triangleTexCoords = texCoords;
		this.triangleTexData = texData;
		requestUpdate();
	}	
}
