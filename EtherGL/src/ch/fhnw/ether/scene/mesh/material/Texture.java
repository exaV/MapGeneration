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

package ch.fhnw.ether.scene.mesh.material;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import ch.fhnw.util.UpdateRequest;

/**
 * Texture data encapsulation (FIXME: needs extension/generalization, array tex, 3d tex etc)
 *
 * @author radar
 */
public class Texture {
	private static final ColorModel GL_SRGBA_MODEL = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false,
			ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

	private static final ColorModel GL_SRGB_MODEL = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 }, false, false,
			ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

	private final UpdateRequest updater = new UpdateRequest();

	private int width;
	private int height;
	private Buffer buffer;
	private int format;

	public Texture() {
	}

	public Texture(URL url) {
		setData(url);
	}

	public void setData(URL url) {
		try {
			BufferedImage image = ImageIO.read(url);
			Buffer buffer = convertImage(image, true);
			setData(image.getWidth(), image.getHeight(), buffer, image.getColorModel().hasAlpha() ? GL.GL_RGBA : GL.GL_RGB);
		} catch (Exception e) {
			throw new IllegalArgumentException("can't load image " + url);
		}
	}

	public void setData(int width, int height, Buffer buffer, int format) {
		this.width = width;
		this.height = height;
		this.buffer = buffer;
		this.format = format;
		updater.requestUpdate();
	}

	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Buffer getBuffer() {
		return buffer;
	}

	public int getFormat() {
		return format;
	}

	@Override
	public String toString() {
		return "texture[w=" + width + " h=" + height + "]";
	}

	// FIXME: this is incredibly slow...
	private Buffer convertImage(BufferedImage image, boolean flipVertically) {
		int w = image.getWidth();
		int h = image.getHeight();
		boolean alpha = image.getColorModel().hasAlpha();
		boolean premult = image.getColorModel().isAlphaPremultiplied();

		BufferedImage tex;
		if (alpha) {
			WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, w, h, 4, null);
			tex = new BufferedImage(GL_SRGBA_MODEL, raster, premult, new Hashtable<>());
		} else {
			WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, w, h, 3, null);
			tex = new BufferedImage(GL_SRGB_MODEL, raster, premult, new Hashtable<>());
		}

		Graphics2D g = tex.createGraphics();
		g.setComposite(AlphaComposite.Src);
		if (flipVertically) {
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -image.getHeight(null));
			g.setTransform(tx);
		}
		g.drawImage(image, 0, 0, null);

		byte[] data = ((DataBufferByte) tex.getRaster().getDataBuffer()).getData();

		ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(data, 0, data.length);

		return buffer;
	}
}
