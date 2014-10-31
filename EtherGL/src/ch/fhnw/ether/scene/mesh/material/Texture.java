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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import ch.fhnw.util.UpdateRequest;

/**
 * Texture data encapsulation (FIXME: needs extension/generalization, array tex, 3d tex etc)
 *
 * @author radar
 */
public class Texture {
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
		BufferedImage image = null;
		try {
			image = ImageIO.read(url);
			// flip the image vertically (alternatively, we could adjust tex coords, but for clarity, we flip the image)
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);
			setData(image.getWidth(), image.getHeight(), ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()), GL.GL_RGB);
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
}
