package ch.ethz.ether.render;

import java.nio.Buffer;
import java.util.EnumSet;

import ch.ethz.ether.render.util.IAddOnlyFloatList;
import ch.ethz.util.UpdateRequest;

public interface IRenderGroup {
	static final float[] DEFAULT_COLOR = new float[] { 1, 1, 1, 1 };

	interface ITextureData {
		Buffer getBuffer();

		int getWidth();

		int getHeight();

		int getFormat();

		void requestUpdate();

		boolean needsUpdate();
	}

	abstract class AbstractTextureData implements ITextureData {
		private UpdateRequest updater = new UpdateRequest();

		protected AbstractTextureData() {
			this(false);
		}

		protected AbstractTextureData(boolean requestUpdate) {
			if (requestUpdate)
				requestUpdate();
		}

		@Override
		public void requestUpdate() {
			updater.requestUpdate();
		}

		@Override
		public boolean needsUpdate() {
			return updater.needsUpdate();
		}
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
