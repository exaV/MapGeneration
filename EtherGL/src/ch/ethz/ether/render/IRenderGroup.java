package ch.ethz.ether.render;

import java.nio.Buffer;
import java.util.EnumSet;

import ch.ethz.ether.render.util.IAddOnlyFloatList;

public interface IRenderGroup {
	public static float[] DEFAULT_COLOR = new float[] { 1, 1, 1, 1};
	
	public interface ITextureData {
		Buffer getBuffer();
		int getWidth();
		int getHeight();
	}
	
	public enum Source {
		MODEL,
		TOOL;
		
		public static EnumSet<Source> ALL_SOURCES = EnumSet.allOf(Source.class);
	}
	
	public enum Type {
		POINTS,
		LINES,
		TRIANGLES,
	}
	
	enum Pass {
		DEPTH,
		TRANSPARENCY,
		OVERLAY,
		DEVICE_SPACE_OVERLAY,
		SCREEN_SPACE_OVERLAY
	}	
	
	public enum Flag {
		SHADED,
		TEXTURED,
		INTERACTIVE_VIEW_ONLY
	}
	
	public void requestUpdate();
	public boolean needsUpdate();

	public Source getSource();
	
	public Type getType();
	
	public Pass getPass();
	public void setPass(Pass pass);
	
	public boolean containsFlag(Flag flags);
	
	public void getVertices(IAddOnlyFloatList dst);
	public void getNormals(IAddOnlyFloatList dst);
	public void getColors(IAddOnlyFloatList dst);
	public void getTexCoords(IAddOnlyFloatList dst);

	public float[] getColor();
	public float getPointSize();
	public float getLineWidth();
	public ITextureData getTexData();
}
