package ch.ethz.fcl.ether.render;

import ch.ethz.fcl.util.FloatList;


public interface IRenderGroup {
	enum Element {
		POINTS,
		LINES,
		TRIANGLES,
	}
	
	enum Appearance {
		OUTLINES_ONLY,
		COLORS_PER_VERTEX,
		SHADED,
		TEXTURED,
	}
	
	public boolean containsElement(Element element);
	public boolean containsAppearance(Appearance appearance);
	
	public void getPointVertices(FloatList dst);
	public void getPointColors(FloatList dst);
	
	public void getLineVertices(FloatList dst);
	public void getLineColors(FloatList dst);
	
	// TODO normals & tex coords currently missing
	public void getTriangleVertices(FloatList dst);
	public void getTriangleColors(FloatList dst);
	
	public void requestUpdate();
	public boolean needsUpdate();
}
