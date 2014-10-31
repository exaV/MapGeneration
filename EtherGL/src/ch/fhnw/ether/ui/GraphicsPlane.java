/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.EnumSet;

import javax.media.opengl.GL;

import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IAttribute;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Flags;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.PrimitiveType;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.util.UpdateRequest;

class GraphicsPlane {
	public static final Font FONT = new Font("SansSerif", Font.BOLD, 12);

	private static final Color CLEAR_COLOR = new Color(0, 0, 0, 0);

	private final UpdateRequest updater = new UpdateRequest();

	private final DefaultMesh mesh;

	private final BufferedImage image;
	private final Graphics2D graphics;
	private final Texture texture = new Texture();

	private int x;
	private int y;
	private int w;
	private int h;
	

	public GraphicsPlane(int x, int y, int w, int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setFont(FONT);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		IAttribute[] attribs = { IMaterial.POSITION_ARRAY, IMaterial.COLOR_MAP_ARRAY };
		float[] position = { x, y, 0, x + w, y, 0, x + w, y + h, 0, x, y, 0, x + w, y + h, 0, x, y + h, 0 };
		float[] texCoords = MeshLibrary.DEFAULT_QUAD_TEX_COORDS;
		IGeometry geometry = new DefaultGeometry(PrimitiveType.TRIANGLES, attribs, new float[][] { position, texCoords });
		IMaterial material = new ColorMapMaterial(texture);

		mesh = new DefaultMesh(material, geometry, Pass.SCREEN_SPACE_OVERLAY, EnumSet.of(Flags.INTERACTIVE_VIEWS_ONLY));
	}

	public final Texture getTexture() {
		return texture;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final int getWidth() {
		return w;
	}

	public final int getHeight() {
		return h;
	}

	public final void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		requestUpdate();
	}

	public void clear() {
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		fillRect(CLEAR_COLOR, x, y, w, h);
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		requestUpdate();
	}

	public void fillRect(Color color, int x, int y, int w, int h) {
		graphics.setColor(color);
		graphics.fillRect(x, y, w, h);
		requestUpdate();
	}

	public void drawString(String string, int x, int y) {
		drawString(Color.WHITE, string, x, y);
		requestUpdate();
	}

	public void drawString(Color color, String string, int x, int y) {
		graphics.setColor(color);
		graphics.drawString(string, x, y);
		requestUpdate();
	}

	public void drawStrings(String[] strings, int x, int y) {
		drawStrings(Color.WHITE, strings, x, y);
	}

	public void drawStrings(Color color, String[] strings, int x, int y) {
		graphics.setColor(color);
		int dy = 0;
		for (String s : strings) {
			graphics.drawString(s, x, y + dy);
			dy += FONT.getSize();
		}
		requestUpdate();
	}

	public IMesh getMesh() {
		return mesh;
	}
	
	public void update() {
		if (updater.needsUpdate())
			texture.setData(w, h, IntBuffer.wrap(((DataBufferInt) image.getRaster().getDataBuffer()).getData()), GL.GL_BGRA);			
	}

	private void requestUpdate() {
		updater.requestUpdate();
	}
}
