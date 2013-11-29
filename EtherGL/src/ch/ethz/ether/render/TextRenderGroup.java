package ch.ethz.ether.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.Buffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import ch.ethz.ether.render.util.IAddOnlyFloatList;
import ch.ethz.ether.render.util.Primitives;

public class TextRenderGroup extends AbstractRenderGroup {
	public static final Font FONT = new Font("SansSerif", Font.BOLD, 12);

	private static final float[] TEX_COORDS = new float[] { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 };
	private BufferedImage image;
	private Graphics2D graphics;
	private int x;
	private int y;
	private int w;
	private int h;

	private ITextureData textureData = new AbstractTextureData() {
		@Override
		public int getWidth() {
			return w;
		}

		@Override
		public int getHeight() {
			return h;
		}

		@Override
		public Buffer getBuffer() {
			return IntBuffer.wrap(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
		}

		@Override
		public int getFormat() {
			return GL.GL_BGRA;
		}
	};
	
	public TextRenderGroup(Source source, int x, int y, int w, int h) {
		super(source, Type.TRIANGLES, Pass.SCREEN_SPACE_OVERLAY);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setFont(FONT);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		requestUpdate();
	}
	
	public void clear() {
		graphics.setColor(Color.RED);
		//graphics.setColor(new Color(0, 0, 0, 0));
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.setColor(Color.WHITE);
		graphics.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
		requestTexureUpdate();
	}

	public void drawString(String string, int x, int y) {
		graphics.setColor(Color.WHITE);
		graphics.drawString(string, x, y);
		requestTexureUpdate();
	}
	
	public void drawStrings(String[] strings, int x, int y) {
		graphics.setColor(Color.WHITE);
		int dy = 0;
		for (String s : strings) {
			graphics.drawString(s, x, y + dy);
			dy += FONT.getSize();
		}
		requestTexureUpdate();
	}

	@Override
	public void getVertices(IAddOnlyFloatList dst) {
		Primitives.addRectangle(dst, x, y, x + w, y + h);
	};

	@Override
	public void getTexCoords(IAddOnlyFloatList dst) {
		dst.addAll(TEX_COORDS);
	}

	@Override
	public ITextureData getTextureData() {
		return textureData;
	}
	
	public void requestTexureUpdate() {
		textureData.requestUpdate();		
	}
}
