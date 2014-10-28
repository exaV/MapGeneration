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

package ch.fhnw.ether.render.gl;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

//FIXME: we should switch to a texture class which is independent of GL
// - should support different texture types (2d, 3d, array, cube map, etc) in an abstract way
// - gl renderer would wrap around these types, in combination with texture unit provided by material

/**
 * Very simple texture wrapper.
 *
 * @author radar
 */
public class Texture {
	private int[] tex;
	private int width;
	private int height;
	private Buffer buffer;
	private int format;
	
	public Texture() {
	}
	
	public Texture(URL url) {
		setData(url);
	}

	// FIXME: textures don't seem to be disposed!
	public void dispose(GL gl) {
		if (tex != null) {
			gl.glDeleteTextures(1, tex, 0);
			tex = null;
			buffer = null;
		}
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
	}

	private void load(GL gl) {
		if (buffer != null) {
			load(gl, width, height, buffer, format);
			buffer = null;
		}
	}

	private void load(GL gl, int width, int height, Buffer buffer, int format) {
		if (tex == null) {
			tex = new int[1];
			gl.glGenTextures(1, tex, 0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, tex[0]);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		} else {
			gl.glBindTexture(GL.GL_TEXTURE_2D, tex[0]);
		}
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		buffer.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, format, GL.GL_UNSIGNED_BYTE, buffer);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	public void enable(GL gl) {
		load(gl);
		if (tex != null)
			gl.glBindTexture(GL.GL_TEXTURE_2D, tex[0]);
	}

	public void disable(GL gl) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	@Override
	public String toString() {
		return "texture[valid=" + (tex != null) + " w=" + width + " h=" + height + "]";
	}
}
