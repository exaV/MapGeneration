package ch.ethz.ether.render;

import java.nio.Buffer;
import java.util.EnumSet;

import ch.ethz.ether.render.util.IAddOnlyFloatList;

public interface IRenderGroup {
	static final float[] DEFAULT_COLOR = new float[] { 1, 1, 1, 1 };
	static final float[] DEFAULT_QUAD_TEX_COORDS = new float[] { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 };

	interface ITextureData {
		Buffer getBuffer();

		int getWidth();

		int getHeight();

		int getFormat();
	}

	enum Source {
		MODEL, TOOL;

		public static EnumSet<Source> ALL_SOURCES = EnumSet.allOf(Source.class);
	}

	enum Type {
		POINTS, LINES, TRIANGLES,
	}

	enum Pass {
		DEPTH, TRANSPARENCY, OVERLAY, DEVICE_SPACE_OVERLAY, SCREEN_SPACE_OVERLAY
	}

	enum Flag {
		SHADED, TEXTURED, INTERACTIVE_VIEW_ONLY
	}

	void requestUpdate();

	boolean needsUpdate();

	Source getSource();

	Type getType();

	Pass getPass();

	void setPass(Pass pass);

	boolean containsFlag(Flag flags);

	void getVertices(IAddOnlyFloatList dst);

	void getNormals(IAddOnlyFloatList dst);

	void getColors(IAddOnlyFloatList dst);

	void getTexCoords(IAddOnlyFloatList dst);

	float[] getColor();

	float getPointSize();

	float getLineWidth();
	
	void requestTextureUpdate();
	
	boolean needsTextureUpdate();

	ITextureData getTextureData();
}
