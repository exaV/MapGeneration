package ch.ethz.ether.render;

import ch.ethz.util.FloatList;

public interface IRenderGroup {
	public interface ITextureData {
		byte[] getRGBAData();
		int getWidth();
		int getHeight();
	}
	
	public enum Element {
		POINTS,
		LINES,
		TRIANGLES,
	}
	
	public enum Appearance {
		SHADED,
		TEXTURED,
	}
	
	public boolean containsElement(Element element);
	public boolean containsAppearance(Appearance appearance);
	
	public void getPointVertices(FloatList dst);
	public void getPointColors(FloatList dst);
	public float getPointSize();
	
	public void getLineVertices(FloatList dst);
	public void getLineColors(FloatList dst);
	public float getLineWidth();
	
	public void getTriangleVertices(FloatList dst);
	public void getTriangleColors(FloatList dst);
	public void getTriangleNormals(FloatList dst);
	public void getTriangleTexCoords(FloatList dst);
	public ITextureData getTriangleTexData();
	
	public void requestUpdate();
	public boolean needsUpdate();
}
